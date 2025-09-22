package com.develop.domain.entity.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "receipt")
public class Receipt {

    @Id
    @Column(name = "receipt_code")
    private String receiptCode;

    @Column(name = "user_id")
    private String userId;

    private String payment;

    @Column(name = "pay_id")
    private String payId;

    @Column(name = "total_price")
    private Long totalPrice;

    private Boolean status;

    @Column(name = "receipt_desc")
    private String receiptDesc;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    @Builder
    public Receipt(String receiptCode, String userId, String payment, String payId, String receiptDesc, Long totalPrice) {
        this.receiptCode = receiptCode;
        this.userId = userId;
        this.payment = payment;
        this.payId = payId;
        this.receiptDesc = receiptDesc;
        this.totalPrice = totalPrice;
    }
}
