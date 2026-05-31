package com.example.db1.repository;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.dto.ProductSummary;
import com.example.model.Product;

public interface ProductDb1Repository extends CrudRepository<Product, Long> {

	// 1. Spring Data จะเดา SQL ให้จากชื่อ Method (Query Method)
	List<Product> findByNameContaining(String keyword);

	// 2. เขียน SQL เองแบบตรงไปตรงมาด้วย @Query
	@Query("SELECT * FROM product WHERE price > :minPrice")
	List<Product> findExpensiveProducts(@Param("minPrice") BigDecimal minPrice);

	// 3. ถ้าเป็นคำสั่ง Update/Delete ที่เขียน SQL เอง ต้องใส่ @Modifying ด้วย
	@Modifying
	@Query("UPDATE product SET price = :newPrice WHERE id = :id")
	boolean updatePrice(@Param("id") Long id, @Param("newPrice") BigDecimal newPrice);

	// Spring Data JDBC จะนำผลลัพธ์จาก SQL ไปใส่ ProductSummary ให้เอง
	// โดยดูจากชื่อ Column ที่ตรงกับชื่อตัวแปรใน Record
	@Query("SELECT id, name FROM product")
	List<ProductSummary> findAllProductSummaries();
}