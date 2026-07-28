package com.infotact.project1;

import com.infotact.project1.config.TestRedisBeansConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisBeansConfig.class)
class Project1ApplicationTests {

    @Test
    void contextLoads() {
    }
}
