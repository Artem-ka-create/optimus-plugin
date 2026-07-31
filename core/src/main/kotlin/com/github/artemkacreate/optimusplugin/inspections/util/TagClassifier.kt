package com.github.artemkacreate.optimusplugin.inspections.util

import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.enums.TechnologyType
import com.intellij.psi.PsiFile

object TagClassifier {

    fun PsiFile.getFileTechnologyType(): TechnologyType {
        val fileName = this.name.lowercase().trim()

        val isAngularFile =
            fileName.endsWith(".component.${FileExtension.TS.extName}") || fileName.endsWith(".component.${FileExtension.JS.extName}")
        val isVueFile = fileName.endsWith(".${FileExtension.VUE.extName}")
        val isReactFile =
            fileName.endsWith(".${FileExtension.TSX.extName}") || fileName.endsWith(".${FileExtension.JSX.extName}") || fileName.endsWith(
                ".${FileExtension.JS.extName}"
            )

        return when {
            isAngularFile -> TechnologyType.ANGULAR
            isVueFile -> TechnologyType.VUE
            isReactFile -> TechnologyType.REACT
            else -> TechnologyType.VANILLA
        }
    }
}
