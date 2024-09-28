package com.grad.akemha;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class AkemhaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkemhaApplication.class, args);
    }

    @Configuration
    public static class OpenApiConfig {

        // Swagger URL: http://localhost:8090/swagger-ui/index.html
        @Bean
        public OpenAPI customOpenAPI() {
            var securityKey = "Auth Token";
            var securityScheme = new SecurityScheme();
            securityScheme
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer");
            return new OpenAPI()
                    .components(
                            new Components().addSecuritySchemes(securityKey, securityScheme)
                    )
                    .addSecurityItem(new SecurityRequirement().addList(securityKey))
                    .info(
                            new Info()
                                    .title("Akemha - API")
                                    .version("1.0.0")
                                    .description("Backend documentation for Akemha app")
                    );
        }
    }

}
