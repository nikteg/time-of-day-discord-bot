@file:OptIn(ExperimentalTime::class)

package se.sodapop.timeofday

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class NameChangerLoopTest {
    @Test
    fun `nameChangerLoop works`() =
        runTest {
            val items =
                mutableListOf(
                    SleepLoopInfo("Third", "First", 1.hours),
                    SleepLoopInfo("First", "Second", 2.hours),
                    SleepLoopInfo("Second", "Third", 3.hours),
                )
            var name: String? = null

            val job =
                launch {
                    sleepLoop({
                        items.removeFirst()
                    }, { info ->
                        name = info.nextName
                    })
                }

            runCurrent()

            assertEquals("First", name)
            // First should be valid for 1 hour
            tick(10.minutes)
            assertEquals("First", name)
            tick(50.minutes)
            // Now 1 hour has passed
            assertEquals("Second", name)

            // Second should be valid for 2 hours
            tick(1.hours)
            assertEquals("Second", name)
            tick(30.minutes)
            assertEquals("Second", name)
            tick(1.hours)

            // Now 2 hours and 30 minutes have passed
            assertEquals("Third", name)

            job.cancelAndJoin()
        }

    fun TestScope.tick(duration: kotlin.time.Duration) {
        advanceTimeBy(duration)
        runCurrent()
    }
}
