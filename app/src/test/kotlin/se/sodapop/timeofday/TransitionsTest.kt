package se.sodapop.timeofday

import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TransitionsTest {
    @Test fun `getCurrent and getNext returns correct names`() {
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

        assert(LocalTime(0, 0), "Nattdiscord", "Morgondiscord", transitions)
        assert(LocalTime(5, 9), "Nattdiscord", "Morgondiscord", transitions)
        assert(LocalTime(6, 0), "Morgondiscord", "Lunchdiscord", transitions)
        assert(LocalTime(11, 4), "Morgondiscord", "Lunchdiscord", transitions)
        assert(LocalTime(11, 5), "Morgondiscord", "Lunchdiscord", transitions)
        assert(LocalTime(13, 4), "Lunchdiscord", "Eftermiddagsdiscord", transitions)
        assert(LocalTime(13, 5), "Lunchdiscord", "Eftermiddagsdiscord", transitions)
        assert(LocalTime(16, 9), "Eftermiddagsdiscord", "Kvällsdiscord", transitions)
        assert(LocalTime(17, 0), "Kvällsdiscord", "Nattdiscord", transitions)
        assert(LocalTime(20, 9), "Kvällsdiscord", "Nattdiscord", transitions)
        assert(LocalTime(21, 0), "Nattdiscord", "Morgondiscord", transitions)
    }

    fun assert(
        localTime: LocalTime,
        current: String,
        next: String,
        transitions: Transitions,
    ) {
        assertEquals(current, transitions.getCurrent(localTime).name)
        assertEquals(next, transitions.getNext(localTime).name)
    }
}
