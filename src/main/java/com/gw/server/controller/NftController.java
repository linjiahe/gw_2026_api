package com.gw.server.controller;

import com.gw.server.dto.*;
import com.gw.server.entity.NftRecord;
import com.gw.server.entity.PresaleRecord;
import com.gw.server.service.NftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Api(tags = "NFT接口")
@RestController
@RequestMapping("/api/nft")
public class NftController {

    private final NftService nftService;

    public NftController(NftService nftService) {
        this.nftService = nftService;
    }

    // ========== 1. 创建预售记录 ==========
    @ApiOperation("创建预售记录")
    @PostMapping("/presale/create")
    public Result<Void> createPresaleRecord(@Valid @RequestBody CreatePresaleRequest request,
                                            HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        nftService.createPresaleRecord(address, request.getNftLevelId(), request.getQuantity());
        return Result.ok();
    }

    // ========== 2. 创建NFT记录 ==========
    @ApiOperation("创建NFT记录")
    @PostMapping("/record/create")
    public Result<Void> createNftRecord(@Valid @RequestBody CreateNftRecordRequest request,
                                        HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        nftService.createNftRecord(address, request.getNftLevelId(), request.getQuantity());
        return Result.ok();
    }

    // ========== 3. 预售地址随机获取 ==========
    @ApiOperation("预售地址随机获取")
    @GetMapping("/presale/address")
    public Result<Map<String, String>> getRandomPresaleAddress() {
        String address = nftService.getRandomPresaleAddress();
        return Result.ok(Collections.singletonMap("address", address));
    }

    // ========== 4. NFT地址随机获取 ==========
    @ApiOperation("NFT地址随机获取")
    @GetMapping("/address")
    public Result<Map<String, String>> getRandomNftAddress() {
        String address = nftService.getRandomNftAddress();
        return Result.ok(Collections.singletonMap("address", address));
    }

    // ========== 5. 预售记录全网前20 ==========
    @ApiOperation("预售记录全网前20")
    @GetMapping("/presale/records")
    public Result<List<PresaleRecord>> getPresaleRecords() {
        return Result.ok(nftService.getPresaleRecords());
    }

    // ========== 6. NFT记录全网前20 ==========
    @ApiOperation("NFT记录全网前20")
    @GetMapping("/records")
    public Result<List<NftRecord>> getNftRecords() {
        return Result.ok(nftService.getNftRecords());
    }

    // ========== 7. 我的预售记录（分页） ==========
    @ApiOperation("我的预售记录")
    @GetMapping("/presale/my")
    public Result<PageResult<PresaleRecord>> getMyPresaleRecords(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(nftService.getMyPresaleRecords(address, page, size));
    }

    // ========== 8. 我的NFT记录（分页） ==========
    @ApiOperation("我的NFT记录")
    @GetMapping("/my")
    public Result<PageResult<NftRecord>> getMyNftRecords(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(nftService.getMyNftRecords(address, page, size));
    }
}
