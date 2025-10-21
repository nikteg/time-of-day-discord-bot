@file:OptIn(ExperimentalTime::class)

package se.sodapop.timeofday

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private suspend fun main() {
    val rest = RestClient(System.getenv("TOKEN"))
    val channelId = Snowflake(System.getenv("CHANNEL_ID"))
    val zone = TimeZone.of("Europe/Stockholm")

    val transitions =
        Transitions(
            listOf(
                TimeTransition("Morgondiscord", LocalTime(6, 0)),
                TimeTransition("Lunchdiscord", LocalTime(11, 30)),
                TimeTransition("Eftermiddagsdiscord", LocalTime(13, 30)),
                TimeTransition("Kvällsdiscord", LocalTime(17, 0)),
                TimeTransition("Nattdiscord", LocalTime(21, 0)),
            ),
        )

    val getNow = { Clock.System.now() }

    sleepLoop(
        getSleepLoopInfo = { getSleepLoopInfo(getNow(), zone, transitions) },
        onTick = { info ->
            println("Changing name to '${info.currentName}'")
            withContext(Dispatchers.IO) {
                rest.channel.patchChannel(
                    channelId,
                    ChannelModifyPatchRequest(Optional(info.currentName)),
                )
            }
            val now = getNow()
            val localTime = now.toLocalDateTime(zone)
            val nextUpdate = now + info.sleepDuration
            val nextLocalTime = nextUpdate.toLocalDateTime(zone)
            println("Current time is '$localTime'")
            println("Next update scheduled at '$nextLocalTime' (in '${info.sleepDuration}') with name '${info.nextName}'")
        },
    )
}
