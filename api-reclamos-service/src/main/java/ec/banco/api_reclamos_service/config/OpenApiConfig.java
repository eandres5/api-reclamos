package ec.banco.api_reclamos_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Banco Internacional - API Reclamos")
                        .description("""
                                API REST para la gestión de reclamos de clientes del Banco Internacional.
                                
                                **Flujo de uso:**
                                1. `POST /v1/api/auth/login` → Obtener JWT con cédula y password
                                2. `GET /v1/api/clientes/{id}` → Consultar datos del cliente
                                3. `POST /v1/api/reclamos` → Registrar un reclamo
                                
                                **Autenticación:** Bearer Token (JWT)
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banco Internacional - TI")
                                .email("soporte@bancointernacional.ec"))
                        .license(new License()
                                .name("Uso interno")
                                .url("https://www.bancointernacional.ec")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo"),
                        new Server().url("https://api.bancointernacional.ec").description("Producción")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT. Ejemplo: eyJhbGciOiJIUzI1NiJ9...")));
    }
}
