package top.angelinaBot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author strelitzia
 * @Date 2022/04/03
 * 读取配置文件中DataSource相关配置
 **/
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class LocalDataSourceProperties {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
