package com.jungle.chalnaServer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ChalnaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChalnaServerApplication.class, args);
    }

}
