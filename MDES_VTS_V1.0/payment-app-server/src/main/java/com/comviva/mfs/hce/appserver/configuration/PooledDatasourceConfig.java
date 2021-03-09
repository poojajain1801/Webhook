package com.comviva.mfs.hce.appserver.configuration;

import com.comviva.decryption.DecryptDataKeyStore;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * this class had @ConfigurationProperties(prefix = "spring.datasource")
 *
 * */
@Configuration
@EnableTransactionManagement
public class PooledDatasourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PooledDatasourceConfig.class);

    @Delegate
    private HikariDataSource dataSourceBuilder;

    @Value("${spring.datasource.driverClassName}")
    private String databaseDriverClassName;

    @Value("${spring.datasource.jdbcUrl}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String encryptDatabasePassword;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String keyStorePath;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String plainKey;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String keyStorePassword;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String keyName;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String keyPassword;

    @Resource
    private Environment environment;

    @Bean
    public DataSource DataSource() {

        if ( null!= encryptDatabasePassword  || !encryptDatabasePassword.equals("")) {

            keyStorePath = environment.getProperty("keystore.path");
            keyStorePassword = environment.getProperty("keystore.password");
            keyPassword = environment.getProperty("keystore.key.password");
            keyName = environment.getProperty("keystore.alias.name");
            LOGGER.info("find key set path {},{},{},{}", keyStorePath, keyStorePassword, keyPassword, keyName);
            try {

                plainKey = DecryptDataKeyStore.decrypt(encryptDatabasePassword, keyStorePath, keyStorePassword, keyPassword, keyName);
            } catch (Exception gse) {
                LOGGER.error("Error in reading  encrypted db password", gse);
            }
        }

        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(databaseDriverClassName);
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(plainKey);
        return dataSource;
    }

}