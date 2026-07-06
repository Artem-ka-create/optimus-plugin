package com.github.artemkacreate.optimusplugin.inspections.util

object CommonValues {

    val DYNAMIC_PREFIXES = listOf(":", "v-bind:", "[")

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
        "aria-colcount",
        "aria-colindex",
        "aria-colindextext",
        "aria-colspan",
        "aria-controls",
        "aria-describedby",
        "aria-description",
        "aria-details",
        "aria-flowto",
        "aria-owns",
        "aria-posinset",
        "aria-rowcount",
        "aria-rowindex",
        "aria-rowindextext",
        "aria-rowspan",
        "aria-setsize",

        // 4. Global Attributes
        "aria-current",
        "aria-keyshortcuts",
        "aria-roledescription"
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

    val FIELD_LABEL_ATTRIBUTES = setOf("label", "output")

}
