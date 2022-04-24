package top.angelinaBot.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author strelitzia
 * @Date 2020/11/20 17:01
 **/
@Configuration
@MapperScan(basePackages = "top.angelinaBot.dao",
        sqlSessionTemplateRef = "angelinaSqlSessionTemplate")
@Slf4j
public class LocalDataSourceConfig {
    /**
     * 创建数据源
     */
    @Bean(name = "angelinaDataSource")
    public DataSource getFirstDataSource() {
        File file = new File("runFile/template.db");
        if (!file.exists()) {
            if (file.mkdirs()) {
                ClassPathResource classPathResource = new ClassPathResource("/sqlite/template.db");
                try(InputStream is = classPathResource.getInputStream();FileOutputStream fs = new FileOutputStream(file)) {
                    byte[] b = new byte[1024];
                    while (is.read(b) != -1) {
                        fs.write(b);
                    }
                } catch (IOException e) {
                    log.error("创建数据库文件失败");
                }
            }
        }
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
