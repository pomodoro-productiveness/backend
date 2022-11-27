package com.igorgorbunov3333.timer.console;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {

    @Override
    public void run(String... args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

}
