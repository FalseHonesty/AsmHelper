package me.falsehonesty.asmhelper.example

import me.falsehonesty.asmhelper.dsl.*

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

            method {
                type = InvokeType.VIRTUAL
                owner = "net/minecraft/client/gui/inventory/GuiContainer"
                name = "checkHotbarKeys"
                desc = "(I)Z"
            }

            ifClause(JumpCondition.NOT_EQUAL) {
                return_()
            }

            createInstance("e/e/", "()V")

            createInstance("e/e/", "(II)V") {
                iload(1)
                iload(2)
            }
        }
    }
}
