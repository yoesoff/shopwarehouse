package com.mhyusuf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// TAMBAHKAN DUA IMPORT INI
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
// WAJIB: extends SpringBootServletInitializer
public class YSFunHRApplication extends SpringBootServletInitializer {

    // WAJIB: Override metode configure()
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(YSFunHRApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(YSFunHRApplication.class, args);
    }
}