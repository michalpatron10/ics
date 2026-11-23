package com.ics.backend;

import com.ics.backend.common.MultitenantDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
class IcsApplicationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
        assertInstanceOf(MultitenantDataSource.class, dataSource);
    }

}
