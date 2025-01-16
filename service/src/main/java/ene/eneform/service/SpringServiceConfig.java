package ene.eneform.service;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SpringServiceConfig {

    public static void main(String[] args) {
        SpringApplication.run(SpringServiceConfig.class, args);
    }

}
