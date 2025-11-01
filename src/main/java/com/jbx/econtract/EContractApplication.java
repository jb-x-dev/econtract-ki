package com.jbx.econtract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * eContract KI - Hauptanwendungsklasse
 * 
 * KI-gestütztes Vertragsverwaltungsmodul für die jb-x eBusiness Suite
 * 
 * @author jb-x development team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class EContractApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EContractApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EContractApplication.class);
    }
}

