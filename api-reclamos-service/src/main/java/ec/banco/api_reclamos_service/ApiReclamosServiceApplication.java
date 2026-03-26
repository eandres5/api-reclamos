package ec.banco.api_reclamos_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"ec.banco.api_reclamos_service", "ec.banco.commons"})
public class ApiReclamosServiceApplication extends SpringBootServletInitializer {

    /**
     * Metodo principal para levantar la aplicacion de springboot.
     *
     * @param args un array de argumentos para la aplicacion
     */
    public static void main(final String[] args) {
        SpringApplication.run(ApiReclamosServiceApplication.class, args);
    }

    /**
     * Configura la aplicación Spring Boot cuando es desplegada como archivo WAR
     * en un contenedor externo de servlets (por ejemplo, Tomcat o WildFly).
     *
     * @param application utilizado para construir el contexto de la aplicacion
     * @return configuracion de las fuentes de la aplicacion
     */
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(ApiReclamosServiceApplication.class);
    }


}
