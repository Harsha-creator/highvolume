package com.example.highvolume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@EnableKafka
public class HighvolumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighvolumeApplication.class, args);
	}

}
