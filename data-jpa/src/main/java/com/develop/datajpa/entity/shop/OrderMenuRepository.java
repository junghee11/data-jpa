package com.develop.datajpa.entity.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

    List<OrderMenu> findByReceiptCode(String receiptCode);

}
