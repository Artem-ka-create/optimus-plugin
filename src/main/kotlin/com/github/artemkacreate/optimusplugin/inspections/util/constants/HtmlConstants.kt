package com.github.artemkacreate.optimusplugin.inspections.util.constants

object HtmlConstants {

    val DYNAMIC_PREFIXES = listOf(":", "v-bind:", "[")

    /**
     * The complete set of *native* HTML element names (always lowercase).
     *
     * This whitelist is the source of truth used to distinguish a real HTML
     * element (`<input>`, `<button>`, `<img>`) from a framework component that
     * merely shares its name in a case-insensitive comparison
     * (React/Vue `<Input>`, `<Button>`, web components / Angular `<app-input>`).
     *
     * Only names in this set — and only when written all-lowercase and without a
     * hyphen — are treated as native tags by the accessibility rules.
     */
    val NATIVE_HTML_TAGS: Set<String> = setOf(
        // Document / metadata
        "html",
        "head",
        "title",
        "base",
        "link",
        "meta",
        "style",
        "body",
        // Sectioning & content
        "address",
        "article",
        "aside",
        "footer",
        "header",
        "h1",
        "h2",
        "h3",
        "h4",
        "h5",
        "h6",
        "hgroup",
        "main",
        "nav",
        "section",
        "search",
        // Text content
        "blockquote",
        "dd",
        "div",
        "dl",
        "dt",
        "figcaption",
        "figure",
        "hr",
        "li",
        "menu",
        "ol",
        "p",
        "pre",
        "ul",
        // Inline text semantics
        "a",
        "abbr",
        "b",
        "bdi",
        "bdo",
        "br",
        "cite",
        "code",
        "data",
        "dfn",
        "em",
        "i",
        "kbd",
        "mark",
        "q",
        "rp",
        "rt",
        "ruby",
        "s",
        "samp",
        "small",
        "span",
        "strong",
        "sub",
        "sup",
        "time",
        "u",
        "var",
        "wbr",
        // Image & multimedia
        "area",
        "audio",
        "img",
        "map",
        "track",
        "video",
        // Embedded content
        "embed",
        "iframe",
        "object",
        "picture",
        "portal",
        "source",
        // SVG & MathML
        "svg",
        "math",
        // Scripting
        "canvas",
        "noscript",
        "script",
        // Demarcating edits
        "del",
        "ins",
        // Table content
        "caption",
        "col",
        "colgroup",
        "table",
        "tbody",
        "td",
        "tfoot",
        "th",
        "thead",
        "tr",
        // Forms
        "button",
        "datalist",
        "fieldset",
        "form",
        "input",
        "label",
        "legend",
        "meter",
        "optgroup",
        "option",
        "output",
        "progress",
        "select",
        "textarea",
        // Interactive elements
        "details",
        "dialog",
        "summary",
        // Web components host
        "slot",
        "template",
        // Obsolete but still parsed (targeted by no-distracting-elements etc.)
        "marquee",
        "blink"
    )

    val DEFAULT_FOCUSABLE_ELEMENTS = setOf(
        "button", "input", "select", "textarea", "audio", "video"
    )

    val FOR_ATTRIBUTES = setOf("for", "htmlfor")

    val FIELD_LABEL_ATTRIBUTES = setOf("label")

    val LABEL_REQUIRED_NATIVE_TAGS = setOf(
        "button", "a", "input", "textarea", "select"
    )

    val ALL_INTERACTIVE_TAGS = setOf(
        "button",
        "a",
        "input",
        "select",
        "textarea",
        "option",
        "details",
        "summary",
        "area",
        "audio",
        "video",
        "embed",
        "iframe",
        "object"
    )

    val NON_INTERACTIVE_TAGS = setOf(
        "p",
        "article",
        "section",
        "nav",
        "aside",
        "header",
        "footer",
        "main",
        "address",
        "hgroup",
        "hr",
        "blockquote",
        "pre",
        "figure",
        "figcaption",
        "h1",
        "h2",
        "h3",
        "h4",
        "h5",
        "h6",
        "em",
        "strong",
        "i",
        "b",
        "u",
        "s",
        "small",
        "sub",
        "sup",
        "mark",
        "time",
        "data",
        "abbr",
        "cite",
        "dfn",
        "q",
        "samp",
        "kbd",
        "var",
        "code",
        "ul",
        "ol",
        "li",
        "dl",
        "dt",
        "dd",
        "table",
        "caption",
        "thead",
        "tbody",
        "tfoot",
        "tr",
        "th",
        "td",
        "colgroup",
        "col",
        "img",
        "picture",
        "source",
        "canvas",
        "svg",
        "math",
        "track"
    )
}
