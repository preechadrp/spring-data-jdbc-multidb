package com.example.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.convert.InsertStrategyFactory;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.QueryMappingConfiguration;
import org.springframework.data.jdbc.core.convert.SqlGeneratorSource;
import org.springframework.data.jdbc.core.convert.SqlParametersFactory;
import org.springframework.data.jdbc.core.dialect.JdbcH2Dialect;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJdbcRepositories(basePackages = "com.example.db2.repository", // 🎯 ชี้เป้าโฟลเดอร์
		jdbcAggregateOperationsRef = "db2JdbcAggregateTemplate", // 🌟 แก้ไข: เปลี่ยนชื่อ Ref มาใช้ตัวใหม่
		transactionManagerRef = "db2TransactionManager")
public class Db2Config {

	@Bean(name = "db2DataSource")
	@ConfigurationProperties(prefix = "db2")
	HikariDataSource dataSource() {
		return new HikariDataSource();
	}

	@Bean(name = "db2JdbcOperations")
	NamedParameterJdbcOperations jdbcOperations(@Qualifier("db2DataSource") DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean(name = "db2TransactionManager")
	PlatformTransactionManager transactionManager(@Qualifier("db2DataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "db2JdbcAggregateTemplate")
	JdbcAggregateTemplate dbJdbcAggregateTemplate(
			ApplicationContext applicationContext,
			JdbcMappingContext mappingContext,
			JdbcConverter jdbcConverter,
			@Qualifier("db2JdbcOperations") NamedParameterJdbcOperations jdbcOperations) {

		Dialect dialect = JdbcH2Dialect.INSTANCE;
		// เปลี่ยนบรรทัด Dialect เป็นแบบนี้ถ้าใช้ฐานข้อมูลที่ spring รู้จัก เช่น mariadb,mysql,postgresql เป็นต้น
		//Dialect dialect = DialectResolver.getDialect(jdbcOperations.getJdbcOperations());

		DataAccessStrategy dataAccessStrategy = new DefaultDataAccessStrategy(
				new SqlGeneratorSource(mappingContext, jdbcConverter, dialect),
				mappingContext,
				jdbcConverter,
				jdbcOperations,
				new SqlParametersFactory(mappingContext, jdbcConverter),
				new InsertStrategyFactory(jdbcOperations, dialect),
				QueryMappingConfiguration.EMPTY);

		return new JdbcAggregateTemplate(applicationContext, mappingContext, jdbcConverter, dataAccessStrategy);
	}
}