package com.github.artemkacreate.optimusplugin.inspections.util.constants

object RoleTagConstants {

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

    val ROLE_REQUIRED_ARIA_PROPS: Map<String, Set<String>> = mapOf(
        "checkbox" to setOf("aria-checked"),
        "combobox" to setOf("aria-controls", "aria-expanded"),
        "heading" to setOf("aria-level"),
        "menuitemcheckbox" to setOf("aria-checked"),
        "menuitemradio" to setOf("aria-checked"),
        "meter" to setOf("aria-valuenow"),
        "option" to setOf("aria-selected"),
        "radio" to setOf("aria-checked"),
        "scrollbar" to setOf("aria-controls", "aria-valuenow"),
        "slider" to setOf("aria-valuenow"),
        "switch" to setOf("aria-checked")
    )

    /**
     * Map of ARIA role -> the **extra** role-specific ARIA states/properties it
     * supports, *in addition to* two lists that are reused automatically:
     *   - [AriaConstants.GLOBAL_ARIA_ATTRIBUTES]  (supported by every role), and
     *   - [ROLE_REQUIRED_ARIA_PROPS] (a required prop is, by definition, supported).
     *
     * To avoid duplication, attributes already present in those two lists are NOT
     * repeated here. Use [supportedAriaPropsForRole] to obtain the full, merged
     * set of attributes allowed on an element with a given role.
     *
     * Any `aria-*` attribute outside that merged union is NOT supported by the
     * role and should be flagged by `role-supports-aria-props`.
     *
     * Values already include properties inherited from super-class roles
     * (e.g. `columnheader` includes what it inherits from `cell`/`gridcell`).
     * Source: WAI-ARIA "Supported States and Properties" + aria-query.
     */
    val ROLE_SUPPORTED_ARIA_PROPS: Map<String, Set<String>> = mapOf(
        // --- Widgets --- (required props omitted, they are merged from ROLE_REQUIRED_ARIA_PROPS)
        "button" to setOf("aria-expanded", "aria-pressed"),
        "checkbox" to setOf("aria-readonly", "aria-required"),
        "gridcell" to setOf(
            "aria-colindex",
            "aria-colspan",
            "aria-rowindex",
            "aria-rowspan",
            "aria-expanded",
            "aria-readonly",
            "aria-required",
            "aria-selected"
        ),
        "link" to setOf("aria-expanded"),
        "menuitem" to setOf("aria-expanded", "aria-posinset", "aria-setsize"),
        "menuitemcheckbox" to setOf("aria-posinset", "aria-setsize", "aria-readonly"),
        "menuitemradio" to setOf("aria-posinset", "aria-setsize", "aria-readonly"),
        "option" to setOf("aria-checked", "aria-posinset", "aria-setsize"),
        "progressbar" to setOf(
            "aria-valuemax", "aria-valuemin", "aria-valuenow", "aria-valuetext"
        ),
        "radio" to setOf("aria-posinset", "aria-setsize"),
        "scrollbar" to setOf(
            "aria-orientation", "aria-valuemax", "aria-valuemin", "aria-valuetext"
        ),
        "searchbox" to setOf(
            "aria-activedescendant",
            "aria-autocomplete",
            "aria-multiline",
            "aria-placeholder",
            "aria-readonly",
            "aria-required"
        ),
        "separator" to setOf(
            "aria-orientation", "aria-valuemax", "aria-valuemin", "aria-valuenow", "aria-valuetext"
        ),
        "slider" to setOf(
            "aria-orientation", "aria-readonly", "aria-valuemax", "aria-valuemin", "aria-valuetext"
        ),
        "spinbutton" to setOf(
            "aria-valuemax", "aria-valuemin", "aria-valuenow", "aria-valuetext", "aria-readonly", "aria-required"
        ),
        "switch" to setOf("aria-readonly"),
        "tab" to setOf("aria-selected", "aria-expanded", "aria-posinset", "aria-setsize"),
        "textbox" to setOf(
            "aria-activedescendant",
            "aria-autocomplete",
            "aria-multiline",
            "aria-placeholder",
            "aria-readonly",
            "aria-required"
        ),
        "treeitem" to setOf(
            "aria-checked", "aria-expanded", "aria-level", "aria-posinset", "aria-setsize", "aria-selected"
        ),

        // --- Composite widgets ---
        "combobox" to setOf(
            "aria-activedescendant", "aria-autocomplete", "aria-readonly", "aria-required", "aria-orientation"
        ),
        "grid" to setOf(
            "aria-level",
            "aria-multiselectable",
            "aria-readonly",
            "aria-activedescendant",
            "aria-colcount",
            "aria-rowcount"
        ),
        "listbox" to setOf(
            "aria-multiselectable",
            "aria-readonly",
            "aria-required",
            "aria-activedescendant",
            "aria-orientation",
            "aria-expanded"
        ),
        "menu" to setOf("aria-activedescendant", "aria-orientation"),
        "menubar" to setOf("aria-activedescendant", "aria-orientation"),
        "radiogroup" to setOf(
            "aria-activedescendant", "aria-orientation", "aria-readonly", "aria-required"
        ),
        "tablist" to setOf(
            "aria-activedescendant", "aria-multiselectable", "aria-orientation", "aria-level"
        ),
        "tree" to setOf(
            "aria-activedescendant", "aria-multiselectable", "aria-orientation", "aria-required"
        ),
        "treegrid" to setOf(
            "aria-activedescendant",
            "aria-colcount",
            "aria-rowcount",
            "aria-level",
            "aria-multiselectable",
            "aria-orientation",
            "aria-readonly"
        ),

        // --- Document structure ---
        "article" to setOf("aria-posinset", "aria-setsize"),
        "cell" to setOf(
            "aria-colindex", "aria-colspan", "aria-rowindex", "aria-rowspan"
        ),
        "columnheader" to setOf(
            "aria-sort",
            "aria-colindex",
            "aria-colspan",
            "aria-rowindex",
            "aria-rowspan",
            "aria-expanded",
            "aria-readonly",
            "aria-required",
            "aria-selected"
        ),
        "group" to setOf("aria-activedescendant"),
        "listitem" to setOf("aria-level", "aria-posinset", "aria-setsize"),
        "meter" to setOf("aria-valuemax", "aria-valuemin", "aria-valuetext"),
        "row" to setOf(
            "aria-colindex",
            "aria-level",
            "aria-rowindex",
            "aria-selected",
            "aria-activedescendant",
            "aria-expanded",
            "aria-setsize",
            "aria-posinset"
        ),
        "rowheader" to setOf(
            "aria-sort",
            "aria-colindex",
            "aria-colspan",
            "aria-rowindex",
            "aria-rowspan",
            "aria-expanded",
            "aria-readonly",
            "aria-required",
            "aria-selected"
        ),
        "table" to setOf("aria-colcount", "aria-rowcount"),
        "toolbar" to setOf("aria-activedescendant", "aria-orientation"),

        // --- Windows / dialogs ---
        "alertdialog" to setOf("aria-modal", "aria-expanded"),
        "dialog" to setOf("aria-modal"),
        "application" to setOf("aria-activedescendant")
    )

    /**
     * Returns the complete set of ARIA attributes allowed on an element with the
     * given [role], reusing the existing lists instead of duplicating data:
     *   GLOBAL_ARIA_ATTRIBUTES + ROLE_REQUIRED_ARIA_PROPS[role] + ROLE_SUPPORTED_ARIA_PROPS[role]
     *
     * Returns only the global attributes for roles that have no extra props, and
     * for structural roles (e.g. `banner`, `main`) that support globals only.
     */
    fun supportedAriaPropsForRole(role: String): Set<String> =
        AriaConstants.GLOBAL_ARIA_ATTRIBUTES + ROLE_REQUIRED_ARIA_PROPS.getOrDefault(
            role, emptySet()
        ) + ROLE_SUPPORTED_ARIA_PROPS.getOrDefault(role, emptySet())

}
