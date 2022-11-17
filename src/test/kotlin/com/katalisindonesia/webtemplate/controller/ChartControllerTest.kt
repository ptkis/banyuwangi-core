package com.katalisindonesia.webtemplate.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class ChartControllerTest(
    @Autowired private val chartController: ChartController,
) {
    @Test
    fun `flood all null`() {
        val result = chartController.flood(null, null, null, null)
        assertNotNull(result)
    }
}
