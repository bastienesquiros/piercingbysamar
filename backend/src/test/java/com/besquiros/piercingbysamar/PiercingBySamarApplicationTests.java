package com.besquiros.piercingbysamar;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires a running PostgreSQL instance — run with Testcontainers in CI")
class PiercingBySamarApplicationTests {

    @Test
    void contextLoads() {
    }

}
