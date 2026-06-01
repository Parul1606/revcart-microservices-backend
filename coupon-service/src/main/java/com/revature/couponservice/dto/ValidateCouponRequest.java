package com.revature.couponservice.dto;

import java.math.BigDecimal;

public class ValidateCouponRequest {
    private String code;
    private BigDecimal orderAmount;

    public ValidateCouponRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }
}
