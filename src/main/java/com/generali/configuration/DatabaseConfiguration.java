package com.generali.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = { "com.generali.domain" })
public class DatabaseConfiguration implements TransactionManagementConfigurer {

  private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

//  @Resource
//  private HaloConfigurationProperties haloConfigurationProperties;

  @Resource
  private Environment environment;

  @Bean
  public DataSource dataSource() {
    LOGGER.info("Attempting to create the Data Source");

    HikariDataSource dataSource = new HikariDataSource();

//    dataSource.setDriverClassName(haloConfigurationProperties.getDataSource().getDriverClassName());
//    dataSource.setJdbcUrl(haloConfigurationProperties.getDataSource().getUrl());
//    dataSource.setUsername(haloConfigurationProperties.getDataSource().getUsername());
//    dataSource.setPassword(haloConfigurationProperties.getDataSource().getPassword());
//    // dataSource.setMinimumIdle(haloConfigurationProperties.getDataSource().getHikari().getMinimumIdle());
//    dataSource.setMaximumPoolSize(haloConfigurationProperties.getDataSource().getHikari().getMaximumPoolSize());

    dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
    dataSource.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
    dataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
    dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));

    LOGGER.info("Created the Data Source");

    return dataSource;
  }

  private Properties hibernateProperties() {
    LOGGER.info("Attempting to create the Hibernate Properties Map");

    Properties hibernateProperties = new Properties();

    hibernateProperties.setProperty("hibernate.dialect",
                                    environment.getRequiredProperty("spring.jpa.properties.hibernate.dialect"));

    // We always want to use the hibernate 5 spring session context - should never change doesnt need to be a property
    hibernateProperties.setProperty("hibernate.current_session_context_class",
                                    SpringSessionContext.class.getCanonicalName());

    // This should never actually be changed, but we still want it to be a property so we can configure it to
    // auto-generate the tables for tests to run against. This property should only ever be configured within the
    // application.properties that exists within src/main/resources and is bundled as part of the jar, never externally.
    hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
                                    environment.getRequiredProperty("spring.jpa.hibernate.ddl-auto"));

    // Use the old generator mappings this affects SQL Server 2012+, forcing the database to use IDENTITY for PKs
    // instead of using the newer sequences, consider changing this to true when we can guarantee that we only need to
    // support SQLServer 2012+ (as this is the first time sequences were available in SQLServer)
    // Changing this means BIG database changes for SQLServer - No benefit in changing?
    hibernateProperties.setProperty("hibernate.id.new_generator_mappings",
                                    "false");

    if ("com.microsoft.sqlserver.jdbc.SQLServerDriver"
      .equals(environment.getRequiredProperty("spring.datasource.driver-class-name"))) {
      // only valid for sqlserver
      // Use nationalized (nvarchar) over varchar for sting columns, see:
      // http://confluence:8090/display/DEVOPS/varchar+versus+nvarchar
      hibernateProperties.setProperty("hibernate.use_nationalized_character_data",
                                      "true");
    }

    hibernateProperties.setProperty("hibernate.jdbc.time_zone", "UTC");
    LOGGER.info("Created the Hibernate Properties Map");

    return hibernateProperties;
  }

  @Bean
  @DependsOn("liquibase")
  public SessionFactory sessionFactory() {
    LOGGER.info("Attempting to create the Hibernate Session Factory");

    try {
      LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();

      localSessionFactoryBean.setDataSource(dataSource());
      localSessionFactoryBean.setHibernateProperties(hibernateProperties());
      localSessionFactoryBean.setPackagesToScan("com.generali.domain");
      localSessionFactoryBean.setAnnotatedPackages("com.generali.domain");
      localSessionFactoryBean.afterPropertiesSet();

      SessionFactory sessionFactory = localSessionFactoryBean.getObject();

      LOGGER.info("Created the Hibernate Session Factory");

      return sessionFactory;
    }
    catch (IOException ex) {
      LOGGER.error("Unable to create the Hibernate Session Factory: ", ex);
      throw new ApplicationRuntimeException("Unable to create Hibernate Session Factory", ex);
    }
  }

  @Override
  public TransactionManager annotationDrivenTransactionManager() {
    return transactionManager();
  }

   @Bean
   @Primary
   @DependsOn("sessionFactory")
   public HibernateTransactionManager transactionManager() {
     LOGGER.info("Attempting to create the Transaction Manager");

     HibernateTransactionManager txManager = new HibernateTransactionManager();
     txManager.setSessionFactory(sessionFactory());
     txManager.setRollbackOnCommitFailure(true);

     LOGGER.info("Created the Transaction Manager");

     return txManager;
   }

//  @Bean
//  @Primary
//  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//    //JpaVendorAdapteradapter can be autowired as well if it's configured in application properties.
//    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//    vendorAdapter.setGenerateDdl(false);
//
//    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//    factory.setJpaVendorAdapter(vendorAdapter);
//    //Add package to scan for entities.
//    factory.setPackagesToScan("com.generali.domain");
//    factory.setDataSource(dataSource());
//    return factory;
//  }

  // @Bean
  // @DependsOn("sessionFactory")
  // public TransactionTemplate transactionTemplate() {
  // LOGGER.info("Attempting to create the Transaction Template");
  //
  // TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
  // transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
  //
  // LOGGER.info("Created the Transaction Template");
  //
  // transactionTemplate.execute(status -> status);
  //
  // return transactionTemplate;
  // }

}
