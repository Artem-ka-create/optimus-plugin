package com.github.artemkacreate.optimusplugin.inspections.enums

enum class FileExtension(val extName: String) {
    JS("js"), TS("ts"), JSX("jsx"), TSX("tsx"), HTML("html"), VUE("vue");

    companion object {
        fun fromExtension(ext: String): FileExtension? = entries.firstOrNull {
            it.extName.equals(ext, true) }
    }
}
