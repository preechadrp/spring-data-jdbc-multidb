package com.example.model;


import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("PRODUCT") // ระบุชื่อตาราง (ถ้าชื่อเดียวกับ Class ไม่ต้องใส่ก็ได้)
public record Product(
		@Id Long id,

		@Column("NAME") // บอก Spring Data JDBC ว่าฟิลด์นี้คือ name ใน Database
		String name,

		BigDecimal price) {
	// ใช้ Java Record จะช่วยให้โค้ดคลีนขึ้นมาก ไม่ต้องเขียน Getter/Setter/ToString

	// สร้าง Wither Method สำหรับอัปเดตข้อมูล (เนื่องจาก Record เป็น Immutable)
	public Product withId(Long id) {
		return new Product(id, this.name, this.price);
	}
}