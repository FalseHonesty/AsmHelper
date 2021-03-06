package me.falsehonesty.asmhelper.example

import me.falsehonesty.asmhelper.ClassTransformationService

class TestClassTransformerService : ClassTransformationService {
    override fun transformerClasses() = listOf("me.falsehonesty.asmhelper.example.TestClassTransformer")
}
