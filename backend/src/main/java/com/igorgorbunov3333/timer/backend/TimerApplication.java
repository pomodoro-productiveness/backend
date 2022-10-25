package com.igorgorbunov3333.timer.backend;

import com.igorgorbunov3333.timer.backend.service.console.command.line.CommandLineController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimerApplication implements CommandLineRunner {

	@Autowired
	private CommandLineController commandLineController;

	public static void main(String[] args) {
		SpringApplication.run(TimerApplication.class, args);
	}

	@Override
	public void run(String... args) {
		commandLineController.start();
	}

}
