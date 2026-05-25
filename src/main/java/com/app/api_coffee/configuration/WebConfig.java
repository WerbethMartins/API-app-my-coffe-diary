package com.app.api_coffee.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig  implements WebMvcConfigurer {

    // Diretório onde as imagens serão armazenadas, configurável via application.properties
    @Value("${app.upload.dir:uploads/coffee-images}")
    private String uploadDir;

    // Configura o mapeamento para servir arquivos estáticos do diretório de uploads
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // Converte o caminho relativo para absoluto
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/")
                .setCachePeriod(3600); // Cache de 1 hora
    }
}
