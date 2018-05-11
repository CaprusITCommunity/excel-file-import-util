/*
 * copyright (C) reserved to CaprusIT (I) Pvt. Ltd. 2018 - 2018 All rights reserved.
 *
 * This Software is licensed under the CaprusIT private license version 1.0
 * any breach or unauthorized reutilization of this will be strictly prohibited and may leads to leagal issue.  
 */
package com.caprus.cupcake.service;

import java.util.Properties;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

// TODO: Auto-generated Javadoc
/**
 * The Class DBConfig.
 * @author - VijayaSaradhi R
 */
//@Configuration
public class DBConfig {

	/** The environment. */
	@Autowired
	Environment environment;

	/**
	 * Entity manager factory.
	 *
	 * @return the local container entity manager factory bean
	 */
	// @Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(dataSource());
		entityManager.setPackagesToScan("com");
		entityManager.setPersistenceProvider(new HibernatePersistenceProvider());
		entityManager.setJpaProperties(hibernateProperties());
		return entityManager;
	}

	/**
	 * Hibernate properties.
	 *
	 * @return the properties
	 */
	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		// we need to added this property to text file
		properties.put("hibernate.jdbc.batch_size", "20");
		return properties;
	}

	/**
	 * Data source.
	 *
	 * @return the driver manager data source
	 */
	public DriverManagerDataSource dataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
		driverManagerDataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
		driverManagerDataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
		driverManagerDataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
		return driverManagerDataSource;
	}

}
