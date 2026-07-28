package com.github.artemkacreate.optimusplugin.inspections.util

object AriaConstants {

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

    /**
     * Global ARIA states/properties that are supported by **every** role
     * (they are inherited from the base `roletype` role in WAI-ARIA 1.2).
     *
     * Any element, regardless of its role, may legally carry these attributes,
     * so `role-supports-aria-props` must always treat them as allowed.
     */
    val GLOBAL_ARIA_ATTRIBUTES: Set<String> = setOf(
        "aria-atomic",
        "aria-busy",
        "aria-controls",
        "aria-current",
        "aria-describedby",
        "aria-description",
        "aria-details",
        "aria-disabled",
        "aria-dropeffect",   // deprecated in 1.1 but still valid
        "aria-errormessage",
        "aria-flowto",
        "aria-grabbed",      // deprecated in 1.1 but still valid
        "aria-haspopup",
        "aria-hidden",
        "aria-invalid",
        "aria-keyshortcuts",
        "aria-label",
        "aria-labelledby",
        "aria-live",
        "aria-owns",
        "aria-relevant",
        "aria-roledescription",
        "aria-braillelabel",
        "aria-brailleroledescription"
    )

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
}
