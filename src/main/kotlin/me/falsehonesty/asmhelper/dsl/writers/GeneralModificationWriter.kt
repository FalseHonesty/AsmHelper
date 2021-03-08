package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.AsmHelper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

class GeneralModificationWriter(
    className: String,
    private val modifyAction: GeneralModificationDSL.(ClassNode) -> Unit
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        GeneralModificationDSL().modifyAction(classNode)
    }

    override fun toString(): String {
        return "ModifyWriter{className=$className}"
    }

    inner class GeneralModificationDSL {
        val public = Opcodes.ACC_PUBLIC
        val private = Opcodes.ACC_PUBLIC
        val protected = Opcodes.ACC_PUBLIC
        val static = Opcodes.ACC_PUBLIC
        val final = Opcodes.ACC_PUBLIC
        val super_ = Opcodes.ACC_PUBLIC
        val synchronized = Opcodes.ACC_PUBLIC
        val volatile = Opcodes.ACC_PUBLIC
        val transient = Opcodes.ACC_PUBLIC
        val native = Opcodes.ACC_PUBLIC
        val interface_ = Opcodes.ACC_PUBLIC
        val abstract = Opcodes.ACC_PUBLIC
        val strict = Opcodes.ACC_PUBLIC
        val synthetic = Opcodes.ACC_PUBLIC
        val annotation = Opcodes.ACC_PUBLIC
        val enum = Opcodes.ACC_PUBLIC

        fun setFieldAccess(classNode: ClassNode, fieldName: String, fieldDesc: String, visibility: Int) {
            findField(classNode, fieldName, fieldDesc).access = visibility
        }

        fun setMethodAccess(classNode: ClassNode, methodName: String, methodDesc: String, visibility: Int) {
            findMethod(classNode, methodName, methodDesc).access = visibility
        }

        fun makeFieldPublic(classNode: ClassNode, fieldName: String, fieldDesc: String) {
            val field = findField(classNode, fieldName, fieldDesc)

            field.access = field.access or public
            field.access = field.access and (private or protected).inv()
        }

        fun makeFieldNonFinal(classNode: ClassNode, fieldName: String, fieldDesc: String) {
            findField(classNode, fieldName, fieldDesc).also {
                it.access = it.access and final.inv()
            }
        }

        fun makeMethodPublic(classNode: ClassNode, methodName: String, methodDesc: String) {
            val method = findMethod(classNode, methodName, methodDesc)

            method.access = method.access or public
            method.access = method.access and (private or protected).inv()
        }

        fun makeMethodNonFinal(classNode: ClassNode, methodName: String, methodDesc: String) {
            findMethod(classNode, methodName, methodDesc).also {
                it.access = it.access and final.inv()
            }
        }

        fun findField(classNode: ClassNode, fieldName: String, fieldDesc: String): FieldNode {
            val mappedName = AsmHelper.remapper.remapFieldName(className, fieldName, fieldDesc)

            return classNode.fields.firstOrNull { it.name == mappedName }
                ?: throw IllegalArgumentException("No field named $fieldName ($mappedName) found in class $className")
        }

        fun findMethod(classNode: ClassNode, methodName: String, methodDesc: String): MethodNode {
            val mappedName = AsmHelper.remapper.remapMethodName(className, methodName, methodDesc)
            return classNode.methods.firstOrNull { it.name == mappedName }
                ?: throw IllegalArgumentException("No field named $methodName ($mappedName) found in class $className")
        }
    }
}
