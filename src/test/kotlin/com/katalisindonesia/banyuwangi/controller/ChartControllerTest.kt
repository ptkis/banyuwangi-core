package com.katalisindonesia.banyuwangi.controller

import org.junit.jupiter.api.Assertions.assertNotNull
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
        val result = chartController.flood(
            startDate = null,
            endDate = null,
            location = null,
        )
        assertNotNull(result)
    }
    @Test
    fun `trash all null`() {
        val result = chartController.trash(
            startDate = null,
            endDate = null,
            location = null,
        )
        assertNotNull(result)
    }
    @Test
    fun `street vendor all null`() {
        val result = chartController.streetvendor(
            startDate = null,
            endDate = null,
            location = null,
        )
        assertNotNull(result)
    }
    @Test
    fun `crowd all null`() {
        val result = chartController.crowd(
            startDate = null,
            endDate = null,
            location = null,
        )
        assertNotNull(result)
    }

    @Test
    fun `traffic all null`() {
        val result = chartController.traffic(
            startDate = null,
            endDate = null,
            location = null,
        )
        assertNotNull(result)
    }
}
