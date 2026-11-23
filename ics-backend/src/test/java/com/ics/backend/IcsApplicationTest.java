package com.ics.backend;

import com.ics.backend.common.MultitenantDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IcsApplicationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void usesMultitenantDataSource() {
        assertThat(dataSource).isInstanceOf(MultitenantDataSource.class);
    }

}
