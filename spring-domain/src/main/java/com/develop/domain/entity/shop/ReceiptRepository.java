package com.develop.domain.entity.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptCode(String code);

    List<Receipt> findByUserIdAndStatus(String userId, boolean status);

}
