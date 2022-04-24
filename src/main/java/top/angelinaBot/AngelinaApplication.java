package top.angelinaBot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = {"top.angelinaBot.dao"})
@EnableSwagger2
@EnableScheduling
@EnableAsync
public class AngelinaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AngelinaApplication.class, args);
    }

}
