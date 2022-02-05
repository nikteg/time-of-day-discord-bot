package se.sodapop.timeofday

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@OptIn(ObsoleteCoroutinesApi::class)
suspend fun main() {
    val rest = RestClient(System.getenv("TOKEN"))
    val channelId = Snowflake(System.getenv("CHANNEL_ID"))

    val now = ZonedDateTime.now(ZoneId.of("Europe/Stockholm"))
    val scheduleDate = now.truncatedTo(ChronoUnit.HOURS).plusHours(1)
    val timeUntilNextHour = now.until(scheduleDate, ChronoUnit.MILLIS)
    val everyHour = 3_600_000L

    val ticker = ticker(everyHour, timeUntilNextHour)

    println("I will tick in ${formatRelativeTime(timeUntilNextHour)}")

    while (true) {
        println("Current hour is currently ${LocalDateTime.now().hour}")
        println("Next hour, the channel name will be ${getChannelName(LocalDateTime.now().hour + 1)}")

        ticker.receive()

        val name = getChannelName(LocalDateTime.now().hour)

        println("Woop, will rename to $name")

        rest.channel.patchChannel(channelId, ChannelModifyPatchRequest(Optional(name)))
    }
}

private fun getChannelName(hour: Int): String {
    return when (hour) {
        in 6..11 -> "Morgon-discord"
        in 11..13 -> "Lunch-discord"
        in 13..17 -> "Eftermiddags-discord"
        in 17..21 -> "Kvälls-discord"
        else -> "Natt-discord"
    }
}

fun formatRelativeTime(millis: Long): String {
    return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))
}