package com.github.artemkacreate.optimusplugin.inspections.util

import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.enums.TechnologyType
import com.intellij.psi.PsiFile
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText

/**
 * Shared utility methods for accessibility rules.
 * Centralizes common patterns: attribute lookup, dynamic binding extraction,
 * numeric parsing, content checks, and ARIA label detection.
 */
object ExtractionTool {

    // ──────────────────────────────────────────────
    // Dynamic binding detection & extraction
    // ──────────────────────────────────────────────

    /**
     * Checks if an attribute name represents a dynamic binding (Vue/Angular).
     * E.g., ":href", "v-bind:alt", "\[tabindex\]"
     */
    fun isDynamicBinding(attrName: String): Boolean {
        val lower = attrName.lowercase()
        return HtmlConstants.DYNAMIC_PREFIXES.any { lower.startsWith(it) }
    }

    /**
     * Tries to extract a string literal from a dynamic binding value.
     * E.g.:
     * - "'photo of cat'" → "photo of cat"
     * - "\"hello\"" → "hello"
     * - "`static text`" → "static text"
     * Returns null if the value is a complex expression (variable, function call, etc.)
     */
    fun extractLiteralFromBinding(value: String?): String? {
        if (value == null) return null
        val trimmed = value.trim()
        if (trimmed.length < 2) return null

        val first = trimmed.first()
        val last = trimmed.last()

        // Single or double quoted string literal
        if ((first == '\'' && last == '\'') || (first == '"' && last == '"')) {
            return trimmed.substring(1, trimmed.length - 1)
        }

        // Template literal with backticks (only if no interpolation)
        if (first == '`' && last == '`' && !trimmed.contains("\${")) {
            return trimmed.substring(1, trimmed.length - 1)
        }

        return null
    }

    /**
     * Resolves the effective attribute value considering dynamic bindings and JSX expressions.
     * - For Vue/Angular dynamic bindings (:attr, v-bind:attr, [attr]): extracts literal from binding
     * - For JSX expressions ({value}): unwraps and extracts literal or raw inner value
     * - For static attributes: returns raw value directly
     * Returns null for complex expressions that cannot be resolved statically.
     */
    fun resolveAttributeValue(attr: XmlAttribute): String? {
        val rawValue = attr.value ?: return null

        // Vue/Angular dynamic binding
        if (isDynamicBinding(attr.name)) {
            // Prefer a quoted string literal (:aria-hidden="'true'"), but also
            // support bare simple values like booleans/numbers/identifiers
            // (:aria-hidden="true", :tabindex="0").
            return extractLiteralFromBinding(rawValue) ?: rawValue.trim().takeIf { isSimpleValue(it) }
        }

        // JSX expression: alt={"some text"} or tabIndex={5}
        val unwrapped = unwrapJsxExpression(rawValue)
        if (unwrapped != null) return unwrapped

        // Plain static value
        return rawValue
    }

    /**
     * Unwraps a JSX expression value.
     * - "{5}" → "5"
     * - "{'hello'}" → "hello"
     * - "{\"text\"}" → "text"
     * - "{`template`}" → "template"
     * - "{{5}}" (Vue) → "5"
     * Returns null if not a JSX/Vue expression or if it contains a complex expression.
     */
    fun unwrapJsxExpression(value: String): String? {
        val trimmed = value.trim()
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) return null

        val inner = trimmed.substring(1, trimmed.length - 1).trim()
        if (inner.isEmpty()) return null

        // Handle Vue double braces: {{value}}
        if (inner.startsWith("{") && inner.endsWith("}")) {
            val vueInner = inner.substring(1, inner.length - 1).trim()
            return extractLiteralFromBinding(vueInner)
                ?: vueInner.takeIf { it.toIntOrNull() != null || isSimpleValue(it) }
        }

        // Try to extract string literal from inside: {'text'} or {"text"} or {`text`}
        val literal = extractLiteralFromBinding(inner)
        if (literal != null) return literal

        // If inner is a simple value (number, identifier without dots/calls), return as-is
        if (isSimpleValue(inner)) return inner

        // Complex expression — cannot resolve statically
        return null
    }

    /**
     * Checks if a value is "simple" — a number or a plain identifier without function calls.
     */
    private fun isSimpleValue(value: String): Boolean {
        // Numbers: "5", "-1", "0"
        if (value.toIntOrNull() != null) return true
        // Simple identifier (no dots, parentheses, brackets)
        return value.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))
    }

    // ──────────────────────────────────────────────
    // Numeric value parsing
    // ──────────────────────────────────────────────

    /**
     * Parses numeric value from various formats:
     * - "5" (plain HTML)
     * - "{5}" (JSX expression)
     * - "{{5}}" (Vue template expression)
     * Returns null if value cannot be parsed as integer.
     */
    fun parseNumericValue(raw: String): Int? {
        val trimmed = raw.trim()
        // Try plain number first: tabindex="5"
        trimmed.toIntOrNull()?.let { return it }
        // Try JSX expression: tabIndex={5}
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            // Handle Vue double braces: {{5}}
            if (inner.startsWith("{") && inner.endsWith("}")) {
                return inner.substring(1, inner.length - 1).trim().toIntOrNull()
            }
            return inner.toIntOrNull()
        }
        return null
    }

    fun normalizeAttrName(attrName: String): String {
        val attrNameToLower = attrName.lowercase().replace("\"", "").replace("" + "\'", "")
        // maybe add replace - to ""
        return when {
            attrNameToLower.startsWith("v-bind:") -> attrNameToLower.removePrefix("v-bind:")  // v-bind:aria-label → aria-label
            attrNameToLower.startsWith(":") -> attrNameToLower.removePrefix(":")              // :aria-label → aria-label
            attrNameToLower.startsWith("[attr.") && attrNameToLower.endsWith("]") -> attrNameToLower.removePrefix("[attr.")
                .removeSuffix("]")            // [attr.aria-label] → aria-label
            attrNameToLower.startsWith("[") && attrNameToLower.endsWith("]") -> attrNameToLower.removePrefix("[")
                .removeSuffix("]")                 // [aria-label] → aria-label
            else -> attrNameToLower                                                 // aria-label → aria-label
        }
    }

    /**
     * Returns `true` when this tag is a *native* HTML element, and `false` when
     * it is a framework component that only looks like one in a case-insensitive
     * comparison (React/Vue `<Input>`, `<Button>`; web components / Angular /
     * Vue kebab-case `<app-input>`, `<my-button>`).
     *
     * Detection combines three signals so it works for HTML, Vue, Angular and
     * React across `.html`, `.vue`, `.jsx` and `.tsx` files:
     *  1. **Hyphen** → custom element / web component (the HTML spec *requires*
     *     custom elements to contain a `-`). Never native.
     *  2. **Casing** → in JSX/Vue/Angular a name with an uppercase letter is a
     *     component reference (`<Input>`, `<Button>`), so native names must be
     *     lowercase there. Plain HTML is case-insensitive per spec, so uppercase
     *     native tags like `<MARQUEE>` are still recognized (whitelist only).
     *  3. **Whitelist** → the (lowercased) name must be a known HTML element in
     *     \[CommonValues.NATIVE_HTML_TAGS\].
     */
    fun XmlTag.isNativeHtmlElement(): Boolean {
        val raw = name
        if (raw.isEmpty() || '-' in raw ) return false

        val isComponentFramework = when (containingFile?.getFileTechnologyType()) {
            TechnologyType.REACT, TechnologyType.VUE, TechnologyType.ANGULAR -> true
            else -> false // VANILLA / plain HTML
        }
        if (isComponentFramework && raw != raw.lowercase()) return false
        return raw.lowercase() in HtmlConstants.NATIVE_HTML_TAGS
    }

    fun XmlTag.isHtmlTag(vararg tagNames: String): Boolean {
        if (!isNativeHtmlElement()) return false
        val normalized = name.lowercase()
        return tagNames.any { it == normalized }
    }

    fun XmlTag.nativeTagNameOrNull(): String? =
        if (isNativeHtmlElement()) name.lowercase() else null

    // ──────────────────────────────────────────────
    // Content & ARIA checks
    // ──────────────────────────────────────────────

    /**
     * Checks if an XmlTag has visible text content (text nodes, child tags, or trimmed text).
     */
    fun hasTextContent(element: XmlTag): Boolean {
        return element.value.children.isNotEmpty() || element.subTags.isNotEmpty() || element.value.trimmedText.isNotBlank()
    }

    fun XmlTag.containsXmlTextNonRecursive(): Boolean {
        return SyntaxTraverser.psiTraverser(this).filter(XmlText::class.java).firstOrNull() != null
    }

    /**
     * Collects and concatenates the visible text of all nested XmlText elements,
     * normalized to a single space-separated, trimmed string.
     * E.g. `<a>Click <span>here</span></a>` → "Click here"
     */
    fun XmlTag.collectNestedText(): String {
        return SyntaxTraverser.psiTraverser(this).filter(XmlText::class.java)
            .mapNotNull { it.value.trim().takeIf(String::isNotEmpty) }.joinToString(" ").trim()
    }

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


    /**
     * Checks if element has aria-label or aria-labelledby (in any binding form).
     */
    fun hasAriaLabel(element: XmlTag): Boolean {
        return element.attributes.any { it.name.lowercase() in AriaConstants.ARIA_LABEL_ATTRIBUTES }
    }

    // ──────────────────────────────────────────────
    // Tag nesting helpers
    // ──────────────────────────────────────────────

    /**
     * Walks UP the tree and checks whether \[element\] is nested inside a tag
     * whose name matches \[ancestorTagName\] (case-insensitive).
     * E.g. an <input> nested inside a <label>.
     */
    fun isNestedInsideTag(element: XmlTag, ancestorTagName: String): Boolean {
        var parent = element.parentTag
        while (parent != null) {
            if (parent.name.equals(ancestorTagName, ignoreCase = true)) return true
            parent = parent.parentTag
        }
        return false
    }

    /**
     * Walks DOWN the tree and checks whether \[element\] contains any descendant tag
     * whose name (lowercased) is in \[tagNames\].
     * E.g. a <label> containing a nested <input>/<select>/<textarea>.
     */
    fun hasNestedTag(element: XmlTag, tagNames: Set<String>): Boolean {
        return generateSequence(element.subTags.asList()) { tags ->
            tags.flatMap { it.subTags.asList() }.takeIf { it.isNotEmpty() }
        }.flatten().any { it.name.lowercase() in tagNames }
    }
}
