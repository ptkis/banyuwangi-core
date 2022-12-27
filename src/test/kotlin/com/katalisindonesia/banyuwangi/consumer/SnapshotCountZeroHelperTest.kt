package com.katalisindonesia.banyuwangi.consumer

import com.katalisindonesia.banyuwangi.model.Camera
import com.katalisindonesia.banyuwangi.model.DetectionType
import com.katalisindonesia.banyuwangi.model.Snapshot
import com.katalisindonesia.banyuwangi.model.SnapshotCount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class SnapshotCountZeroHelperTest {
    @Test
    fun `removeZeros during the delay period but different id must not modify the map`() {
        val ref = AtomicReference(Instant.EPOCH)
        val helper = SnapshotCountZeroHelper { ref.get() }

        val camera0 = Camera(name = "Test 0", location = "Test 0")
        val camera1 = Camera(name = "Test 1", location = "Test 1")

        val map0 = mutableMapOf(
            DetectionType.CROWD to SnapshotCount(
                snapshot = Snapshot(
                    imageId = UUID.randomUUID(),
                    camera = camera0, length = 0
                ),
                value = 0,
                type = DetectionType.CROWD
            )
        )
        helper.removeZeros(
            cameraId = camera0.id,
            map = map0,
            delaySeconds = 60,
        )

        assertEquals(1, map0.size)

        val map1 = mutableMapOf(
            DetectionType.CROWD to SnapshotCount(
                snapshot = Snapshot(
                    imageId = UUID.randomUUID(),
                    camera = camera1, length = 0
                ),
                value = 0,
                type = DetectionType.CROWD
            )
        )
        helper.removeZeros(
            cameraId = camera1.id,
            map = map1,
            delaySeconds = 60,
        )

        assertEquals(1, map1.size)
    }
    @Test
    fun `removeZeros during the delay period must modify the map`() {
        val ref = AtomicReference(Instant.EPOCH)
        val helper = SnapshotCountZeroHelper { ref.get() }

        val camera0 = Camera(name = "Test 0", location = "Test 0")

        val map0 = mutableMapOf(
            DetectionType.CROWD to SnapshotCount(
                snapshot = Snapshot(
                    imageId = UUID.randomUUID(),
                    camera = camera0, length = 0
                ),
                value = 0,
                type = DetectionType.CROWD
            )
        )
        helper.removeZeros(
            cameraId = camera0.id,
            map = map0,
            delaySeconds = 60,
        )

        assertEquals(1, map0.size)

        val map1 = mutableMapOf(
            DetectionType.CROWD to SnapshotCount(
                snapshot = Snapshot(
                    imageId = UUID.randomUUID(),
                    camera = camera0, length = 0
                ),
                value = 0,
                type = DetectionType.CROWD
            )
        )
        helper.removeZeros(
            cameraId = camera0.id,
            map = map1,
            delaySeconds = 60,
        )

        assertEquals(0, map1.size)
    }
    @Test
    fun `removeZeros after the delay period must not modify the map`() {
        val ref = AtomicReference(Instant.EPOCH)
        val helper = SnapshotCountZeroHelper { ref.get() }

        val camera0 = Camera(name = "Test 0", location = "Test 0")

        val map0 = mutableMapOf(
            DetectionType.CROWD to SnapshotCount(
                snapshot = Snapshot(
                    imageId = UUID.randomUUID(),
                    camera = camera0, length = 0
                ),
                value = 0,
                type = DetectionType.CROWD
            )
        )
        helper.removeZeros(
            cameraId = camera0.id,
            map = map0,
            delaySeconds = 60,
        )

        assertEquals(1, map0.size)

        ref.set(Instant.now())

        val map1 = mutableMapOf(
            DetectionType.CROWD to SnapshotCount(
                snapshot = Snapshot(
                    imageId = UUID.randomUUID(),
                    camera = camera0, length = 0
                ),
                value = 0,
                type = DetectionType.CROWD
            )
        )
        helper.removeZeros(
            cameraId = camera0.id,
            map = map1,
            delaySeconds = 60,
        )

        assertEquals(1, map1.size)
    }
}
