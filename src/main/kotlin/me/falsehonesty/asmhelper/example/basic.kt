package me.falsehonesty.asmhelper.example

import me.falsehonesty.asmhelper.dsl.At
import me.falsehonesty.asmhelper.dsl.InjectionPoint
import me.falsehonesty.asmhelper.dsl.inject
import me.falsehonesty.asmhelper.dsl.instructions.*

fun test() {
    inject {
        className = "net.minecraft.client.gui.inventory.GuiContainer"
        methodName = "mouseClicked"
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

            ifClause(JumpCondition.NOT_EQUAL) {
                methodReturn()
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
