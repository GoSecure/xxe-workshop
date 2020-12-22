package net.gosecure.rssviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = AtomController.class)
public class App1Application {

	public static void main(String[] args) {
		SpringApplication.run(App1Application.class, args);
	}
}
