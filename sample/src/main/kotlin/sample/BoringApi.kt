package sample

import f2.dsl.fnc.F2Consumer
import f2.dsl.fnc.F2Supplier
import f2.dsl.fnc.f2Consumer
import f2.dsl.fnc.f2Function
import f2.dsl.fnc.f2Supplier
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import kotlinx.coroutines.flow.flowOf

/**
 * Does some boring stuff
 * @d2 api
 * @parent [BoringInterfacePage]
 */
class BoringApi {
    /**
     * So boring, can't help but sleep
     * @d2 command
     * @return true if slept, false else
     */
    fun sleep(duration: Long): Boolean = duration > 0

    /**
     * Doesn't even bother to do anything
     * @d2 command
     */
    fun procrastinate(query: String) { TODO() }

    /**
     * Would consume stuff if it weren't lazy
     * @d2 command
     */
    @RolesAllowed("get_stuff", "consume_stuff")
    fun consume(): F2Consumer<Long> = f2Consumer { }

    /**
     * Probably does stuff but it's not really interesting
     * @d2 command
     */
    @PermitAll
    fun doStuff(): BoringBoringFunction = f2Function { true }

    /**
     * Supplies a useless not-ever-changing indicator
     */
    fun supply(): F2Supplier<Boolean> = f2Supplier { flowOf(true) }

    /**
     * Annoying function
     */
    fun getBoring(): BoringGetQueryFunction = f2Function { TODO() }
}
