package com.ics.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class IcsBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void applicationModules() {
        ApplicationModules modules = ApplicationModules.of(IcsBackendApplication.class);
        modules.forEach(System.out::println);
    }

}
