/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import java.io.IOException;
import java.security.GeneralSecurityException;


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
        try {
            if (null != encryptDatabasePassword && !encryptDatabasePassword.equals("")) {

                keyStorePath = environment.getProperty("keystore.path");
                keyStorePassword = environment.getProperty("keystore.password");
                keyPassword = environment.getProperty("keystore.key.password");
                keyName = environment.getProperty("keystore.alias.name");
                LOGGER.info("find key set path {},{},{},{}", keyStorePath, keyStorePassword, keyPassword, keyName);
                plainKey = DecryptDataKeyStore.decrypt(encryptDatabasePassword, keyStorePath, keyStorePassword, keyPassword, keyName);
            }
        } catch(NullPointerException npe) {
            LOGGER.error("Error in reading  encrypted db password", npe);
        } catch (GeneralSecurityException e) {
            LOGGER.error("Error in reading encrypted db password -> general Security exception", e);
        } catch (IOException e) {
            LOGGER.error("Error in reading  encrypted db password -> IOException", e);
        } catch (Exception e) {
            LOGGER.error("Error in reading  encrypted db password", e);
        }

        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(databaseDriverClassName);
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(plainKey);
        return dataSource;
    }

}
