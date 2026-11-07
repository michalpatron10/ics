package com.ics.backend.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MultitenantConfiguration {

    /*@Value("${defaultTenant}")*/
    private String defaultTenant = "ics_tenant1";

    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public DataSource dataSource() {
        Resource[] propertyFiles;
        try {
            propertyFiles = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:db/connections/*.properties");
        } catch (IOException exp) {
            throw new IllegalStateException("Unable to find tenant datasource properties at db/connections/*.properties", exp);
        }
        if (propertyFiles.length == 0) {
            throw new IllegalStateException("No tenant datasource properties found at db/connections/*.properties");
        }

        Map<Object, Object> resolvedDataSources = new HashMap<>();

        for (Resource propertyFile : propertyFiles) {
            Properties tenantProperties = new Properties();
            try (InputStream inputStream = propertyFile.getInputStream()) {
                tenantProperties.load(inputStream);
            } catch (IOException exp) {
                throw new IllegalStateException("Unable to load tenant datasource properties from " + propertyFile.getDescription(), exp);
            }

            String tenantId = tenantProperties.getProperty("name");
            if (tenantId == null || tenantId.isBlank()) {
                throw new IllegalStateException("Tenant datasource property 'name' is missing in " + propertyFile.getDescription());
            }
            if (resolvedDataSources.containsKey(tenantId)) {
                throw new IllegalStateException("Duplicate tenant datasource name '" + tenantId + "' in " + propertyFile.getDescription());
            }

            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(tenantProperties.getProperty("datasource.driver-class-name"));
            dataSourceBuilder.username(tenantProperties.getProperty("datasource.username"));
            dataSourceBuilder.password(tenantProperties.getProperty("datasource.password"));
            dataSourceBuilder.url(tenantProperties.getProperty("datasource.url"));
            resolvedDataSources.put(tenantId, dataSourceBuilder.build());
        }

        DataSource defaultDataSource = (DataSource) resolvedDataSources.get(defaultTenant);
        if (defaultDataSource == null) {
            throw new IllegalStateException("Default tenant datasource '" + defaultTenant + "' was not found in db/connections/*.properties");
        }

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(defaultDataSource);
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }

}