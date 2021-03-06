package dev.falsehonesty.asmhelper.example

import dev.falsehonesty.asmhelper.ClassTransformationService

class TestClassTransformerService : ClassTransformationService {
    override fun transformerClasses() = listOf("dev.falsehonesty.asmhelper.example.TestClassTransformer")
}
