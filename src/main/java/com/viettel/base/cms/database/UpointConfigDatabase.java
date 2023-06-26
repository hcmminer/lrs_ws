package com.viettel.base.cms.database;

import com.viettel.base.cms.common.Constant;
import com.viettel.security.PassTranformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "cmsEntityMgrFactory",
        transactionManagerRef = "cmsTransactionMgr",
        basePackages = {
          Constant.PACKAGE_REPO_UPOINT
        })
@EnableTransactionManagement
public class UpointConfigDatabase {

  @Autowired
  private Environment env;

  @Bean(name = "cms")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.cms.config")
  public DataSource dataSource() {
     PassTranformer.setInputKey(env.getProperty("key.enscrypt.security"));
    return DataSourceBuilder.create()
            .username(env.getProperty("spring.datasource.cms.username"))
            .password(env.getProperty("spring.datasource.cms.password"))
            .url(env.getProperty("spring.datasource.cms.url"))
            .build();
  }

  @Bean(name = "cmsEntityMgrFactory")
  @Primary
  public LocalContainerEntityManagerFactoryBean cmsEntityMgrFactory(
          final EntityManagerFactoryBuilder builder,
          @Qualifier("cms") final DataSource dataSource) {
    // dynamically setting up the hibernate properties for each of the datasource.
    final Map<String, String> properties = new HashMap<>();
    properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
    properties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
    return builder
            .dataSource(dataSource)
            .properties(properties)
            .packages(Constant.PACKAGE_MODEL_UPOINT)
            .persistenceUnit(Constant.UNIT_NAME_ENTITIES_CMS)
            .build();
  }

  @Bean(name = "cmsTransactionMgr")
  @Primary
  public PlatformTransactionManager cmsTransactionMgr(
          @Qualifier("cmsEntityMgrFactory") final EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
