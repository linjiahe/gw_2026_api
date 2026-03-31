package com.gw.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gw.server.dto.AddressNftDetailResponse;
import com.gw.server.dto.TeamResponse;
import com.gw.server.entity.NftRecord;
import com.gw.server.entity.PresaleRecord;
import com.gw.server.entity.User;
import com.gw.server.exception.BusinessException;
import com.gw.server.mapper.NftRecordMapper;
import com.gw.server.mapper.PresaleRecordMapper;
import com.gw.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final NftRecordMapper nftRecordMapper;
    private final PresaleRecordMapper presaleRecordMapper;

    public UserService(UserMapper userMapper, NftRecordMapper nftRecordMapper, PresaleRecordMapper presaleRecordMapper) {
        this.userMapper = userMapper;
        this.nftRecordMapper = nftRecordMapper;
        this.presaleRecordMapper = presaleRecordMapper;
    }

    public User getUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    public User getUserByAddress(String address) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWalletAddress, address.toLowerCase())
        );
    }

    /**
     * Get team info for a given user
     */
    public TeamResponse getTeamInfo(User user) {
        // 1. Direct referrals (invited_by = user.id)
        List<User> directUsers = userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getInvitedBy, user.getId())
        );
        List<String> directAddresses = directUsers.stream()
                .map(User::getWalletAddress)
                .collect(Collectors.toList());

        // 2. Full team (BFS through invited_by)
        Set<Integer> teamUserIds = new HashSet<>();
        Set<String> teamAddresses = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(user.getId());

        while (!queue.isEmpty()) {
            Integer currentId = queue.poll();
            if (teamUserIds.contains(currentId)) continue;
            teamUserIds.add(currentId);

            List<User> subUsers = userMapper.selectList(
                    new LambdaQueryWrapper<User>().eq(User::getInvitedBy, currentId)
            );
            for (User sub : subUsers) {
                teamAddresses.add(sub.getWalletAddress());
                if (!teamUserIds.contains(sub.getId())) {
                    queue.add(sub.getId());
                }
            }
        }

        // 3. NFT counts
        int directNftCount = sumNftQuantity(directAddresses);
        int teamNftCount = sumNftQuantity(new ArrayList<>(teamAddresses));

        return TeamResponse.builder()
                .addresses(directAddresses)
                .teamCount(teamAddresses.size())
                .directCount(directUsers.size())
                .teamNftCount(teamNftCount)
                .directNftCount(directNftCount)
                .build();
    }

    /**
     * Sum nft_record.quantity + presale_record.quantity for given addresses
     */
    private int sumNftQuantity(List<String> addresses) {
        if (addresses.isEmpty()) return 0;

        // NFT records
        List<NftRecord> nftRecords = nftRecordMapper.selectList(
                new LambdaQueryWrapper<NftRecord>().in(NftRecord::getWalletAddress, addresses)
        );
        int nftSum = nftRecords.stream().mapToInt(r -> r.getQuantity() != null ? r.getQuantity() : 0).sum();

        // Presale records
        List<PresaleRecord> presaleRecords = presaleRecordMapper.selectList(
                new LambdaQueryWrapper<PresaleRecord>().in(PresaleRecord::getWalletAddress, addresses)
        );
        int presaleSum = presaleRecords.stream().mapToInt(r -> r.getQuantity() != null ? r.getQuantity() : 0).sum();

        return nftSum + presaleSum;
    }

    /**
     * Get NFT and presale records for a given address
     */
    public AddressNftDetailResponse getAddressNftDetail(String address) {
        String normalized = address.toLowerCase();
        List<NftRecord> nftRecords = nftRecordMapper.selectList(
                new LambdaQueryWrapper<NftRecord>()
                        .eq(NftRecord::getWalletAddress, normalized)
                        .orderByDesc(NftRecord::getCreatedAt)
        );
        List<PresaleRecord> presaleRecords = presaleRecordMapper.selectList(
                new LambdaQueryWrapper<PresaleRecord>()
                        .eq(PresaleRecord::getWalletAddress, normalized)
                        .orderByDesc(PresaleRecord::getCreatedAt)
        );
        return AddressNftDetailResponse.builder()
                .nftRecords(nftRecords)
                .presaleRecords(presaleRecords)
                .build();
    }

    /**
     * Get direct subordinates for a given address
     */
    public List<User> getSubordinates(String address) {
        User user = getUserByAddress(address);
        if (user == null) return Collections.emptyList();
        return userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getInvitedBy, user.getId())
                        .orderByDesc(User::getCreatedAt)
        );
    }
}
