package com.bugsnag.android

import android.os.Looper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BlockedThreadDetectorTest {

    private val looper = Looper.getMainLooper()

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidBlockedThresholdMs() {
        BlockedThreadDetector(-1, looper) {}
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidThread() {
        BlockedThreadDetector(1, null) {}
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidDelegate() {
        BlockedThreadDetector(1, looper, null)
    }
}
