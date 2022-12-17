package top.angelinaBot.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();


        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                                         @Override
                                         public void handleError(@NotNull ClientHttpResponse clientHttpResponse) throws IOException {
                                             //只要重写此方法，不去抛出HttpClientErrorException异常即可
                                             HttpStatus statusCode = clientHttpResponse.getStatusCode();
                                             System.out.println("错误码 = " + statusCode);
                                         }
                                     });
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<HttpMessageConverter<?>> partConverters = new ArrayList<>();
        partConverters.add(stringConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        partConverters.add(new ByteArrayHttpMessageConverter());
        formHttpMessageConverter.setPartConverters(partConverters);

        List<MediaType> MediaTypes = new ArrayList<MediaType>() {{
            add(MediaType.TEXT_PLAIN);
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON);
            add(MediaType.MULTIPART_FORM_DATA);
        }};

        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(MediaTypes);
        formHttpMessageConverter.setSupportedMediaTypes(MediaTypes);

        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(formHttpMessageConverter);

        return restTemplate;
    }
}
