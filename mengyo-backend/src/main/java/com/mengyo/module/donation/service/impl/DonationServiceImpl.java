package com.mengyo.module.donation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mengyo.common.exception.BusinessException;
import com.mengyo.module.donation.dto.MaterialDonationRequest;
import com.mengyo.module.donation.dto.MoneyDonationRequest;
import com.mengyo.module.donation.entity.DonationRecord;
import com.mengyo.module.donation.mapper.DonationRecordMapper;
import com.mengyo.module.donation.service.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 捐赠服务实现类
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationServiceImpl extends ServiceImpl<DonationRecordMapper, DonationRecord> implements DonationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createMoneyDonation(Long userId, MoneyDonationRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("捐赠金额必须大于0");
        }

        // 生成订单号
        String orderNo = "DN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);

        DonationRecord record = new DonationRecord();
        record.setDonorId(userId);
        record.setDonationType("MONEY");
        record.setAmount(request.getAmount());
        record.setTargetType(request.getTargetType());
        record.setTargetId(request.getTargetId());
        record.setOrderNo(orderNo);
        record.setPaymentMethod(request.getPaymentMethod());
        record.setPaymentStatus("PENDING");
        record.setIsAnonymous(request.getIsAnonymous());
        record.setMessage(request.getMessage());

        save(record);
        log.info("用户{}创建资金捐赠订单，订单号：{}", userId, orderNo);

        // TODO: 调用支付接口，获取支付参数
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", record.getId());
        result.put("orderNo", orderNo);
        result.put("amount", request.getAmount());
        result.put("paymentMethod", request.getPaymentMethod());
        // 这里应该返回真实的支付参数，如微信支付的prepay_id等
        result.put("paymentParams", "模拟支付参数");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMaterialDonation(Long userId, MaterialDonationRequest request) {
        if (request.getMaterialQuantity() == null || request.getMaterialQuantity() <= 0) {
            throw new BusinessException("物资数量必须大于0");
        }

        DonationRecord record = new DonationRecord();
        record.setDonorId(userId);
        record.setDonationType("MATERIAL");
        record.setMaterialName(request.getMaterialName());
        record.setMaterialQuantity(request.getMaterialQuantity());
        record.setMaterialUnit(request.getMaterialUnit());
        record.setTargetType(request.getTargetType());
        record.setTargetId(request.getTargetId());
        record.setPaymentStatus("SUCCESS"); // 物资捐赠直接标记为成功
        record.setPaymentTime(LocalDateTime.now());
        record.setIsAnonymous(request.getIsAnonymous());
        record.setMessage(request.getMessage());

        save(record);
        log.info("用户{}创建物资捐赠记录，ID：{}", userId, record.getId());

        return record.getId();
    }

    @Override
    public Page<DonationRecord> getDonationList(Integer page, Integer size, String donationType) {
        Page<DonationRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(donationType)) {
            wrapper.eq(DonationRecord::getDonationType, donationType);
        }

        // 只显示支付成功的记录
        wrapper.eq(DonationRecord::getPaymentStatus, "SUCCESS")
                .orderByDesc(DonationRecord::getCreateTime);

        return page(pageParam, wrapper);
    }

    @Override
    public List<DonationRecord> getMyDonations(Long userId) {
        LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DonationRecord::getDonorId, userId)
                .orderByDesc(DonationRecord::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Map<String, Object>> getDonationRanking(Integer limit) {
        // TODO: 实现捐赠榜单查询
        // 这里需要使用原生SQL或者自定义mapper方法来实现按金额排序的榜单
        log.info("查询捐赠榜单，限制：{}", limit);
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(String orderNo, String transactionId, String paymentStatus) {
        LambdaQueryWrapper<DonationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DonationRecord::getOrderNo, orderNo);
        DonationRecord record = getOne(wrapper);

        if (record == null) {
            log.error("支付回调失败，订单不存在：{}", orderNo);
            throw new BusinessException("订单不存在");
        }

        if (!"PENDING".equals(record.getPaymentStatus())) {
            log.warn("订单{}状态不是待支付，无需处理", orderNo);
            return;
        }

        record.setTransactionId(transactionId);
        record.setPaymentStatus(paymentStatus);
        if ("SUCCESS".equals(paymentStatus)) {
            record.setPaymentTime(LocalDateTime.now());
        }

        updateById(record);
        log.info("处理支付回调，订单号：{}，状态：{}", orderNo, paymentStatus);
    }
}
