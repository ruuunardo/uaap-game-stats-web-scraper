package com.teamr.runardo.uuapdataservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UuapdataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UuapdataServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner test() {
		return runner -> {
			System.out.println("Hello!");

//			UaapSeason uaapSeason = new UaapSeason();
//			System.out.println(uaapSeason.getSeasonNumber());

		};
	}

}
