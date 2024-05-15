package org.tuanit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigClass {
    @Autowired
    TrustAllCertsManager trustAllCertsManager;
    @Bean
    public void setup(){
        trustAllCertsManager.disableSSLVerification();
    }
}
