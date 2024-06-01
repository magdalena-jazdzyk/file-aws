package com.app.file.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        var server = new Server();
        server.setUrl("https://localhost:8080");
        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components());
    }
}
