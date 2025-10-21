@file:OptIn(ExperimentalTime::class)

package se.sodapop.timeofday

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GetSleepLoopInfoTest {
    @Test
    fun `getSleepLoopInfo returns correct values before lunch`() =
        runTest {
            val zone = TimeZone.of("Europe/Stockholm")
            // 2025-01-15T10:30:00Z is 11:30:00 in Europe/Stockholm (UTC+1 during winter)
            val now = Instant.parse("2025-01-15T10:30:00Z")

            val (currentName, nextName, sleepDuration) =
                getSleepLoopInfo(
                    now,
                    zone,
                    Transitions(
                        listOf(
                            TimeTransition("Morgondiscord", LocalTime(6, 0)),
                            TimeTransition("Lunchdiscord", LocalTime(11, 30)),
                            TimeTransition("Eftermiddagsdiscord", LocalTime(13, 30)),
                            TimeTransition("Kvällsdiscord", LocalTime(17, 0)),
                            TimeTransition("Nattdiscord", LocalTime(21, 0)),
                        ),
                    ),
                )

            assertEquals("Lunchdiscord", currentName)
            assertEquals("Eftermiddagsdiscord", nextName)
            assertEquals(120.minutes, sleepDuration)
        }

    @Test
    fun `getSleepLoopInfo returns correct values in morning`() =
        runTest {
            val zone = TimeZone.of("Europe/Stockholm")
            // 2025-01-15T07:00:00Z is 08:00:00 in Europe/Stockholm (UTC+1)
            val now = Instant.parse("2025-01-15T07:00:00Z")

            val (currentName, nextName, sleepDuration) =
                getSleepLoopInfo(
                    now,
                    zone,
                    Transitions(
                        listOf(
                            TimeTransition("Morgondiscord", LocalTime(6, 0)),
                            TimeTransition("Lunchdiscord", LocalTime(11, 30)),
                            TimeTransition("Eftermiddagsdiscord", LocalTime(13, 30)),
                            TimeTransition("Kvällsdiscord", LocalTime(17, 0)),
                            TimeTransition("Nattdiscord", LocalTime(21, 0)),
                        ),
                    ),
                )

            assertEquals("Morgondiscord", currentName)
            assertEquals("Lunchdiscord", nextName)
            assertEquals(210.minutes, sleepDuration)
        }

    @Test
    fun `getSleepLoopInfo wraps to next day`() =
        runTest {
            val zone = TimeZone.of("Europe/Stockholm")
            // 2025-01-15T22:00:00Z is 23:00:00 in Europe/Stockholm (UTC+1)
            val now = Instant.parse("2025-01-15T22:00:00Z")

            val (currentName, nextName, sleepDuration) =
                getSleepLoopInfo(
                    now,
                    zone,
                    Transitions(
                        listOf(
                            TimeTransition("Morgondiscord", LocalTime(6, 0)),
                            TimeTransition("Lunchdiscord", LocalTime(11, 30)),
                            TimeTransition("Eftermiddagsdiscord", LocalTime(13, 30)),
                            TimeTransition("Kvällsdiscord", LocalTime(17, 0)),
                            TimeTransition("Nattdiscord", LocalTime(21, 0)),
                        ),
                    ),
                )

            assertEquals("Nattdiscord", currentName)
            assertEquals("Morgondiscord", nextName)
            assertEquals(420.minutes, sleepDuration)
        }
}
