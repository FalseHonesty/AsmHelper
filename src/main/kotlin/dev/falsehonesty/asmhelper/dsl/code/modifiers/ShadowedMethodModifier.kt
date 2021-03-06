package dev.falsehonesty.asmhelper.dsl.code.modifiers

import dev.falsehonesty.asmhelper.printing.prettyString
import dev.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class ShadowedMethodModifier(codeBlockClass: String, val targetClassNode: ClassNode, val codeBlockMethod: MethodNode) :
    ShadowedModifier(codeBlockClass) {
    override fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        if (node.desc.contains("kotlin/jvm/functions/")) {
            // The field returns a Function lambda, this has to be a shadowed method.

            manipulateShadowedMethodCall(instructions, node, shadowedName)

            return
        }
    }

    private fun manipulateShadowedMethodCall(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        /*
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1.$getChatOpen : Lkotlin/jvm/functions/Function0;
        INVOKEINTERFACE kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; (itf)
        CHECKCAST java/lang/Boolean
         */

        /*
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$printChatMessageWithOptionalDeletion : Lkotlin/jvm/functions/Function2;
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$local1 : Lnet/minecraft/util/IChatComponent;
        SIPUSH 1337
        INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
        INVOKEINTERFACE kotlin/jvm/functions/Function2.invoke (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; (itf)
        POP
         */

        verbose("${node.prettyString()} looks to be a shadowed method. Transforming now...")

        var searchNode = node.next
        val finalCall: MethodInsnNode

        while (true) {
            if (searchNode is MethodInsnNode && searchNode.itf && searchNode.name == "invoke" && node.desc.substring(
                    1,
                    node.desc.length - 1
                ) == searchNode.owner
            ) {
                finalCall = searchNode
                break
            } else if (searchNode is MethodInsnNode && searchNode.owner.startsWith("java/lang") && searchNode.name == "valueOf") {
                // This is a primitive -> wrapper call, we probably don't want it!

                verbose("Found primitive -> wrapper call, likely an artifact from Kotlin's boxing, so let's toss it.")
                verbose(searchNode.prettyString())

                val tmp = searchNode.next
                instructions.remove(searchNode)
                searchNode = tmp

                continue
            }

            searchNode = searchNode.next
        }

        // Since all of the arguments passed to the shadowed method are generic, they will be erased at runtime.
        // Because of this, we need to analyze the stack to find out what is actually being passed in.
        val numberOfArguments = Type.getArgumentTypes(finalCall.desc).size

        val analyzedFrame = Analyzer(instructions, codeBlockMethod).analyze(node.next, finalCall)

        val argumentTypes = arrayOfNulls<Type>(numberOfArguments)

        repeat(numberOfArguments) { index ->
            argumentTypes[index] = analyzedFrame.pop()
        }

        argumentTypes.reverse()

        verbose("Theoretically, the stack would look like this when the method is called: ${argumentTypes.toList()}")
        verbose("We want the top $numberOfArguments from it.")

        // Now we need to know the return type.
        val returnTypeIndicator = finalCall.next

        val returnType = if (returnTypeIndicator is TypeInsnNode) {
            // This must be a CHECKCAST, so we can snag the return type from here.
            val rawType = Type.getObjectType(returnTypeIndicator.desc)

            wrappedTypeToPrimitive(rawType)
        } else {
            // This ought to be a POP (to discard the return value) meaning it must be void/unit.
            Type.VOID_TYPE
        }

        verbose("The return type is believed to be $returnType")

        val syntheticMethodDesc = Type.getMethodDescriptor(returnType, *argumentTypes)

        verbose("Synthetic method description has been formed: $syntheticMethodDesc")

        // TODO: Remap?

        // Now we want to remove all these instructions to replace them with a valid method call to the shadowed method.
        val methodCall = if (shadowedName.startsWith("super")) {
            val methodName = shadowedName.substring(5)
            MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                targetClassNode.superName,
                if (methodName.startsWith("_")) methodName.substring(1) else methodName.decapitalize(),
                syntheticMethodDesc,
                false
            )
        } else {
            val methodName = if (shadowedName.startsWith("_super")) {
                shadowedName.substring(1)
            } else shadowedName
            MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                targetClassNode.name,
                methodName,
                syntheticMethodDesc,
                false
            )
        }

        verbose("- ${node.prettyString()}")
        instructions.remove(node)

        instructions.insertBefore(finalCall, methodCall)
        instructions.remove(finalCall)

        verbose("- ${finalCall.prettyString()}")
        verbose("+ ${methodCall.prettyString()}")

        val possibleWrapperToPrim = returnTypeIndicator.next

        verbose("- ${returnTypeIndicator.prettyString()}")
        instructions.remove(returnTypeIndicator)

        if (possibleWrapperToPrim is MethodInsnNode && possibleWrapperToPrim.owner.startsWith("java/lang/") && possibleWrapperToPrim.name.endsWith(
                "Value"
            ) && possibleWrapperToPrim.desc.startsWith("()")
        ) {
            // We're pretty sure this is a Wrapper -> Primitive call, so we need to remove it!
            verbose("- ${possibleWrapperToPrim.prettyString()}")
            instructions.remove(possibleWrapperToPrim)
        }
    }
}
