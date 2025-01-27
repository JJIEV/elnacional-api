package com.api.elnacional.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class MysqlConfig {

    @Autowired
    private Environment env;


    @Bean
    public DataSource elNacionalDataSource() throws NamingException {
        if (Arrays.asList(env.getActiveProfiles()).contains("DEV")) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(env.getProperty("spring.datasource.url"));
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
            dataSource.setPassword(env.getProperty("spring.datasource.password"));
            dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            return dataSource;
        } else {
            JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
            bean.setJndiName(env.getProperty("spring.datasource.jndi-name"));
            bean.setProxyInterface(DataSource.class);
            bean.setLookupOnStartup(false);
            bean.afterPropertiesSet();
            return (DataSource) bean.getObject();
        }
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean elNacionalEntityManager() throws NamingException {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(elNacionalDataSource());

        em.setPackagesToScan("com.api.elnacional.domain");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));

        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager elNacionalTransactionManager() throws NamingException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(elNacionalEntityManager().getObject());
        return transactionManager;
    }

    @Configuration
    @EnableJpaRepositories(
            basePackages = {"com.api.elnacional.repository"},
            entityManagerFactoryRef = "elNacionalEntityManager",
            transactionManagerRef = "elNacionalTransactionManager"
    )

    static class elNacionalRepositoriesConfig {}

}