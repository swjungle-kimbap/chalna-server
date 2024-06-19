package com.jungle.chalnaServer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class ChalnaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChalnaServerApplication.class, args);
    }

}
