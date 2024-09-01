package com.teamr.runardo.uuapdataservice.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class UaapDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaapDataApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return args -> {
//
//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            Arrays.asList(ctx.getBeanDefinitionNames()).stream().sorted().forEach(System.out::println);
//
//        };
//    }
}
