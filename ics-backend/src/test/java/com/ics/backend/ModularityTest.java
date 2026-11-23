package com.ics.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void verifiesModuleBoundaries() {
        ApplicationModules modules = ApplicationModules.of(IcsApplication.class);
        modules.forEach(System.out::println);
        modules.verify();
    }

}
