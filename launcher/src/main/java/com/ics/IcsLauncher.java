package com.ics;

import com.ics.common.MultitenantConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MultitenantConfiguration.class)
public class IcsLauncher {

    static void main(String[] args) {
        SpringApplication.run(IcsLauncher.class, args);
    }

}
