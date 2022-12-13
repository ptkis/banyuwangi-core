package com.katalisindonesia.banyuwangi.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class M3u8HelperTest {
    @Test
    fun extractGreatestDateTimeM3u8_notEmpty() {
        val content = """#EXTM3U
#EXT-X-VERSION:3
#EXT-X-ALLOW-CACHE:NO
#EXT-X-TARGETDURATION:3
#EXT-X-MEDIA-SEQUENCE:20210717192494
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192494.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192495.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192496.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192497.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192498.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192499.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192500.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192501.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192502.ts
#EXTINF:3.333333,
oRgcCjQ0wGdpzCG7fA4N5j0JNIUiJ9aRY=20210717192503.ts
"""
        val dt = extractGreatestDateTimeM3u8(content)

        Assertions.assertNotNull(dt)
        Assertions.assertEquals(LocalDateTime.of(2021, 7, 17, 19, 25), dt)
    }
    @Test
    fun extractGreatestDateTimeM3u8_empty() {
        val content = """#EXTM3U
#EXT-X-VERSION:3
#EXT-X-ALLOW-CACHE:NO
#EXT-X-TARGETDURATION:3
#EXT-X-MEDIA-SEQUENCE:0"""
        val dt = extractGreatestDateTimeM3u8(content)

        Assertions.assertNull(dt)
    }
}
