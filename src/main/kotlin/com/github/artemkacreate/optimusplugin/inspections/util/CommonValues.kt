package com.github.artemkacreate.optimusplugin.inspections.util

object CommonValues {

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
        "html", "head", "title", "base", "link", "meta", "style", "body",
        // Sectioning & content
        "address", "article", "aside", "footer", "header", "h1", "h2", "h3",
        "h4", "h5", "h6", "hgroup", "main", "nav", "section", "search",
        // Text content
        "blockquote", "dd", "div", "dl", "dt", "figcaption", "figure", "hr",
        "li", "menu", "ol", "p", "pre", "ul",
        // Inline text semantics
        "a", "abbr", "b", "bdi", "bdo", "br", "cite", "code", "data", "dfn",
        "em", "i", "kbd", "mark", "q", "rp", "rt", "ruby", "s", "samp",
        "small", "span", "strong", "sub", "sup", "time", "u", "var", "wbr",
        // Image & multimedia
        "area", "audio", "img", "map", "track", "video",
        // Embedded content
        "embed", "iframe", "object", "picture", "portal", "source",
        // SVG & MathML
        "svg", "math",
        // Scripting
        "canvas", "noscript", "script",
        // Demarcating edits
        "del", "ins",
        // Table content
        "caption", "col", "colgroup", "table", "tbody", "td", "tfoot", "th",
        "thead", "tr",
        // Forms
        "button", "datalist", "fieldset", "form", "input", "label", "legend",
        "meter", "optgroup", "option", "output", "progress", "select",
        "textarea",
        // Interactive elements
        "details", "dialog", "summary",
        // Web components host
        "slot", "template",
        // Obsolete but still parsed (targeted by no-distracting-elements etc.)
        "marquee", "blink"
    )

    val ARIA_LABEL_ATTRIBUTES = setOf(
        "aria-label",
        ":aria-label",
        "v-bind:aria-label",
        "[aria-label]",
        "aria-labelledby",
        ":aria-labelledby",
        "v-bind:aria-labelledby",
        "[aria-labelledby]"
    )
    val VALID_ARIA_ATTRIBUTES = setOf(
        // 1. Widget Attributes
        "aria-autocomplete",
        "aria-checked",
        "aria-disabled",
        "aria-errormessage",
        "aria-expanded",
        "aria-haspopup",
        "aria-hidden",
        "aria-invalid",
        "aria-label",
        "aria-labelledby",
        "aria-level",
        "aria-modal",
        "aria-multiline",
        "aria-multiselectable",
        "aria-orientation",
        "aria-placeholder",
        "aria-pressed",
        "aria-readonly",
        "aria-required",
        "aria-selected",
        "aria-sort",
        "aria-valuemax",
        "aria-valuemin",
        "aria-valuenow",
        "aria-valuetext",

        // 2. Live Region Attribute
        "aria-atomic",
        "aria-busy",
        "aria-live",
        "aria-relevant",

        // 3. Relationship Attributes
        "aria-activedescendant",
        "aria-colcount",
        "aria-colindex",
        "aria-colindextext",
        "aria-colspan",
        "aria-controls",
        "aria-describedby",
        "aria-description",
        "aria-details",
        "aria-errormessage",
        "aria-flowto",
        "aria-owns",
        "aria-posinset",
        "aria-rowcount",
        "aria-rowindex",
        "aria-rowindextext",
        "aria-rowspan",
        "aria-setsize",

        // 4. Global Attributes
        "aria-braillelabel",
        "aria-brailleroledescription",
        "aria-current",
        "aria-keyshortcuts",
        "aria-roledescription",

        // 5. Drag-and-Drop Attributes (deprecated in ARIA 1.1 but still valid)
        "aria-dropeffect",
        "aria-grabbed"
    )

    val ARIA_ROLE_ATTRIBUTE = "role"

    val VALID_ARIA_ROLE_VALUES = setOf(
        // Widgets
        "button",
        "checkbox",
        "gridcell",
        "link",
        "menuitem",
        "menuitemcheckbox",
        "menuitemradio",
        "option",
        "progressbar",
        "radio",
        "scrollbar",
        "searchbox",
        "separator",
        "slider",
        "spinbutton",
        "switch",
        "tab",
        "tabpanel",
        "textbox",
        "treeitem",

        // Composite
        "combobox",
        "grid",
        "listbox",
        "menu",
        "menubar",
        "radiogroup",
        "tablist",
        "tree",
        "treegrid",

        // Landmarks
        "banner",
        "complementary",
        "contentinfo",
        "form",
        "main",
        "navigation",
        "region",
        "search",

        // Live regions
        "alert",
        "alertdialog",
        "log",
        "marquee",
        "status",
        "timer",

        // Document structure
        "application",
        "article",
        "blockquote",
        "caption",
        "cell",
        "code",
        "columnheader",
        "definition",
        "deletion",
        "directory",
        "document",
        "emphasis",
        "feed",
        "figure",
        "group",
        "heading",
        "img",
        "insertion",
        "list",
        "listitem",
        "math",
        "meter",
        "none",
        "presentation",
        "note",
        "paragraph",
        "row",
        "rowgroup",
        "rowheader",
        "strong",
        "subscript",
        "superscript",
        "table",
        "term",
        "time",
        "toolbar",
        "tooltip"
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

    val ALL_NON_INTERACTIVE_ROLES = setOf(
        // --- Presentational / Decorative ---
        "presentation",
        "none",

        // --- Document Structure ---
        "article",
        "blockquote",
        "caption",
        "code",
        "definition",
        "deletion",
        "doc-abstract",
        "doc-acknowledgments",
        "doc-afterword",
        "doc-appendix",
        "doc-bibliography",
        "doc-chapter",
        "doc-conclusion",
        "doc-credits",
        "doc-endnotes",
        "doc-epigraph",
        "doc-epilogue",
        "doc-foreword",
        "doc-glossary",
        "doc-introduction",
        "doc-part",
        "doc-preface",
        "doc-prologue",
        "doc-toc",
        "document",
        "emphasis",
        "figure",
        "generic",
        "group",
        "heading",
        "img",
        "insertion",
        "list",
        "listitem",
        "mark",
        "math",
        "note",
        "paragraph",
        "row",
        "rowgroup",
        "rowheader", // Metadata header for a row, not an interactive cell
        "strong",
        "subscript",
        "superscript",
        "suggestion",
        "term",
        "time",
        "timer",

        // --- Landmarks & Main Regions ---
        "banner",
        "complementary",
        "contentinfo",
        "form", // Static form container, not an interactive control itself
        "main",
        "navigation",
        "region",
        "search",

        // --- Status & Live Regions (Informational) ---
        "alert",
        "log",
        "marquee",
        "status"
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

    val ALL_INTERACTIVE_ROLES = setOf(
        // --- Widget Roles (Віджетні ролі) ---
        "button",
        "checkbox",
        "gridcell",
        "link",
        "menuitem",
        "menuitemcheckbox",
        "menuitemradio",
        "option",
        "radio",
        "scrollbar",
        "searchbox",
        "slider",
        "spinbutton",
        "switch",
        "tab",
        "textbox",
        "treeitem",
        "columnheader",
        "rowheader",
        "combobox",
        "grid",
        "listbox",
        "menu",
        "menubar",
        "radiogroup",
        "tablist",
        "tree",
        "treegrid"
    )

    val REDUNDANT_TAGS_ROLES_MAP = mapOf(
        "button" to "button",
        "area" to "link",
        "article" to "article",
        "aside" to "complementary",
        "body" to "document",
        "datalist" to "listbox",
        "dd" to "definition",
        "dialog" to "dialog",
        "dl" to "list",
        "dt" to "term",
        "footer" to "contentinfo",
        "form" to "form",
        "h1" to "heading",
        "h2" to "heading",
        "h3" to "heading",
        "h4" to "heading",
        "h5" to "heading",
        "h6" to "heading",
        "header" to "banner",
        "hr" to "separator",
        "img" to "img",
        "li" to "listitem",
        "main" to "main",
        "math" to "math",
        "nav" to "navigation",
        "ol" to "list",
        "option" to "option",
        "output" to "status",
        "progress" to "progressbar",
        "section" to "region",
        "select" to "listbox",
        "summary" to "button",
        "table" to "table",
        "tbody" to "rowgroup",
        "td" to "cell",
        "textarea" to "textbox",
        "tfoot" to "rowgroup",
        "th" to "columnheader",
        "thead" to "rowgroup",
        "tr" to "row",
        "ul" to "list",
        "fieldset" to "group",
        "optgroup" to "group",
        "details" to "group"
    )

    val ROLE_TO_PREFERRED_TAGS: Map<String, List<String>> =
        REDUNDANT_TAGS_ROLES_MAP.entries.groupBy({ it.value }, { it.key })
            .mapValues { (_, tags) -> tags.sorted() } + mapOf(
            // input-type based roles (not in the tag->role map)
            "checkbox" to listOf("input[type=checkbox]"),
            "radio" to listOf("input[type=radio]"),
            "slider" to listOf("input[type=range]"),
            "spinbutton" to listOf("input[type=number]"),
            "searchbox" to listOf("input[type=search]"),
            "textbox" to listOf("input[type=text]", "textarea"),
            "link" to listOf("a[href]"),
        )
}
