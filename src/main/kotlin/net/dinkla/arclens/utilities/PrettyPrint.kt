package net.dinkla.arclens.utilities

import net.dinkla.arclens.domain.kotlinlang.ClassModifier
import net.dinkla.arclens.domain.kotlinlang.ClassParameter
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionParameter
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.InheritanceModifier
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.MemberModifier
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.PropertyModifier
import net.dinkla.arclens.domain.kotlinlang.TypeAlias
import net.dinkla.arclens.domain.kotlinlang.VisibilityModifier

fun KotlinFile.prettyPrint(): String {
    var isLineNeeded = false
    return buildString {
        appendLine("package $packageName")
        appendLine()
        if (imports.isNotEmpty()) {
            appendLine(imports.joinToString("\n") { it.prettyPrint() })
            isLineNeeded = true
        }
        if (isLineNeeded) {
            appendLine()
            isLineNeeded = false
        }
        if (typeAliases.isNotEmpty()) {
            appendLine(typeAliases.joinToString("\n") { it.prettyPrint() })
            isLineNeeded = true
        }
        if (isLineNeeded) {
            appendLine()
            isLineNeeded = false
        }
        if (properties.isNotEmpty()) {
            appendLine(properties.joinToString("\n") { it.prettyPrint() })
            isLineNeeded = true
        }
        if (isLineNeeded) {
            appendLine()
            isLineNeeded = false
        }
        if (functions.isNotEmpty()) {
            appendLine(functions.joinToString("\n") { it.prettyPrint() })
            isLineNeeded = true
        }
        if (isLineNeeded) {
            appendLine()
            isLineNeeded = false
        }
        if (classes.isNotEmpty()) {
            appendLine(classes.joinToString("\n") { it.prettyPrint() })
            isLineNeeded = true
        }
    }
}

fun Import.prettyPrint(): String = "import $name"

fun TypeAlias.prettyPrint(): String = "typealias $name = $def"

fun ClassModifier?.prettyPrint() =
    when (this) {
        null -> ""
        else -> text
    }

fun ClassParameter.prettyPrint(): String {
    val visibility = addSpaceAfter(visibilityModifier.prettyPrint())
    val property = addSpaceAfter(propertyModifier.prettyPrint())
    return "$visibility$property$name: $type"
}

fun ClassSignature.prettyPrint(): String {
    val visMod = addSpaceAfter(visibilityModifier.prettyPrint())
    val classMod = addSpaceAfter(classModifier.prettyPrint())
    val inhMod = addSpaceAfter(inheritanceModifier.prettyPrint())
    val type = elementType.text
    val prettyParameters: String =
        if (parameters.isEmpty()) "" else parameters.joinToString(", ") { it.prettyPrint() }
    val inherited = if (superTypes.isEmpty()) "" else " : " + superTypes.joinToString(", ")

    val joined =
        declarations
            .joinToString("\n") {
                val text =
                    when (it) {
                        is FunctionSignature -> it.prettyPrint()
                        is Property -> it.prettyPrint()
                        is ClassSignature -> it.prettyPrint()
                        else -> ""
                    }
                "    $text"
            }
    val joined2 =
        if (joined.isNotEmpty()) {
            "\n$joined\n"
        } else {
            joined
        }
    return "$visMod$classMod$inhMod$type $name($prettyParameters)$inherited {$joined2}"
}

fun FunctionParameter.prettyPrint(): String = "$name: $type"

fun FunctionSignature.prettyPrint(): String {
    val prettyReturnType = if (returnType == null) "" else ": $returnType"
    val prettyParameters: String =
        if (parameters.isEmpty()) "" else parameters.joinToString(", ") { it.prettyPrint() }
    val ext = if (extensionOf == null) "" else "$extensionOf."
    val visibility = addSpaceAfter(visibilityModifier.prettyPrint())
    val memberMod = addSpaceAfter(memberModifier.prettyPrint())
    return "${visibility}${memberMod}fun $ext$name($prettyParameters)$prettyReturnType"
}

fun InheritanceModifier?.prettyPrint() =
    when (this) {
        null -> ""
        else -> text
    }

fun MemberModifier?.prettyPrint() =
    when (this) {
        null -> ""
        else -> text
    }

fun Property.prettyPrint(): String {
    val mMod = addSpaceAfter(memberModifier.map { it.prettyPrint() }.sortedDescending().joinToString(" "))
    val vMod = addSpaceAfter(visibilityModifier.prettyPrint())
    val mod = modifier.text
    val type = if (dataType != null) " : $dataType" else ""
    return "$mMod$vMod$mod $name$type"
}

fun PropertyModifier?.prettyPrint() =
    when (this) {
        null -> ""
        else -> text
    }

fun VisibilityModifier?.prettyPrint() =
    when (this) {
        null -> ""
        else -> text
    }
