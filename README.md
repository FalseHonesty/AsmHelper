# AsmHelper

AsmHelper is a library that makes writing ASM more comfortable. 
It has the goal of being a lightweight alternative to Sponge Mixins while still
providing similar abstractions. 

AsmHelper provides a convenient Kotlin DSL, but also tries to support Java as best
as possible.

# How to Use

To begin with this library, you need to set up an FMLLoadingPlugin like normal,
and point it to a ClassTransformer you create. This ClassTransformer then must
extend `BaseClassTransformer`.

```kotlin
class TestClassTransformer : BaseClassTransformer() {

}
```

This will hook up the class transformer to the AsmHelper library. From there,
you need to override the `makeTransformers` function in order to write your actual
transformers.

```kotlin
class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        // TODO: Make transformers!!
    }
}
```

Now we're set up and we can actually write our transformers. All of the options
we have for how to transform are available in the `method.kt` file. At the time
of writing, this includes options such as `inject` and `overwrite`. For our example,
we will use `inject` and `applyField`. To write our transformers, we need to make
some functions our `makeTransformers` function will call.

```kotlin
class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
    }

    private fun injectCountPrint() = inject {
    
    }

    private fun injectCountField() = applyField {
        
    }
}
```

We now have access to the AsmHelper DSL inside of the `inject` and `applyField` 
blocks. Both blocks will require us to specify what class we are trying to inject
into, so we'll add that.

```kotlin
class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
    }

    private fun injectCountPrint() = inject {
        className = "net.minecraft.client.gui.GuiNewChat"
    }

    private fun injectCountField() = applyField {
        className = "net.minecraft.client.gui.GuiNewChat"
    }
}

```

Let's flesh out the `applyField` block first. Fields in the JVM require a name,
a type, access modifiers, and optionally, an initial value. The DSL provided allows
all of these to be easily added like so:

```kotlin
class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
    }

    private fun injectCountPrint() = inject {
        className = "net.minecraft.client.gui.GuiNewChat"
    }

    private fun injectCountField() = applyField {
        className = "net.minecraft.client.gui.GuiNewChat"
        accessTypes = listOf(AccessType.PRIVATE)
        fieldName = "testMessagesSent"
        fieldDesc = "I"
        initialValue = 0
    }
}
```

Next, the `inject` block needs work. When injecting, you need to pick a method to
inject into, so we need to specify that. In addition, we need to the library
where we want to inject, so we can use the handy `At` class as a utility.

```kotlin
class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
    }

    private fun injectCountPrint() = inject {
        className = "net.minecraft.client.gui.GuiNewChat"
        methodName = "printChatMessage"
        at = At(InjectionPoint.HEAD)
    }

    private fun injectCountField() = applyField {
        className = "net.minecraft.client.gui.GuiNewChat"
        accessTypes = listOf(AccessType.PRIVATE)
        fieldName = "testMessagesSent"
        fieldDesc = "I"
        initialValue = 0
    }
}
```

Finally, we actually need to inject our bytecode instructions. Again, the library
provides a convenient DSL for this task. First, create an `insnList` block. Then,
you can call methods that correspond to all (well, not quite yet, WIP) of the
available JVM instructions. 

In addition to having 1 to 1 representations of JVM bytecode instructions, there
are also abstractions over some more complicated bytecode tasks. This includes
helper methods for getting & setting field values, as well as creating new object
instances.

```kotlin
class TestClassTransformer : BaseClassTransformer() {
    override fun makeTransformers() {
        injectCountField()
        injectCountPrint()
    }

    private fun injectCountPrint() = inject {
        className = "net.minecraft.client.gui.GuiNewChat"
        methodName = "printChatMessage"
        at = At(InjectionPoint.HEAD)

        insnList {
            field(FieldAction.GET_STATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            createInstance("java/lang/StringBuilder", "()V")

            val testMessagesSent = Descriptor("net/minecraft/client/gui/GuiNewChat", "testMessagesSent", "I")

            getLocalField(testMessagesSent)
            invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;")

            ldc(" messages sent so far")
            invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")

            invoke(InvokeType.VIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;")
            invoke(InvokeType.VIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V")

            updateLocalField(testMessagesSent) {
                bipush(1)
                iadd()
            }
        }
    }

    private fun injectCountField() = applyField {
        className = "net.minecraft.client.gui.GuiNewChat"
        accessTypes = listOf(AccessType.PRIVATE)
        fieldName = "testMessagesSent"
        fieldDesc = "I"
        initialValue = 0
    }
}
```