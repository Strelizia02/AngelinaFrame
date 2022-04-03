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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 注入DataSource相关配置Bean
 **/
@Configuration
@MapperScan(basePackages = "top.angelinaBot.dao",
        sqlSessionTemplateRef = "localSqlSessionTemplate")
public class LocalDataSourceConfig {

    @Bean(name = "localDataSource")
    public DataSource getFirstDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url("jdbc:sqlite:angelina.db")
                .type(SQLiteDataSource.class)
                .build();
    }

    @Bean("localSqlSessionFactory")
    public SqlSessionFactory localSqlSessionFactory(
            @Qualifier("localDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapping/*.xml"));
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean("localTransactionManger")
    public DataSourceTransactionManager localTransactionManger(
            @Qualifier("localDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("localSqlSessionTemplate")
    public SqlSessionTemplate localSqlSessionTemplate(
            @Qualifier("localSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MyMappingJackson2HttpMessageConverter());
        return restTemplate;
    }

}

class MyMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public MyMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        setSupportedMediaTypes(mediaTypes);
    }
}
