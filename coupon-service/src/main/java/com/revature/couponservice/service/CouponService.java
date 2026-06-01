package com.revature.couponservice.service;

import com.revature.couponservice.dto.CouponValidationResponse;
import com.revature.couponservice.dto.ValidateCouponRequest;
import com.revature.couponservice.entity.Coupon;
import com.revature.couponservice.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public List<Coupon> getActiveCoupons() {
        return couponRepository.findByIsActiveTrue();
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
        Coupon existing = getCouponById(id);

        existing.setCode(updatedCoupon.getCode());
        existing.setDescription(updatedCoupon.getDescription());
        existing.setDiscountType(updatedCoupon.getDiscountType());
        existing.setDiscountValue(updatedCoupon.getDiscountValue());
        existing.setMinOrderValue(updatedCoupon.getMinOrderValue());
        existing.setMaxDiscount(updatedCoupon.getMaxDiscount());
        existing.setValidFrom(updatedCoupon.getValidFrom());
        existing.setValidUntil(updatedCoupon.getValidUntil());
        existing.setIsActive(updatedCoupon.getIsActive());

        return couponRepository.save(existing);
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public CouponValidationResponse validateCoupon(ValidateCouponRequest request) {
        CouponValidationResponse response = new CouponValidationResponse();
        response.setCouponCode(request.getCode());

        try {
            Coupon coupon = getCouponByCode(request.getCode());

            // Check if active
            if (!Boolean.TRUE.equals(coupon.getIsActive())) {
                response.setIsValid(false);
                response.setMessage("Coupon is not active");
                return response;
            }

            // Check validity dates
            LocalDateTime now = LocalDateTime.now();
            if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
                response.setIsValid(false);
                response.setMessage("Coupon is not yet valid");
                return response;
            }
            if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
                response.setIsValid(false);
                response.setMessage("Coupon has expired");
                return response;
            }

            // Check minimum order amount
            if (coupon.getMinOrderValue() != null
                    && request.getOrderAmount().compareTo(coupon.getMinOrderValue()) < 0) {
                response.setIsValid(false);
                response.setMessage("Minimum order amount not met");
                return response;
            }

            // Calculate discount
            BigDecimal discount;
            if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                discount = request.getOrderAmount().multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
                if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                    discount = coupon.getMaxDiscount();
                }
            } else {
                discount = coupon.getDiscountValue();
            }

            response.setIsValid(true);
            response.setMessage("Coupon is valid");
            response.setDiscountAmount(discount);

        } catch (RuntimeException e) {
            response.setIsValid(false);
            response.setMessage("Invalid coupon code");
        }

        return response;
    }
}
