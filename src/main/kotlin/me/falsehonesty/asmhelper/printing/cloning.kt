package me.falsehonesty.asmhelper.printing

import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LabelNode

fun InsnList.clone() = InsnList().let { list ->
    val labels = this.cloneLabels()

    this.iterator().forEach { list.add(it.clone(labels)) }
}

fun InsnList.cloneLabels() = toArray()
    .toList()
    .filterIsInstance<LabelNode>()
    .associateWith { LabelNode() }