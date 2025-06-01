package com.bjoernkw.batch.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.mysql")
  public DataSourceProperties mysqlDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.postgresql")
  public DataSourceProperties postgresqlDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  public DataSource dataSource() {
    return dataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean
  public DataSource mysqlDataSource() {
    return mysqlDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean
  public DataSource postgresqlDataSource() {
    return postgresqlDataSourceProperties()
        .initializeDataSourceBuilder()
        .build();
  }

  @Bean
  public JdbcTemplate mysqlJdbcTemplate(
      @Qualifier("mysqlDataSource") DataSource dataSource
  ) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public JdbcTemplate postgresqlJdbcTemplate(
      @Qualifier("postgresqlDataSource") DataSource dataSource
  ) {
    return new JdbcTemplate(dataSource);
  }
}
