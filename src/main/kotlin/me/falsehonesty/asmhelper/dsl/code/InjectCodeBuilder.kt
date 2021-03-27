package me.falsehonesty.asmhelper.dsl.code

import me.falsehonesty.asmhelper.dsl.code.modifiers.*
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class InjectCodeBuilder(codeClassNode: ClassNode, targetClassNode: ClassNode, targetMethodNode: MethodNode) :
    CodeBuilder(codeClassNode) {

    override val modifiers: List<Modifier> = listOf(
        RemoveReturnModifier(),
        AsmBlockModifier(targetMethodNode),
        CodeBlockShortcutModifier(targetMethodNode),
        LocalVarModifier(targetMethodNode),
        ShadowedMethodModifier(codeClassNode.name, targetClassNode, getMethodNode()),
        ShadowedLocalModifier(codeClassNode.name),
        ShadowedFieldModifier(codeClassNode.name, targetClassNode)
    )
}
