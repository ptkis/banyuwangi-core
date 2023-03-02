package com.katalisindonesia.banyuwangi.service

import com.katalisindonesia.imageserver.service.ProxyRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.net.URI

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ProxyServiceTest(
    @Autowired
    private val proxyService: ProxyService,
) {
    @Test
    fun `test using basic auth header`() {
        val result = proxyService.proxy(
            proxyRequest = ProxyRequest(
                uri = URI("https://apingweb.com/api/auth/user/1"),
                headers = mapOf(
                    "Authorization" to "Basic YWRtaW46MTIzNDU="
                )
            )
        )

        assertEquals(
            """{"success":true,"message":"Success","status":200,"data":[{"user_id":"1","name":"bruce wayne","age":"36",
            |"image":"https:\/\/example.com\/batman.png","date_created":"2022-02-08 16:00:20",
            |"email":"batman@yahoo.com"}]}
            """.trimMargin(),
            String(result.get())
        )
    }

    @Test
    fun `test using no basic auth header should return empty`() {
        assertThrows(ProxyException::class.java) {
            proxyService.proxy(
                proxyRequest = ProxyRequest(
                    uri = URI("https://apingweb.com/api/auth/user/100"),
                    headers = mapOf()
                )
            )
        }
    }
}
