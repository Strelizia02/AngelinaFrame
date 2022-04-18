package top.angelinaBot.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;

/**
 * @author strelitzia
 * @Date 2020/11/20 17:01
 **/
@Configuration
@MapperScan(basePackages = "top.angelinaBot.dao",
        sqlSessionTemplateRef = "angelinaSqlSessionTemplate")
public class LocalDataSourceConfig {
    /**
     * 创建数据源
     */
    @Bean(name = "angelinaDataSource")
    public DataSource getFirstDataSource() {
        File file = new File("src/main/resources/sqlite/template.db");
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url("jdbc:sqlite:" + file.getAbsolutePath())
                .type(SQLiteDataSource.class)
                .build();
    }

    @Bean(name = "angelinaSqlSessionFactory")
    public SqlSessionFactory localSqlSessionFactory(
            @Qualifier("angelinaDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapping/*.xml"));
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean("angelinaTransactionManger")
    public DataSourceTransactionManager angelinaTransactionManger(
            @Qualifier("angelinaDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 创建SqlSessionTemplate
    @Bean(name = "angelinaSqlSessionTemplate")
    public SqlSessionTemplate localSqlSessionTemplate(
            @Qualifier("angelinaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
