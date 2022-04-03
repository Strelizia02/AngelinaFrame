package top.angelinaBot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;


/**
 * @author strelitzia
 * @Date 2022/04/03
 * Swagger的配置类
 **/
@Configuration
@EnableSwagger2
@Profile(value = {"staging", "development"})
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(false)
                .apiInfo(apiInfo())
                .select()
                .apis(basePackage("top.angelinaBot.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Angelina QQ Bot Frame")
                .termsOfServiceUrl("https://github.com/Strelizia02/ArknightsAPI")
                .version("1.0")
                .description("API 描述")
                .build();
    }
}
