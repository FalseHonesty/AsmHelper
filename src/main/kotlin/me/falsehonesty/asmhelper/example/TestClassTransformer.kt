package me.falsehonesty.asmhelper.example

import me.falsehonesty.asmhelper.BaseClassTransformer
import me.falsehonesty.asmhelper.dsl.*
import me.falsehonesty.asmhelper.dsl.instructions.*
import me.falsehonesty.asmhelper.dsl.writers.AccessType

class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
        injectDrawSplashScreen()

        world()
    }

    private fun injectCountPrint() = overwrite {
        className = "net.minecraft.client.gui.GuiNewChat"
        methodName = "printChatMessage"
        methodDesc = "(Lnet/minecraft/util/IChatComponent;)V"
//        at = At(InjectionPoint.HEAD)

        insnList {
            field(FieldAction.GET_STATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            createInstance("java/lang/StringBuilder", "()V")

            val testMessagesSent = Descriptor("net/minecraft/client/gui/GuiNewChat", "testMessagesSent", "I")

            getLocalField(testMessagesSent)
            invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;")

            invoke(
                InvokeType.VIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
            ) {
                argument {
                    ldc(" messages sent so far")
                }
            }

            invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;")
            invoke(InvokeType.VIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V")

            updateLocalField(testMessagesSent) {
                bipush(1)
                iadd()
            }

            methodReturn()
        }
    }

    private fun injectCountField() = applyField {
        className = "net.minecraft.client.gui.GuiNewChat"
        accessTypes = listOf(AccessType.PRIVATE)
        fieldName = "testMessagesSent"
        fieldDesc = "I"
        initialValue = 0
    }

    private fun injectSuper() = inject {
        className = "net.minecraft.entity.EntityLivingBase"
        methodName = "getLook"
        methodDesc = "(F)Lnet/minecraft/util/Vec3;"
        at = At(InjectionPoint.HEAD)

        insnList {
            aload(0)
            instanceof("net/minecraft/entity/EntityLivingBase")

            field(FieldAction.GET_STATIC, "xxx", "xxx", "xxx")

            ifClause(JumpCondition.NOT_EQUAL, JumpCondition.NOT_EQUAL) {
                aload(0)
                fload(1)
                invoke(InvokeType.SPECIAL, "net/minecraft/entity/Entity", "getLook", "(F)Lnet/minecraft/util/Vec3;")
                areturn()
            }
        }
    }

    private fun injectDrawSplashScreen() = overwrite {
        className = "net.minecraft.client.Minecraft"
        methodName = "drawSplashScreen"
        methodDesc = "(Lnet/minecraft/client/renderer/texture/TextureManager;)V"

        insnList {
            placeLabel(makeLabel())

            invokeKOBjectFunction(
                "me/falsehonesty/asmhelper/example/TestHelper",
                "drawSplash",
                "()V"
            )

            methodReturn()
        }
    }
}
