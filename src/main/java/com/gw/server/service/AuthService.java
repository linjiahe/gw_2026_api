package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gw.server.entity.NonceRecord;
import com.gw.server.entity.User;
import com.gw.server.exception.BusinessException;
import com.gw.server.mapper.NonceRecordMapper;
import com.gw.server.mapper.UserMapper;
import com.gw.server.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private static final String INVITE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 8;

    private final UserMapper userMapper;
    private final NonceRecordMapper nonceRecordMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserMapper userMapper, NonceRecordMapper nonceRecordMapper, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.nonceRecordMapper = nonceRecordMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Generate nonce for wallet address (auto-register if new user)
     */
    @Transactional
    public String generateNonce(String address) {
        String normalizedAddress = address.toLowerCase();

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWalletAddress, normalizedAddress)
        );

        boolean isNewUser = false;
        if (user == null) {
            user = new User();
            user.setWalletAddress(normalizedAddress);
            user.setStatus(1);
            user.setInviteCode(generateInviteCode());
            userMapper.insert(user);
            isNewUser = true;
            log.info("New user registered: {}", normalizedAddress);
        }

        // Check user status
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // Generate nonce
        String nonce = UUID.randomUUID().toString();

        // Save nonce to user table
        user.setNonce(nonce);
        userMapper.updateById(user);

        // Save nonce record for replay protection
        NonceRecord record = new NonceRecord();
        record.setWalletAddress(normalizedAddress);
        record.setNonce(nonce);
        record.setUsed(0);
        nonceRecordMapper.insert(record);

        log.debug("Generated nonce for {}: {}", normalizedAddress, nonce);
        return nonce;
    }

    /**
     * Verify signature and login
     */
    @Transactional
    public LoginResult login(String address, String signature, String inviteCode) {
        String normalizedAddress = address.toLowerCase();

        // 1. Find user
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWalletAddress, normalizedAddress)
        );
        if (user == null || user.getNonce() == null) {
            throw new BusinessException(401, "请先获取 nonce");
        }

        // 2. Check nonce not already used
        NonceRecord nonceRecord = nonceRecordMapper.selectOne(
                new LambdaQueryWrapper<NonceRecord>()
                        .eq(NonceRecord::getNonce, user.getNonce())
                        .eq(NonceRecord::getWalletAddress, normalizedAddress)
                        .orderByDesc(NonceRecord::getCreatedAt)
                        .last("LIMIT 1")
        );
        if (nonceRecord == null) {
            throw new BusinessException(401, "nonce 不存在");
        }
        if (nonceRecord.getUsed() == 1) {
            throw new BusinessException(401, "nonce 已使用，请重新获取");
        }

        // 3. Verify signature
        String recoveredAddress = recoverAddress(user.getNonce(), signature);
        if (!recoveredAddress.equalsIgnoreCase(normalizedAddress)) {
            log.warn("Signature verification failed: expected={}, recovered={}", normalizedAddress, recoveredAddress);
            throw new BusinessException(401, "签名验证失败");
        }

        // 4. Mark nonce as used
        nonceRecord.setUsed(1);
        nonceRecordMapper.updateById(nonceRecord);

        // 5. Generate new nonce for next login (prevent reuse)
        String newNonce = UUID.randomUUID().toString();
        user.setNonce(newNonce);

        // 6. Check if new user (last_login_at is null)
        boolean isNewUser = user.getLastLoginAt() == null;

        // 7. Bind invite code (only on first login)
        if (isNewUser && inviteCode != null && !inviteCode.trim().isEmpty()) {
            User inviter = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getInviteCode, inviteCode.trim())
            );
            if (inviter == null) {
                throw new BusinessException("邀请码无效");
            }
            if (inviter.getId().equals(user.getId())) {
                throw new BusinessException("不能使用自己的邀请码");
            }
            user.setInvitedBy(inviter.getId());
            log.info("User {} invited by {} (code: {})", normalizedAddress, inviter.getWalletAddress(), inviteCode);
        }

        // 8. Update last_login_at and nonce
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        // 9. Generate JWT
        String token = jwtTokenProvider.generateToken(user.getId(), user.getWalletAddress());

        log.info("User logged in: {}, isNewUser: {}", normalizedAddress, isNewUser);
        return new LoginResult(token, user.getId(), normalizedAddress, isNewUser, user.getInviteCode());
    }

    /**
     * Recover signer address from personal_sign signature
     */
    private String recoverAddress(String nonce, String signatureHex) {
        // MetaMask personal_sign prepends: "\u0019Ethereum Signed Message:\n" + message.length + message
        String message = "Sign this nonce to login: " + nonce;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // Build prefixed message hash (EIP-191 personal_sign)
        byte[] prefix = ("\u0019Ethereum Signed Message:\n" + messageBytes.length).getBytes(StandardCharsets.UTF_8);
        byte[] concatenated = new byte[prefix.length + messageBytes.length];
        System.arraycopy(prefix, 0, concatenated, 0, prefix.length);
        System.arraycopy(messageBytes, 0, concatenated, prefix.length, messageBytes.length);

        byte[] messageHash = Hash.sha3(concatenated);

        // Parse signature
        byte[] signatureBytes = Numeric.hexStringToByteArray(signatureHex);
        if (signatureBytes.length < 65) {
            throw new BusinessException(401, "签名格式不正确");
        }

        // Extract v, r, s
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);

        ECDSASignature ecdsaSignature = new ECDSASignature(
                new BigInteger(1, r),
                new BigInteger(1, s)
        );

        // Recover public key
        BigInteger recoveredKey = Sign.recoverFromSignature(v - 27, ecdsaSignature, messageHash);
        if (recoveredKey == null) {
            throw new BusinessException(401, "签名恢复失败");
        }

        // Derive address from public key
        return "0x" + Keys.getAddress(recoveredKey);
    }

    /**
     * Generate a random invite code (8 chars, no ambiguous chars like 0/O/1/I)
     */
    private String generateInviteCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            sb.append(INVITE_CHARS.charAt(random.nextInt(INVITE_CHARS.length())));
        }
        return sb.toString();
    }

    public static class LoginResult {
        private final String token;
        private final Integer userId;
        private final String address;
        private final boolean isNewUser;
        private final String inviteCode;

        public LoginResult(String token, Integer userId, String address, boolean isNewUser, String inviteCode) {
            this.token = token;
            this.userId = userId;
            this.address = address;
            this.isNewUser = isNewUser;
            this.inviteCode = inviteCode;
        }

        public String getToken() { return token; }
        public Integer getUserId() { return userId; }
        public String getAddress() { return address; }
        public boolean isNewUser() { return isNewUser; }
        public String getInviteCode() { return inviteCode; }
    }
}
