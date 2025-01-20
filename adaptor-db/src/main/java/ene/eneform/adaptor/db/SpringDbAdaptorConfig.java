package ene.eneform.adaptor.db;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan
@EntityScan(basePackages = {"ene.eneform.domain"})
@EnableJpaRepositories(basePackages = {"ene.eneform.adaptor.db"})
@PropertySource("classpath:adapter-db.properties")
public class SpringDbAdaptorConfig {

}
