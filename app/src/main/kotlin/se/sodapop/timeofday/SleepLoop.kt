package se.sodapop.timeofday

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

suspend fun sleepLoop(
    getSleepLoopInfo: () -> SleepLoopInfo,
    onTick: suspend (info: SleepLoopInfo) -> Unit,
) {
    while (currentCoroutineContext().isActive) {
        val info = getSleepLoopInfo()
        onTick(info)
        delay(info.sleepDuration)
    }
}

@OptIn(ExperimentalTime::class)
internal fun getSleepLoopInfo(
    now: Instant,
    zone: TimeZone,
    transitions: Transitions,
): SleepLoopInfo {
    val localDateTime = now.toLocalDateTime(zone)
    val localTime = localDateTime.time

    val current = transitions.getCurrent(localTime)
    val next = transitions.getNext(localTime)

    val diff = localDateTime.date.atTime(next.time).toInstant(zone) - now
    val duration = if (diff.isNegative()) diff + 1.days else diff

    return SleepLoopInfo(current.name, next.name, duration)
}

data class SleepLoopInfo(
    val currentName: String,
    val nextName: String,
    val sleepDuration: Duration,
)
