package com.gw.server.controller;

import com.gw.server.dto.*;
import com.gw.server.entity.NftRecord;
import com.gw.server.entity.PresaleRecord;
import com.gw.server.service.NftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    @ApiOperation(value = "创建预售记录", notes = "需在Header中携带Bearer Token。创建一条预售购买记录，需指定NFT等级ID和购买数量。")
    @PostMapping("/presale/create")
    public Result<Void> createPresaleRecord(@Valid @RequestBody CreatePresaleRequest request,
                                            HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        nftService.createPresaleRecord(address, request.getNftLevelId(), request.getQuantity());
        return Result.ok();
    }

    // ========== 2. 创建NFT记录 ==========
    @ApiOperation(value = "创建NFT记录", notes = "需在Header中携带Bearer Token。创建一条NFT购买记录，需指定NFT等级ID和购买数量。")
    @PostMapping("/record/create")
    public Result<Void> createNftRecord(@Valid @RequestBody CreateNftRecordRequest request,
                                        HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        nftService.createNftRecord(address, request.getNftLevelId(), request.getQuantity());
        return Result.ok();
    }

    // ========== 3. 预售地址随机获取 ==========
    @ApiOperation(value = "获取随机预售收款地址", notes = "公开接口。从预售地址池中随机返回一个收款钱包地址，用于前端展示打款目标。")
    @GetMapping("/presale/address")
    public Result<Map<String, String>> getRandomPresaleAddress() {
        String address = nftService.getRandomPresaleAddress();
        return Result.ok(Collections.singletonMap("address", address));
    }

    // ========== 4. NFT地址随机获取 ==========
    @ApiOperation(value = "获取随机NFT收款地址", notes = "公开接口。从NFT地址池中随机返回一个收款钱包地址，用于前端展示打款目标。")
    @GetMapping("/address")
    public Result<Map<String, String>> getRandomNftAddress() {
        String address = nftService.getRandomNftAddress();
        return Result.ok(Collections.singletonMap("address", address));
    }

    // ========== 5. 预售记录全网前20 ==========
    @ApiOperation(value = "预售记录全网前20", notes = "公开接口。返回全网最新的20条预售购买记录，按时间倒序。")
    @GetMapping("/presale/records")
    public Result<List<PresaleRecord>> getPresaleRecords() {
        return Result.ok(nftService.getPresaleRecords());
    }

    // ========== 6. NFT记录全网前20 ==========
    @ApiOperation(value = "NFT记录全网前20", notes = "公开接口。返回全网最新的20条NFT购买记录，按时间倒序。")
    @GetMapping("/records")
    public Result<List<NftRecord>> getNftRecords() {
        return Result.ok(nftService.getNftRecords());
    }

    // ========== 7. 我的预售记录（分页） ==========
    @ApiOperation(value = "我的预售记录（分页）", notes = "需在Header中携带Bearer Token。分页查询当前用户的预售购买记录。")
    @GetMapping("/presale/my")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页条数", defaultValue = "10", dataType = "long", paramType = "query")
    })
    public Result<PageResult<PresaleRecord>> getMyPresaleRecords(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(nftService.getMyPresaleRecords(address, page, size));
    }

    // ========== 8. 我的NFT记录（分页） ==========
    @ApiOperation(value = "我的NFT记录（分页）", notes = "需在Header中携带Bearer Token。分页查询当前用户的NFT购买记录。")
    @GetMapping("/my")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页条数", defaultValue = "10", dataType = "long", paramType = "query")
    })
    public Result<PageResult<NftRecord>> getMyNftRecords(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpRequest) {
        String address = (String) httpRequest.getAttribute("walletAddress");
        return Result.ok(nftService.getMyNftRecords(address, page, size));
    }
}
