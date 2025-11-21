package com.mengyo.module.donation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mengyo.module.donation.dto.MaterialDonationRequest;
import com.mengyo.module.donation.dto.MoneyDonationRequest;
import com.mengyo.module.donation.entity.DonationRecord;

import java.util.List;
import java.util.Map;

/**
 * 捐赠服务接口
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
public interface DonationService extends IService<DonationRecord> {

    /**
     * 创建资金捐赠订单
     *
     * @param userId  捐赠人ID
     * @param request 捐赠信息
     * @return 订单信息（包含支付参数）
     */
    Map<String, Object> createMoneyDonation(Long userId, MoneyDonationRequest request);

    /**
     * 创建物资捐赠记录
     *
     * @param userId  捐赠人ID
     * @param request 捐赠信息
     * @return 捐赠记录ID
     */
    Long createMaterialDonation(Long userId, MaterialDonationRequest request);

    /**
     * 分页查询捐赠记录
     *
     * @param page         当前页
     * @param size         每页数量
     * @param donationType 捐赠类型（可选）
     * @return 分页结果
     */
    Page<DonationRecord> getDonationList(Integer page, Integer size, String donationType);

    /**
     * 获取我的捐赠记录
     *
     * @param userId 用户ID
     * @return 捐赠记录列表
     */
    List<DonationRecord> getMyDonations(Long userId);

    /**
     * 获取捐赠榜单
     *
     * @param limit 榜单数量
     * @return 榜单列表
     */
    List<Map<String, Object>> getDonationRanking(Integer limit);

    /**
     * 支付回调处理
     *
     * @param orderNo       订单号
     * @param transactionId 第三方交易号
     * @param paymentStatus 支付状态
     */
    void handlePaymentCallback(String orderNo, String transactionId, String paymentStatus);
}
