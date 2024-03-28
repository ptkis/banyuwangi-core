package com.katalisindonesia.banyuwangi.task

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("default", "secret")
class StreamingCheckTaskTest(
    @Autowired
    private val streamingCheckTask: StreamingCheckTask,
) {
    @Test
    fun streamingCheck() {
        Assertions.assertDoesNotThrow {
            streamingCheckTask.streamingCheck()
        }
    }
}
