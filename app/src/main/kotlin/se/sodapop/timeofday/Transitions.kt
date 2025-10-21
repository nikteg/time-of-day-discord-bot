package se.sodapop.timeofday

import kotlinx.datetime.LocalTime

data class TimeTransition(
    val name: String,
    val time: LocalTime,
)

class Transitions(
    transitions: List<TimeTransition>,
) {
    private val sortedTransitions = transitions.sortedBy { it.time }

    fun getCurrent(localTime: LocalTime): TimeTransition = sortedTransitions.lastOrNull { it.time <= localTime } ?: sortedTransitions.last()

    fun getNext(localTime: LocalTime): TimeTransition = sortedTransitions.firstOrNull { it.time > localTime } ?: sortedTransitions.first()
}
