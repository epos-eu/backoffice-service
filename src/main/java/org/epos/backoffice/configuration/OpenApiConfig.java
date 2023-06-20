package org.epos.backoffice.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = {"org.epos.backoffice"})
@Import({org.springdoc.core.SpringDocConfiguration.class,
        org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class})
class OpenApiConfig implements WebMvcConfigurer {
}