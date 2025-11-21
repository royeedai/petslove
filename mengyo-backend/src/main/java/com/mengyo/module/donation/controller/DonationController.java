package com.mengyo.module.donation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mengyo.common.result.Result;
import com.mengyo.module.donation.dto.MaterialDonationRequest;
import com.mengyo.module.donation.dto.MoneyDonationRequest;
import com.mengyo.module.donation.entity.DonationRecord;
import com.mengyo.module.donation.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 捐赠管理控制器
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Tag(name = "捐赠管理")
@RestController
@RequestMapping("/api/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @Operation(summary = "创建资金捐赠订单")
    @PostMapping("/money")
    public Result<Map<String, Object>> createMoneyDonation(@RequestHeader("userId") Long userId,
                                                           @RequestBody MoneyDonationRequest request) {
        Map<String, Object> result = donationService.createMoneyDonation(userId, request);
        return Result.success(result);
    }

    @Operation(summary = "创建物资捐赠记录")
    @PostMapping("/material")
    public Result<Long> createMaterialDonation(@RequestHeader("userId") Long userId,
                                               @RequestBody MaterialDonationRequest request) {
        Long donationId = donationService.createMaterialDonation(userId, request);
        return Result.success(donationId);
    }

    @Operation(summary = "获取捐赠记录列表")
    @GetMapping("/list")
    public Result<Page<DonationRecord>> getDonationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String donationType) {
        Page<DonationRecord> result = donationService.getDonationList(page, size, donationType);
        return Result.success(result);
    }

    @Operation(summary = "获取我的捐赠记录")
    @GetMapping("/my-donations")
    public Result<List<DonationRecord>> getMyDonations(@RequestHeader("userId") Long userId) {
        List<DonationRecord> donations = donationService.getMyDonations(userId);
        return Result.success(donations);
    }

    @Operation(summary = "获取捐赠榜单")
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getDonationRanking(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> ranking = donationService.getDonationRanking(limit);
        return Result.success(ranking);
    }

    @Operation(summary = "支付回调接口")
    @PostMapping("/callback")
    public Result<Void> paymentCallback(@RequestParam String orderNo,
                                        @RequestParam String transactionId,
                                        @RequestParam String paymentStatus) {
        donationService.handlePaymentCallback(orderNo, transactionId, paymentStatus);
        return Result.success();
    }
}
