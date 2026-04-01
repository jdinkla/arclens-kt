package net.dinkla.arclens.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.serialization.SerializationException
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.utilities.loadJson

abstract class AbstractCommand(
    private val help: String,
    name: String? = null,
) : CliktCommand(name) {
    override fun help(context: Context) = help

    private val model by argument(
        help = "Path to the model file",
    ).file(mustExist = true, canBeDir = false, canBeFile = true)

    protected fun loadProject(): Project =
        try {
            model.loadJson<Project>()
        } catch (e: SerializationException) {
            echo("ERROR: Failed to parse JSON file '${model.absolutePath}'", err = true)
            echo("       ${e.message}", err = true)
            throw ProgramResult(1)
        } catch (e: Exception) {
            echo("ERROR: Failed to read or parse model file '${model.absolutePath}'", err = true)
            echo("       ${e.message}", err = true)
            throw ProgramResult(1)
        }
}
