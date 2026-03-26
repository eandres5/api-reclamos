package ec.banco.api_reclamos_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"ec.banco.api_reclamos_service", "ec.banco.commons"})
public class ApiReclamosServiceApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(ApiReclamosServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ApiReclamosServiceApplication.class);
	}


}
