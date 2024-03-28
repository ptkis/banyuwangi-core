package com.katalisindonesia.banyuwangi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("default", "secret")
class BanyuwangiCoreApplicationTests {

    @Test
    fun contextLoads() {
        // test context
    }
}
