package com.batch.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MssqlDBConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.mssql.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "mssqlDataSource")
    @ConfigurationProperties("spring.mssql.datasource.configuration")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    /*
     * SqlSessionFactory 설정 MyBatis에 관련된 SqlSessionFactory 객체를 반환하는 빈을 선언
     */
    @Primary
    @Bean(name = "mssqlSqlSessionFactory")
    public SqlSessionFactory mssqlSqlSessionFactory(@Qualifier("mssqlDataSource") DataSource mssqlDataSource)
            throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(mssqlDataSource);
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/**/*.xml")); // MyBatis
                                                                                                      // 매퍼
                                                                                                      // 파일의
                                                                                                      // 위치를
                                                                                                      // 설정
        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactoryBean.getObject();
    }

    /*
     * SqlSessionTemplate 설정 SqlSessionTemplate은 SqlSession을 구현하고 코드에서
     * SqlSession를 대체하는 역할 Thead Safe하게 작성되었기 때문에 여러 매퍼에서 공유
     */
    @Primary
    @Bean(name = "mssqlSqlSessionFactory")
    public SqlSessionTemplate mssqlSqlSessionTemplate(SqlSessionFactory mssqlSqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(mssqlSqlSessionFactory);
    }

    /*
     * DataSourceTransactionManager 설정 트랜잭션 매니저를 반환하는 빈을 선언
     */
    @Bean(name = "mssqlDataSourceTransactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("mssqlDataSource") DataSource dataSource)
            throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }
}