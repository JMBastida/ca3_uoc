package com.uoc.pr1

import com.uoc.pr1.data.DataSource
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testSeminarRecommendation() {
        val dataSource = DataSource()
        // Parameters: duration=30, skills="IT", experience=false
        val result = dataSource.seminarRecommendation(30, "IT", false)

        // Asserting that the expected result is "advanced"
        assertEquals("advanced", result)
    }

    /*
     * Android Studio Result:
     * The test FAILS.
     * Expected :advanced
     * Actual   :beginner
     * (Reason: In the seminarRecommendation method, if duration > 20 and skill is NOT "programming", it returns "beginner").
     *
     * Do we need to run the emulator to perform this type of test? Why?
     * No, we do not need to run the emulator to perform this test. This is a "Local Unit Test"
     * using JUnit4. It only tests pure Kotlin logic without relying on any Android framework
     * components (like Views, Context, or Activities). Therefore, it runs directly on your
     * computer's local Java Virtual Machine (JVM), which is much faster than running an emulator.
     */

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}