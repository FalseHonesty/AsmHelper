package dev.falsehonesty.asmhelper.example

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun test() {
    inject {
        className = "net.minecraft.client.gui.inventory.GuiContainer"
        methodName = "mouseClicked"
        methodDesc = "(III)V"
        at = At(InjectionPoint.TAIL)

        insnList {
            aload(0)
            iload(3)

            bipush(100)
            isub()

            invoke(
                InvokeType.VIRTUAL,
                "net/minecraft/client/gui/inventory/GuiContainer",
                "checkHotbarKeys",
                "(I)Z"
            )
            val hotbarKeyPressed = istore()

            load(hotbarKeyPressed)
            ifClause(JumpCondition.TRUE) {
                methodReturn()
            }

            array(3, "java/lang/Object") {
                aadd {
                    int(3)
                }
            }

            createInstance("e/e/", "()V")

            createInstance("e/e/", "(II)V") {
                iload(1)
                iload(2)
            }

            field(
                FieldAction.PUT_FIELD,
                "net/minecraft/client/entity/AbstractClientPlayer",
                "abstractClientPlayerHook",
                "Lio/framesplus/hook/AbstractClientPlayerHook;"
            )

            ifElseClause(JumpCondition.EQUAL) {
                ifCode {

                }

                elseCode {

                }
            }
        }
    }

    println("E")
}
