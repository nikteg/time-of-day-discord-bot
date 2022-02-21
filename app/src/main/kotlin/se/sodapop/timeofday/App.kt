package se.sodapop.timeofday

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@OptIn(ObsoleteCoroutinesApi::class)
suspend fun main() {
    val rest = RestClient(System.getenv("TOKEN"))
    val channelId = Snowflake(System.getenv("CHANNEL_ID"))

    val zone = ZoneId.of("Europe/Stockholm")
    val now = ZonedDateTime.now(zone)
    val halfHour = DurationUnit.ofMinutes(30)
    val scheduleDate = now.truncatedTo(halfHour).plusMinutes(30)
    val timeUntilNextHour = now.until(scheduleDate, ChronoUnit.MILLIS)
    val everyHalfHour = halfHour.duration.toMillis()

    val ticker = ticker(everyHalfHour, timeUntilNextHour)

    println("I will tick in ${formatRelativeTime(timeUntilNextHour)}")

    while (true) {
        println("Time is currently ${ZonedDateTime.now(zone)}. Next half hour, the channel name will be ${getChannelName(ZonedDateTime.now(zone))}")

        ticker.receive()

        val name = getChannelName(ZonedDateTime.now(zone))

        println("Woop, will rename to $name")

        rest.channel.patchChannel(channelId, ChannelModifyPatchRequest(Optional(name)))
    }
}

private fun getChannelName(dateTime: ZonedDateTime): String {
    val time = dateTime.hour + dateTime.minute / 60.0
    return getChannelName(time)
}

private fun getChannelName(time: Double): String {
    return when {
        time >= 21.0 -> "Nattdiscord"
        time >= 17.0 -> "Kvällsdiscord"
        time >= 13.5 -> "Eftermiddagsdiscord"
        time >= 11.5 -> "Lunchdiscord"
        time >= 6 -> "Morgondiscord"
        else -> "Nattdiscord"
    }
}

fun formatRelativeTime(millis: Long): String {
    return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))
}