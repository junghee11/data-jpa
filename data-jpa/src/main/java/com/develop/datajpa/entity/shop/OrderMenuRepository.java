package com.develop.datajpa.entity.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

    List<OrderMenu> findByReceiptCode(String receiptCode);

    @Query(value = """
        SELECT receipt_code,
               STRING_AGG(good_code, ', ') as good_codes,
               ARRAY_AGG(img_url) as img_urls,
               COUNT(*) as count
        FROM order_menu 
        GROUP BY receipt_code
        """, nativeQuery = true)
    List<Object[]> getOrderSummaryNative();

}
