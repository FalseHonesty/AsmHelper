package me.falsehonesty.asmhelper.dsl.instructions

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * Gets an instance of a Kotlin Object.
 */
fun InsnListBuilder.getKObjectInstance(objectClassName: String) {
    field(FieldAction.GET_STATIC, objectClassName, "INSTANCE", "L$objectClassName;")
}

/**
 * Calls an instance method on a Kotlin Object.
 *
 * Behind the scenes, this produces bytecode that gets the Object instance, and
 * then calls the method.
 */
fun InsnListBuilder.invokeKOBjectFunction(
    objectClassName: String,
    methodName: String,
    methodDesc: String,
    arguments: (InsnListBuilder.() -> Unit)? = null
) {
    getKObjectInstance(objectClassName)

    invoke(InvokeType.VIRTUAL, objectClassName, methodName, methodDesc, arguments)
}

/**
 * An abstraction over iconst, bipush, sipush, and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.int(number: Int) {
    when (number) {
        -1 -> insn(InsnNode(Opcodes.ICONST_M1))
        0 -> insn(InsnNode(Opcodes.ICONST_0))
        1 -> insn(InsnNode(Opcodes.ICONST_1))
        2 -> insn(InsnNode(Opcodes.ICONST_2))
        3 -> insn(InsnNode(Opcodes.ICONST_3))
        4 -> insn(InsnNode(Opcodes.ICONST_4))
        5 -> insn(InsnNode(Opcodes.ICONST_5))
        in 6..127 -> bipush(number)
        in -127..-2 -> bipush(number)
        in 128..32768 -> sipush(number)
        in -32768..-128 -> sipush(number)
        else -> ldc(number)
    }
}

/**
 * An abstraction over fconst and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.double(number: Double) {
    when (number) {
        0.0 -> dconst_0()
        1.0 -> dconst_1()
        else -> ldc(number)
    }
}

/**
 * An abstraction over fconst and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.float(number: Float) {
    when (number) {
        0f -> fconst_0()
        1f -> fconst_1()
        2f -> fconst_2()
        else -> ldc(number)
    }
}

/**
 * An abstraction over lconst and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.long(number: Long) {
    when (number) {
        0L -> lconst_0()
        1L -> lconst_1()
        else -> ldc(number)
    }
}

/**
 * Helper for creating an if clause.
 *
 * Jumps into the provided code if and only if the provided condition(s) is/are TRUE.
 * NOTE: This works somewhat inversely to a normal if statement. The code inside the if
 * will be SKIPPED if at least ONE of your jump conditions is true. While this may seem counterintuitive,
 * it better lines up with how JVM Bytecode actually works.
 *
 * If you have multiple conditions, they will be called in the order they are passed. Because of that,
 * you must set up the stack accordingly.
 */
inline fun InsnListBuilder.ifClause(vararg conditions: JumpCondition, code: InsnListBuilder.() -> Unit) {
    val label = makeLabel()

    for (condition in conditions) {
        jump(condition, label)
    }

    this.code()

    placeLabel(label)
}

/**
 * A helper function to create a new instance of a class.
 *
 * This is simply a helper wrapper around the sequence of calls necessary to create a new object (new, dup, invokespecial)
 */
inline fun InsnListBuilder.createInstance(
    className: String,
    constructorDescription: String,
    parameters: InsnListBuilder.() -> Unit = {}
) {
    new(className)
    dup()

    this.parameters()

    invoke(
        InvokeType.SPECIAL,
        className,
        "<init>",
        constructorDescription
    )
}

inline fun InsnListBuilder.ifElseClause(vararg conditions: JumpCondition, builder: IfElseBuilder.() -> Unit) {
    val ifElse = IfElseBuilder(toInjectInto)

    ifElse.builder()

    val ifLabel = makeLabel()
    val endLabel = makeLabel()

    for (cond in conditions) {
        jump(cond, ifLabel)
    }

    insertInsns(ifElse.elseCode)

    jump(JumpCondition.GOTO, endLabel)

    placeLabel(ifLabel)

    insertInsns(ifElse.ifCode)

    placeLabel(endLabel)
}

class IfElseBuilder(val methodNode: MethodNode) {
    var ifCode = InsnList()
    var elseCode = InsnList()

    fun ifCode(builder: InsnListBuilder.() -> Unit) {
        val insn = InsnListBuilder(methodNode)

        insn.builder()

        ifCode = insn.build()
    }

    fun elseCode(builder: InsnListBuilder.() -> Unit) {
        val insn = InsnListBuilder(methodNode)

        insn.builder()

        elseCode = insn.build()
    }
}

enum class BooleanLogic {
    AND,
    OR
}