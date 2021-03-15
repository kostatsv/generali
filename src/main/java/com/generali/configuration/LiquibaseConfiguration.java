package com.generali.configuration;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class LiquibaseConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseConfiguration.class);

  @Resource
  private DataSource dataSource;

  @Resource
  private Environment environment;

  @Bean
  public SpringLiquibase liquibase() throws FileNotFoundException, IOException {
    LOGGER.info("Beginning liquibase config");
    SpringLiquibase liquibase = new SpringLiquibase();

    // Datasource used only for liquibase (separate datasource for the main application)
//    HikariDataSource dataSource = new HikariDataSource();

    // Need to load properties directly from the bootstrap properties as liquibase will be run before the environment
    // has loaded the properties from the database (because liquibase sets up the properties in the database to begin
    // with)
//    Properties bootstrapProperties = BootstrapUtils.loadBootstrapProperties();
//
//    dataSource.setJdbcUrl(BootstrapUtils.getJdbcUrl(bootstrapProperties));
//    dataSource.setUsername(BootstrapUtils.getJdbcUsername(bootstrapProperties));
//    dataSource.setPassword(BootstrapUtils.getJdbcPassword(bootstrapProperties));
//    dataSource.setDriverClassName(BootstrapUtils.getJdbcDriverClassName(bootstrapProperties));
//    dataSource.setPoolName("liquibase");
//    dataSource.setMaximumPoolSize(1);

    liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
    liquibase.setDataSource(dataSource);
//    liquibase
//      .setChangeLogParameters(buildChangeLogParameters(dataSource.getJdbcUrl(), dataSource.getDriverClassName()));

    liquibase.setChangeLogParameters(buildChangeLogParameters());

    LOGGER.info("Liquibase config complete");

    return liquibase;
  }

  private Map<String, String> buildChangeLogParameters() {
    LOGGER.debug("Beginning generation of properties for liquibase scripts");
    Map<String, String> changeLogParameters = new HashMap<>();

    String jdbcUrl = environment.getRequiredProperty("spring.datasource.url");

    LOGGER.debug("Generating database parameter based on jdbc connection url ({})", jdbcUrl);

    LOGGER.debug("Looking for databaseName= within the JDBC URL");

    if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(environment.getRequiredProperty("spring.datasource.driver-class-name"))) {
      // database name only required for sql server

      Pattern databaseNameParameterPattern = Pattern.compile("databaseName=", Pattern.CASE_INSENSITIVE);
      Matcher databaseNameParameterMatcher = databaseNameParameterPattern.matcher(jdbcUrl);

      Integer endIndex = -1;

      if (databaseNameParameterMatcher.find()) {
        endIndex = databaseNameParameterMatcher.end();
        LOGGER.trace("String databaseName= found within the JDBC URL starting " +
                     "at index ({}) and ending at index ({})",
                     databaseNameParameterMatcher.start(), endIndex);
      }

      if (endIndex == -1) {
        String message = String.format("Could not find location of databaseName= parameter within the " +
                                       "Settlements JDBC URL (%s), cannot determine database name required for " +
                                       "running liquibase validation and configuration",
                                       jdbcUrl);
        LOGGER.error(message);
        throw new RuntimeException(message);
      }

      String databaseNameOnwards = jdbcUrl.substring(endIndex);

      LOGGER.trace("After stripping everything before the databaseName= parameter ({})",
                   databaseNameOnwards);

      // ; should first appear after the actual defined database name in the url this gives us our end point
      Integer firstIndexOfSemiColon = databaseNameOnwards.indexOf(';');
      LOGGER.trace("First instance of ; in remaining url ({}) found to be ({})",
                   databaseNameOnwards,
                   firstIndexOfSemiColon);

      String databaseName = databaseNameOnwards;

      if (firstIndexOfSemiColon != -1) {
        LOGGER.trace("Found ; after databaseName remove ; and everything following it to get the database name");
        databaseName = databaseNameOnwards.substring(0, firstIndexOfSemiColon);
      }
      else {
        LOGGER
          .trace("No ; found after databaseName, assuming this is the end of the databaseName parameter within the url");
      }

      LOGGER.debug("Stripped database name out of datasource connection url, " +
                   "will be used in liquibase scripts ({})",
                   databaseName);

      changeLogParameters.put("database-name", databaseName);
    }

    return changeLogParameters;
  }
}
