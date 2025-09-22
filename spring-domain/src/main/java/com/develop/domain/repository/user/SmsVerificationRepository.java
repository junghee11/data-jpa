package com.develop.domain.repository.user;

import com.develop.domain.entity.user.SmsVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsVerificationRepository extends JpaRepository<SmsVerification, Long> {

    SmsVerification findByPhone(String phone);

    Optional<SmsVerification> findByPhoneAndName(String phone, String name);

}
