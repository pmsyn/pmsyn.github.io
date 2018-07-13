package com.shiro.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shiro.cache.ehcache.EhCache;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author pms
 * @create 2017/12/17 15:45
 * @since 1.0
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.shiro.mapper")
public class DataSourceConfig {

    //spring jdbc驱动数据源
    @Bean
    @Profile("pro")
    public DataSource proDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        //Properties properties = new Properties();
        try {
            //properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));
            ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            ds.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
            ds.setUsername("fkyh");
            ds.setUsername("fkyh");
            // properties.load(new FileInputStream(new File("classpath:jdbc.properties")));
            //ds.setConnectionProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    @Profile("dev")
    public DruidDataSource dataSource() {
        DruidDataSource ds = new DruidDataSource();
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));
            ds.configFromPropety(properties);
            List<Filter> list = new ArrayList<Filter>();
            list.add(logFilter());
            ds.setProxyFilters(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        // 设置 mapper xml
        //sessionFactory.setMapperLocations(applicationContext.getResources("classpath:com/shiro/mapper/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public Slf4jLogFilter logFilter() {
        Slf4jLogFilter filter = new Slf4jLogFilter();
        filter.setDataSourceLoggerName("datasource");
        filter.setResultSetLogEnabled(true);
        filter.setConnectionLogEnabled(true);
        filter.setStatementParameterClearLogEnable(true);
        filter.setStatementCreateAfterLogEnabled(true);
        filter.setStatementCloseAfterLogEnabled(true);
        filter.setStatementParameterSetLogEnabled(true);
        filter.setStatementPrepareAfterLogEnabled(true);
        filter.setStatementExecuteQueryAfterLogEnabled(true);
        filter.setStatementExecuteUpdateAfterLogEnabled(true);
        return filter;
    }

}
