package com.ics.backend;

import com.ics.IcsLauncher;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
class IcsLauncherTests {

    @Autowired
    private DataSource dataSource;

/*    @Test
    void contextLoads() {
        assertInstanceOf(MultitenantDataSource.class, dataSource);
    }*/

    @Test
    void applicationModules() {
        ApplicationModules modules = ApplicationModules.of(IcsLauncher.class);
        modules.forEach(System.out::println);
    }

}
