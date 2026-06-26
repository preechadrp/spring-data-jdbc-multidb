/*
 * class นี้ถ้าเป็นฐานข้อมูลดังๆ เช่น mysql,mariadb,mssql,oracle,postgresql,h2,sqllite 
 * ไม่จำเป็นต้องมี แต่ในตัวอย่างนี้ใช้ Firebird เลยต้องใช้
 */
package com.example.config;

import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.dialect.JdbcH2Dialect;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

	@Override
	public JdbcDialect jdbcDialect(NamedParameterJdbcOperations operations) {
		// ขอยืม Dialect ของ H2 มาใช้แทน เนื่องจาก H2 สร้างคำสั่งระดับ ANSI SQL
		// และตัวคลาสสืบทอด interface JdbcDialect มาให้พร้อมใช้งานแล้ว ซึ่งเหมาะกับ firebird
		return JdbcH2Dialect.INSTANCE;
	}

	// 🌟 เพิ่มเมธอดนี้เข้ามาเพื่อเข้าไปแก้ไขการตั้งค่า Mapping 🌟
	@Override
	public JdbcMappingContext jdbcMappingContext(
			Optional<NamingStrategy> namingStrategy,
			JdbcCustomConversions customConversions,
			RelationalManagedTypes jdbcManagedTypes) {

		// ดึงเอา Context เดิมที่ Spring สร้างให้มาปรับแต่งต่อ
		JdbcMappingContext context = super.jdbcMappingContext(namingStrategy, customConversions, jdbcManagedTypes);

		// สั่งปิดการใส่ " (Double Quote) อัตโนมัติ ทำให้ sql ที่ gen ไม่มี Double Quote ครอบชื่อฟิลด์ 
		/*
		สาเหตุที่มันมี Double Quotes (") ครอบ
		ตั้งแต่ Spring Data JDBC เวอร์ชันใหม่ๆ เป็นต้นมา ทีมพัฒนาได้เปิดโหมดที่เรียกว่า Force Quote เป็น true ไว้เป็นค่าเริ่มต้น 
		สาเหตุเพราะเขาต้องการป้องกันไม่ให้ชื่อ Table หรือ Column ของเราไปซ้ำกับ "คำสงวน" (Reserved Words) 
		ของ Database เช่น ถ้าคุณตั้งชื่อฟิลด์ว่า order หรือ select ระบบจะได้ไม่พังเพราะมันจะส่งไปเป็น "order" ให้แทน
		แต่พอมาเจอกับ Firebird การส่ง "name" (ตัวเล็ก) ไป จะทำให้ Database หาคอลัมน์ไม่เจอ เพราะปกติ Firebird จะเก็บชื่อคอลัมน์เป็นตัวใหญ่ (UPPERCASE) ทั้งหมดครับ
		 */
		//context.setForceQuote(false);

		return context;
	}

}