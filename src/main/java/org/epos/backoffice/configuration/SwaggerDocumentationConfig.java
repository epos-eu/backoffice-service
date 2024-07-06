package org.epos.backoffice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-10-12T08:15:11.660Z[GMT]")
@Configuration
public class SwaggerDocumentationConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Backoffice RESTful APIs")
                .description("This is the Backoffice RESTful APIs Swagger page.")
                .termsOfService("")
                .version(System.getenv("VERSION"))
                .license(new License()
                    .name("MIT License")
                    .url("https://epos-ci.brgm.fr/epos/WebApi/raw/master/LICENSE"))
                .contact(new io.swagger.v3.oas.models.info.Contact()
                    .email("apis@lists.epos-ip.org")));
    }

}