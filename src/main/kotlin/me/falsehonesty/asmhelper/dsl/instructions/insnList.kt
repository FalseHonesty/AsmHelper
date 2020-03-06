package me.falsehonesty.asmhelper.dsl.instructions

import me.falsehonesty.asmhelper.AsmHelper
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

open class InsnListBuilder(val toInjectInto: MethodNode) : Opcodes {
    val insnList = InsnList()
    var currentLocalIndex = toInjectInto.maxLocals

    fun aconst_null() = apply {
        insn(InsnNode(ACONST_NULL))
    }

    fun aload(index: Int) = apply {
        insn(VarInsnNode(ALOAD, index))
    }

    fun areturn() = apply {
        insn(InsnNode(ARETURN))
    }

    fun arraylength() = apply {
        insn(InsnNode(ARRAYLENGTH))
    }

    fun astore(index: Int) = apply {
        insn(VarInsnNode(ASTORE, index))
    }

    fun athrow() = apply {
        insn(InsnNode(ATHROW))
    }

    fun bipush(value: Int) = apply {
        insn(IntInsnNode(BIPUSH, value))
    }

    fun checkcast(type: String) = apply {
        insn(TypeInsnNode(CHECKCAST, type))
    }

    fun d2f() = apply { insn(InsnNode(D2F)) }

    fun d2i() = apply { insn(InsnNode(D2I)) }

    fun d2l() = apply { insn(InsnNode(D2L)) }

    fun dadd() = apply { insn(InsnNode(DADD)) }

    fun dcmpg() = apply { insn(InsnNode(DCMPG)) }

    fun dcmpl() = apply { insn(InsnNode(DCMPL)) }

    fun dconst_0() = apply { insn(InsnNode(DCONST_0)) }

    fun dconst_1() = apply { insn(InsnNode(DCONST_1)) }

    fun ddiv() = apply { insn(InsnNode(DDIV)) }

    fun dload(index: Int) = apply { insn(VarInsnNode(DLOAD, index)) }

    fun dmul() = apply { insn(InsnNode(DMUL)) }

    fun dneg() = apply { insn(InsnNode(DNEG)) }

    fun drem() = apply { insn(InsnNode(DREM)) }

    fun dreturn() = apply { insn(InsnNode(DRETURN)) }

    fun dstore(index: Int) = apply { insn(VarInsnNode(DSTORE, index)) }

    fun dsub() = apply { insn(InsnNode(DSUB)) }

    fun dup() = apply {
        insn(InsnNode(DUP))
    }

    fun dup_x1() = apply { insn(InsnNode(DUP_X1)) }

    fun dup_x2() = apply { insn(InsnNode(DUP_X2)) }

    fun dup2() = apply { insn(InsnNode(DUP2)) }

    fun dup2_x1() = apply { insn(InsnNode(DUP2_X1)) }

    fun dup2_x2() = apply { insn(InsnNode(DUP2_X2)) }

    fun f2d() = apply { insn(InsnNode(F2D)) }

    fun f2i() = apply { insn(InsnNode(F2I)) }

    fun f2l() = apply { insn(InsnNode(F2L)) }

    fun fadd() = apply { insn(InsnNode(FADD)) }

    fun fcmpg() = apply { insn(InsnNode(FCMPG)) }

    fun fcmpl() = apply { insn(InsnNode(FCMPL)) }

    fun fconst_0() = apply { insn(InsnNode(FCONST_0)) }

    fun fconst_1() = apply { insn(InsnNode(FCONST_1)) }

    fun fconst_2() = apply { insn(InsnNode(FCONST_2)) }

    fun fdiv() = apply { insn(InsnNode(FDIV)) }

    fun fload(index: Int) = apply { insn(VarInsnNode(FLOAD, index)) }

    fun fmul() = apply { insn(InsnNode(FMUL)) }

    fun fneg() = apply { insn(InsnNode(FNEG)) }

    fun frem() = apply { insn(InsnNode(FREM)) }

    fun freturn() = apply { insn(InsnNode(FRETURN)) }

    fun fstore(index: Int) = apply { insn(VarInsnNode(FSTORE, index)) }

    fun fsub() = apply { insn(InsnNode(FSUB)) }

    fun iload(value: Int) = apply {
        insn(VarInsnNode(ILOAD, value))
    }

    fun istore(value: Int) = apply {
        insn(VarInsnNode(ISTORE, value))
    }

    fun iadd() = apply {
        insn(InsnNode(IADD))
    }

    fun ireturn() = apply {
        insn(InsnNode(IRETURN))
    }

    fun sipush(value: Int) = apply {
        insn(IntInsnNode(SIPUSH, value))
    }

    fun isub() = apply {
        insn(InsnNode(ISUB))
    }

    fun lconst_0() = apply {
        insn(InsnNode(LCONST_0))
    }

    fun lconst_1() = apply {
        insn(InsnNode(LCONST_1))
    }

    fun lstore(value: Int) = apply {
        insn(VarInsnNode(LSTORE, value))
    }

    fun lload(value: Int) = apply {
        insn(VarInsnNode(LLOAD, value))
    }

    fun instanceof(clazzName: String) = apply { insn(TypeInsnNode(INSTANCEOF, clazzName)) }

    fun new(className: String) = apply {
        insn(TypeInsnNode(NEW, className))
    }

    fun pop() = apply { insn(InsnNode(POP)) }

    fun pop2() = apply { insn(InsnNode(POP2)) }

    fun swap() = apply { insn(InsnNode(SWAP)) }

    fun ldc(constant: Any) = apply {
        insn(LdcInsnNode(constant))
    }

    fun methodReturn() = apply {
        insn(InsnNode(RETURN))
    }

    /**
     * Creates a new label, but does not place it anywhere in the bytecode,
     * it simply gives you a reference to it.
     */
    fun makeLabel() = LabelNode()

    /**
     * Places a previously created label.
     */
    fun placeLabel(label: LabelNode) = apply {
        insn(label)
    }

    fun jump(condition: JumpCondition, label: LabelNode) = apply {
        insn(JumpInsnNode(condition.opcode, label))
    }

    fun anewarray(className: String) = apply {
        insn(TypeInsnNode(ANEWARRAY, className))
    }

    fun aaload() = apply { insn(InsnNode(AALOAD)) }
    fun aastore() = apply { insn(InsnNode(AASTORE)) }

    fun baload() = apply { insn(InsnNode(BALOAD)) }
    fun bastore() = apply { insn(InsnNode(BALOAD)) }

    fun caload() = apply { insn(InsnNode(BALOAD)) }
    fun castore() = apply { insn(InsnNode(BALOAD)) }

    fun daload() = apply { insn(InsnNode(BALOAD)) }
    fun dastore() = apply { insn(InsnNode(BALOAD)) }

    fun faload() = apply { insn(InsnNode(BALOAD)) }
    fun fastore() = apply { insn(InsnNode(BALOAD)) }

    fun array(size: Int, className: String, code: ArrayBuilder.() -> Unit) = apply {
        int(size)
        anewarray(className)

        val array = ArrayBuilder(this)
        array.code()
    }

    /**
     * Gets an instance of a Kotlin Object.
     */
    fun getKObjectInstance(objectClassName: String) = apply {
        field(FieldAction.GET_STATIC, objectClassName, "INSTANCE", "L$objectClassName;")
    }

    /**
     * Calls an instance method on a Kotlin Object.
     *
     * Behind the scenes, this produces bytecode that gets the Object instance, and
     * then calls the method.
     */
    @JvmOverloads
    fun invokeKObjectFunction(objectClassName: String, methodName: String, methodDesc: String, arguments: (InsnListBuilder.() -> Unit)? = null) = apply {
        getKObjectInstance(objectClassName)

        invoke(InvokeType.VIRTUAL, objectClassName, methodName, methodDesc, arguments)
    }

    /**
     * An abstraction over iconst, bipush, sipush, and ldc, picking the best one
     * available.
     */
    fun int(number: Int) = apply {
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
    fun double(number: Double) = apply {
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
    fun float(number: Float) = apply {
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
    fun long(number: Long) = apply {
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
    fun ifClause(vararg conditions: JumpCondition, code: InsnListBuilder.() -> Unit) = apply {
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
    @JvmOverloads
    fun createInstance(className: String, constructorDescription: String, parameters: InsnListBuilder.() -> Unit = {}) = apply {
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

    fun ifElseClause(vararg conditions: JumpCondition, builder: IfElseBuilder.() -> Unit) = apply {
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

    fun getStatic(owner: String, name: String, desc: String) = apply {
        field(FieldAction.GET_STATIC, owner, name, desc)
    }

    fun getField(owner: String, name: String, desc: String) = apply {
        field(FieldAction.GET_FIELD, owner, name, desc)
    }

    fun putStatic(owner: String, name: String, desc: String) = apply {
        field(FieldAction.PUT_STATIC, owner, name, desc)
    }

    fun putField(owner: String, name: String, desc: String) = apply {
        field(FieldAction.PUT_FIELD, owner, name, desc)
    }

    fun field(action: FieldAction, descriptor: Descriptor) = this.field(action, descriptor.owner, descriptor.name, descriptor.desc)

    fun field(action: FieldAction, owner: String, name: String, desc: String) = apply {
        val realName = AsmHelper.remapper.mapFieldAccess(name)

        insnList.add(FieldInsnNode(
            action.opcode,
            owner,
            realName,
            desc
        ))
    }

    fun getLocalField(descriptor: Descriptor) = apply {
        aload(0)
        field(FieldAction.GET_FIELD, descriptor)
    }

    fun updateLocalField(descriptor: Descriptor, updater: InsnListBuilder.() -> Unit) = apply {
        aload(0)
        getLocalField(descriptor)

        this.updater()

        field(FieldAction.PUT_FIELD, descriptor)
    }

    fun setLocalField(descriptor: Descriptor, newValue: InsnListBuilder.() -> Unit) = apply {
        aload(0)

        this.newValue()

        field(FieldAction.PUT_FIELD, descriptor)
    }

    fun getLocalField(owner: String, name: String, desc: String) = apply {
        aload(0)
        field(FieldAction.GET_FIELD, owner, name, desc)
    }

    fun updateLocalField(owner: String, name: String, desc: String, updater: InsnListBuilder.() -> Unit) = apply {
        aload(0)
        getLocalField(owner, name, desc)

        this.updater()

        field(FieldAction.PUT_FIELD, owner, name, desc)
    }

    fun setLocalField(owner: String, name: String, desc: String, newValue: InsnListBuilder.() -> Unit) = apply {
        aload(0)

        this.newValue()

        field(FieldAction.PUT_FIELD, owner, name, desc)
    }

    @JvmOverloads
    fun invoke(type: InvokeType, descriptor: Descriptor, arguments: (InsnListBuilder.() -> Unit)? = null) =
        this.invoke(type, descriptor.owner, descriptor.name, descriptor.desc, arguments)

    @JvmOverloads
    fun invokeStatic(
        owner: String,
        name: String,
        desc: String,
        arguments: (InsnListBuilder.() -> Unit)? = null
    ) = apply {
        invoke(InvokeType.STATIC, owner, name, desc, arguments)
    }

    @JvmOverloads
    fun invokeVirtual(
        owner: String,
        name: String,
        desc: String,
        arguments: (InsnListBuilder.() -> Unit)? = null
    ) = apply {
        invoke(InvokeType.VIRTUAL, owner, name, desc, arguments)
    }

    @JvmOverloads
    fun invokeSpecial(
        owner: String,
        name: String,
        desc: String,
        arguments: (InsnListBuilder.() -> Unit)? = null
    ) = apply {
        invoke(InvokeType.SPECIAL, owner, name, desc, arguments)
    }

    @JvmOverloads
    fun invokeInterface(
        owner: String,
        name: String,
        desc: String,
        arguments: (InsnListBuilder.() -> Unit)? = null
    ) = apply {
        invoke(InvokeType.INTERFACE, owner, name, desc, arguments)
    }

    /**
     * Calls a specified method.
     *
     * @param owner the name of the owning class. Packages should be separated using slashes.
     * @param name the name of the method to call.
     * @param desc the method's signature. Ex. (F)Lnet/minecraft/util/Vec3;
     */
    @JvmOverloads
    fun invoke(
        type: InvokeType,
        owner: String,
        name: String,
        desc: String,
        arguments: (InsnListBuilder.() -> Unit)? = null
    ) = apply {
        val realName = AsmHelper.remapper.mapInvocation(name)

        if (arguments != null) {
            val insns = InsnListBuilder(toInjectInto)
            insns.arguments()
            insnList.add(insns.build())
        }

        insnList.add(
            MethodInsnNode(
                type.opcode,
                owner,
                realName,
                desc,
                type == InvokeType.INTERFACE
            )
        )
    }

    fun indyHandle(type: Int, owner: String, name: String, desc: String): Handle {
        return Handle(type, owner, name, desc)
    }

    fun invokeDynamic(name: String, desc: String, handle: Handle, vararg bootstrapArgs: Any?) = apply {
        insn(InvokeDynamicInsnNode(
            name,
            desc,
            handle,
            *bootstrapArgs
        ))
    }

    fun astore(): Local {
        astore(currentLocalIndex)

        return Local(currentLocalIndex++, LocalType.OBJECT)
    }

    fun fstore(): Local {
        fstore(currentLocalIndex)

        return Local(currentLocalIndex++, LocalType.FLOAT)
    }

    fun istore(): Local {
        istore(currentLocalIndex)

        return Local(currentLocalIndex++, LocalType.INT)
    }

    fun dstore(): Local {
        dstore(currentLocalIndex)

        return Local(currentLocalIndex++, LocalType.DOUBLE)
    }

    fun lstore(): Local {
        lstore(currentLocalIndex)

        return Local(currentLocalIndex++, LocalType.LONG)
    }

    fun load(local: Local) = apply {
        when (local.type) {
            LocalType.OBJECT -> aload(local.index)
            LocalType.FLOAT -> fload(local.index)
            LocalType.INT -> iload(local.index)
            LocalType.DOUBLE -> dload(local.index)
            LocalType.LONG -> lload(local.index)
        }
    }

    fun insertInsns(list: InsnList) = apply {
        insnList.add(list)
    }

    fun build(): InsnList = insnList

    internal fun insn(node: AbstractInsnNode) = apply {
        insnList.add(node)
    }
}

enum class JumpCondition(val opcode: Int) {
    /**
     * Equivalent to IFNE and [NOT_EQUAL]
     */
    TRUE(IFNE),
    /**
     * Equivalent to IFEQ and [EQUAL]
     */
    FALSE(IFEQ),
    EQUAL(IFEQ),
    NOT_EQUAL(IFNE),
    LESS_THAN(IFLT),
    GREATER_OR_EQUAL(IFGE),
    GREATER_THAN(IFGT),
    LESS_OR_EQUAL(IFLE),
    NULL(IFNULL),
    NON_NULL(IFNONNULL),
    GOTO(Opcodes.GOTO),
    REFS_EQUAL(IF_ACMPEQ),
    REFS_NOT_EQUAL(IF_ACMPNE)
}

class ArrayBuilder(private val insns: InsnListBuilder) {
    private var currentIndex = 0

    fun aadd(code: InsnListBuilder.() -> Unit) = apply { add(code, AASTORE) }
    fun badd(code: InsnListBuilder.() -> Unit) = apply { add(code, BASTORE) }
    fun cadd(code: InsnListBuilder.() -> Unit) = apply { add(code, CASTORE) }
    fun dadd(code: InsnListBuilder.() -> Unit) = apply { add(code, DASTORE) }
    fun fadd(code: InsnListBuilder.() -> Unit) = apply { add(code, FASTORE) }

    private fun add(code: InsnListBuilder.() -> Unit, opcode: Int) = apply {
        insns.dup()
        insns.int(currentIndex++)
        insns.code()
        insns.insn(InsnNode(opcode))
    }
}

class IfElseBuilder(val methodNode: MethodNode) {
    var ifCode = InsnList()
    var elseCode = InsnList()

    fun ifCode(builder: InsnListBuilder.() -> Unit) = apply {
        val insn = InsnListBuilder(methodNode)

        insn.builder()

        ifCode = insn.build()
    }

    fun elseCode(builder: InsnListBuilder.() -> Unit) = apply {
        val insn = InsnListBuilder(methodNode)

        insn.builder()

        elseCode = insn.build()
    }
}

enum class FieldAction(val opcode: Int) {
    GET_STATIC(Opcodes.GETSTATIC),
    PUT_STATIC(Opcodes.PUTSTATIC),
    GET_FIELD(Opcodes.GETFIELD),
    PUT_FIELD(Opcodes.PUTFIELD)
}

enum class InvokeType(val opcode: Int) {
    VIRTUAL(Opcodes.INVOKEVIRTUAL),
    SPECIAL(Opcodes.INVOKESPECIAL),
    STATIC(Opcodes.INVOKESTATIC),
    INTERFACE(Opcodes.INVOKEINTERFACE)
}

data class Local(val index: Int, val type: LocalType)

enum class LocalType {
    OBJECT,
    FLOAT,
    INT,
    DOUBLE,
    LONG
}