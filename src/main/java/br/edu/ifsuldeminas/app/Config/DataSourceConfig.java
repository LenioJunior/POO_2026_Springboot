package br.edu.ifsuldeminas.app.Config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName:com.microsoft.sqlserver.jdbc.SQLServerDriver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        createDatabaseIfNotExists();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(datasourceUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        return ds;
    }

    private void createDatabaseIfNotExists() {
        String dbName = extractDatabaseName(datasourceUrl);
        String masterUrl = datasourceUrl.replaceAll("(?i)databaseName=[^;]+", "databaseName=master");

        try (Connection conn = DriverManager.getConnection(masterUrl, username, password);
                Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'" + dbName + "')" +
                            " CREATE DATABASE [" + dbName + "]");
            System.out.println("Database '" + dbName + "' verified/created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Could not create database '" + dbName + "': " + e.getMessage(), e);
        }
    }

    private String extractDatabaseName(String url) {
        Matcher matcher = Pattern.compile("(?i)databaseName=([^;]+)").matcher(url);
        return matcher.find() ? matcher.group(1) : "MyDatabase";
    }
}
