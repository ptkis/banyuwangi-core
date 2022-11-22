package com.katalisindonesia.banyuwangi.controller

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class HelloControllerTest(
    @Autowired
    private val helloController: HelloController,
) {
    @Test
    fun `hello world not empty`() {
        val world = helloController.world()
        assertNotNull(world)
    }
}
