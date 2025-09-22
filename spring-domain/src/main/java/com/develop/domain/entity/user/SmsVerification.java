package com.develop.domain.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "sms_verification")
public class SmsVerification {

    @Id
    private String phone;

    @Column(name = "verification_type")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SmsType.VerificationType verificationType;

    private String code;

    private String name;

    private Boolean state;

    @Column(name = "verification_time")
    private LocalDateTime verificationTime;

    public void setVerificationType(SmsType.VerificationType verificationType) {
        this.verificationType = verificationType;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public void setVerificationTime(LocalDateTime verificationTime) {
        this.verificationTime = verificationTime;
    }

    @Builder
    public SmsVerification(String phone, String name, SmsType.VerificationType verificationType, String code) {
        this.phone = phone;
        this.name = name;
        this.verificationType = verificationType;
        this.code = code;
    }
}
