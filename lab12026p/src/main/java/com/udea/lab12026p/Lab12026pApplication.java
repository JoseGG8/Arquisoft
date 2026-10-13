package com.udea.lab12026p;

import com.udea.lab12026p.entity.Customer;
import com.udea.lab12026p.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab12026pApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lab12026pApplication.class, args);
	}

	@Bean
	@SuppressWarnings("null")
	CommandLineRunner initDatabase(CustomerRepository customerRepository) {
		return args -> {
			if (customerRepository.count() == 0) {
				Customer c1 = new Customer(null, "123456", "Carlos", "Perez", 1500.0);
				Customer c2 = new Customer(null, "654321", "Maria", "Gomez", 2500.0);
				customerRepository.save(c1);
				customerRepository.save(c2);
			}
		};
	}

}
