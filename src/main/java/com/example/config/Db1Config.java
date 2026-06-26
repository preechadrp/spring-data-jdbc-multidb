package com.example.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

@Configuration
@EnableJdbcRepositories(basePackages = "com.example.db1.repository", // 🎯 ชี้เป้าโฟลเดอร์
		jdbcAggregateOperationsRef = "db1JdbcAggregateTemplate", // 🌟 แก้ไข: เปลี่ยนชื่อ Ref มาใช้ตัวใหม่
		transactionManagerRef = "db1TransactionManager")
public class Db1Config {

	@Primary /*ต้องใส่ตัวแรกเสมอ*/
	@Bean(name = "db1DataSource")
	@ConfigurationProperties(prefix = "db1")
	DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary /*ต้องใส่ตัวแรกเสมอ*/
	@Bean(name = "db1JdbcOperations")
	NamedParameterJdbcOperations jdbcOperations(@Qualifier("db1DataSource") DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Primary /*ต้องใส่ตัวแรกเสมอ*/
	@Bean(name = "db1TransactionManager")
	PlatformTransactionManager transactionManager(@Qualifier("db1DataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Primary /*ต้องใส่ตัวแรกเสมอ*/
	@Bean(name = "db1JdbcAggregateTemplate")
	JdbcAggregateTemplate dbJdbcAggregateTemplate(
			ApplicationContext applicationContext,
			JdbcMappingContext mappingContext,
			JdbcConverter jdbcConverter,
			@Qualifier("db1JdbcOperations") NamedParameterJdbcOperations jdbcOperations) {

		Dialect dialect = JdbcH2Dialect.INSTANCE;

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