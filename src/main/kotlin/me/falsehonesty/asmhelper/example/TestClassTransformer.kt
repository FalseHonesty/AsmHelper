package me.falsehonesty.asmhelper.example

import me.falsehonesty.asmhelper.BaseClassTransformer
import me.falsehonesty.asmhelper.dsl.*
import me.falsehonesty.asmhelper.dsl.instructions.*
import me.falsehonesty.asmhelper.dsl.writers.AccessType
import me.falsehonesty.asmhelper.dsl.writers.asm
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent

class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
        // injectDrawSplashScreen()
        injectEntityPlayer()

        world()
    }

    private fun injectCountPrint() = inject {
        className = "net.minecraft.client.gui.GuiNewChat"
        methodName = "printChatMessage"
        methodDesc = "(Lnet/minecraft/util/IChatComponent;)V"

        at = At(InjectionPoint.HEAD)

        // insnList {
        //     field(FieldAction.GET_STATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        //     createInstance("java/lang/StringBuilder", "()V")
        //
        //     val testMessagesSent = Descriptor("net/minecraft/client/gui/GuiNewChat", "testMessagesSent", "I")
        //
        //     getLocalField(testMessagesSent)
        //     invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;")
        //
        //     invoke(
        //         InvokeType.VIRTUAL,
        //         "java/lang/StringBuilder",
        //         "append",
        //         "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
        //     ) {
        //         ldc(" messages sent so far")
        //     }
        //
        //     invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;")
        //     invoke(InvokeType.VIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V")
        //
        //     updateLocalField(testMessagesSent) {
        //         bipush(1)
        //         iadd()
        //     }
        // }

        codeBlock {
            val deleteChatLine = shadowMethod<Unit, Int>()
            val printChatMessageWithOptionalDeletion = shadowMethod<Unit, IChatComponent, Int>()
            val local1 = shadowLocal<IChatComponent>()

            code {
                deleteChatLine(1337)

                if (local1.unformattedText.contains("ee")) {
                    printChatMessageWithOptionalDeletion(local1, 1337)

                    // TODO: provide api to return from target method
                    asm {
                        methodReturn()
                    }
                }
            }
        }

        // val testMessagesSent = shadowField<Int>()
        // val getChatOpen = shadowMethod<Boolean>()
        //
        // code {
        //     TestObj.printWhenChatted(testMessagesSent)
        //
        //     val open = getChatOpen()
        //     TestObj.doThing(getChatOpen())
        //     deleteChatLine()
        //
        //     testMessagesSent++
        // }
    }

    private fun injectCountField() = applyField {
        className = "net.minecraft.client.gui.GuiNewChat"
        accessTypes = listOf(AccessType.PRIVATE)
        fieldName = "testMessagesSent"
        fieldDesc = "I"
        initialValue = 0
    }

    val CANCELLABLE_EVENT = "com/chattriggers/ctjs/minecraft/listeners/CancellableEvent"
    val CLIENT_LISTENER = "com/chattriggers/ctjs/minecraft/listeners/ClientListener"
    val CRASH_REPORT_CATEGORY = "net/minecraft/crash/CrashReportCategory"
    val EFFECT_RENDERER = "net/minecraft/client/particle/EffectRenderer"
    val ENTITY = "net/minecraft/entity/Entity"
    val ENTITY_FX = "net/minecraft/client/particle/EntityFX"
    val ENTITY_ITEM = "net/minecraft/entity/item/EntityItem"
    val ENTITY_PLAYER = "net/minecraft/entity/player/EntityPlayer"
    val FILE = "java/io/File"
    val FRAME_BUFFER = "net/minecraft/client/shader/Framebuffer"
    val ICHAT_COMPONENT = "net/minecraft/util/IChatComponent"
    val INVENTORY_PLAYER = "net/minecraft/entity/player/InventoryPlayer"
    val ITEM_STACK = "net/minecraft/item/ItemStack"
    val PACKET = "net/minecraft/network/Packet"
    val TRIGGER_TYPE = "com/chattriggers/ctjs/triggers/TriggerType"


    fun injectEntityPlayer() = inject {
        className = ENTITY_PLAYER
        methodName = "dropOneItem"
        methodDesc = "(Z)L$ENTITY_ITEM;"

        at = At(
            InjectionPoint.INVOKE(
                Descriptor(
                    INVENTORY_PLAYER,
                    "getCurrentItem",
                    "()L$ITEM_STACK;"
                ),
                ordinal = 0
            ),
            shift = 2
        )

        fieldMaps = mapOf("inventory" to "field_71071_by")

        methodMaps = mapOf(
            "getCurrentItem" to "func_70448_g",
            "func_71040_bB" to "dropOneItem",
            "func_70448_g" to "getCurrentItem"
        )

        insnList {
            invokeKObjectFunction(CLIENT_LISTENER, "onDropItem", "(L$ENTITY_PLAYER;L$ITEM_STACK;)Z") {
                aload(0)

                getLocalField(ENTITY_PLAYER, "inventory", "L$INVENTORY_PLAYER;")
                invokeVirtual(INVENTORY_PLAYER, "getCurrentItem", "()L$ITEM_STACK;")
            }

            ifClause(JumpCondition.FALSE) {
                aconst_null()
                areturn()
            }
        }
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

//    private fun injectDrawSplashScreen() = overwrite {
//        className = "net.minecraft.client.Minecraft"
//        methodName = "drawSplashScreen"
//        methodDesc = "(Lnet/minecraft/client/renderer/texture/TextureManager;)V"
//
//        insnList {
//            invokeKOBjectFunction(
//                "me/falsehonesty/asmhelper/example/TestHelper",
//                "drawSplash",
//                "()V"
//            )
//
//            methodReturn()
//        }
//    }
}

object TestObj {
    fun printWhenChatted(messages: Int) {
        println("$messages printed so far.")
    }

    fun doThing(b: Boolean) {
        println("open? $b")
    }
}
