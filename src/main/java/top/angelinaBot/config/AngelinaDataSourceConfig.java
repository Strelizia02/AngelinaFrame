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
public class AngelinaDataSourceConfig {
    /**
     * 创建数据源
     */
    @Bean(name = "angelinaDataSource")
    public DataSource getFirstDataSource() {
        File dir = new File("runFile");
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
        }
        File file = new File("runFile/template.db");
        if (!file.exists()) {
            try (InputStream is = new ClassPathResource("/sqlite/template.db").getInputStream();FileOutputStream fs = new FileOutputStream(file)) {
                if (file.createNewFile()) {
                        byte[] b = new byte[1024];
                        while (is.read(b) != -1) {
                            fs.write(b);
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("创建数据库文件失败");
            }
        }
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url("jdbc:sqlite:" + file.getAbsolutePath())
                .type(SQLiteDataSource.class)
                .build();
    }

    @Bean(name = "angelinaSqlSessionFactory")
    public SqlSessionFactory angelinaSqlSessionFactory(
            @Qualifier("angelinaDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
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
    public SqlSessionTemplate angelinaSqlSessionTemplate(
            @Qualifier("angelinaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
