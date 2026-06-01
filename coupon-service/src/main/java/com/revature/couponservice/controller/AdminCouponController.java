package com.revature.couponservice.controller;

import com.revature.couponservice.entity.Coupon;
import com.revature.couponservice.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/coupons")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCouponStats() {
        List<Coupon> allCoupons = couponService.getAllCoupons();
        List<Coupon> activeCoupons = couponService.getActiveCoupons();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCoupons", allCoupons.size());
        stats.put("activeCoupons", activeCoupons.size());
        stats.put("inactiveCoupons", allCoupons.size() - activeCoupons.size());

        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Coupon> toggleCouponStatus(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getCouponById(id);
            coupon.setIsActive(!coupon.getIsActive());
            Coupon updated = couponService.updateCoupon(id, coupon);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Admin Coupon Service");
        return ResponseEntity.ok(health);
    }
}
