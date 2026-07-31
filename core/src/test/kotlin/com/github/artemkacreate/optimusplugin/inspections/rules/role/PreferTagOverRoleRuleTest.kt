package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for PreferTagOverRoleRule: prefer a native HTML tag over an equivalent ARIA role.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class PreferTagOverRoleRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("prefer using") == true }

    // ==================== Should flag ====================

    fun testHtml_divWithButtonRole_flag() {
        myFixture.configureByText("test.html", """<div role="button">Save</div>""")
        assertTrue("Should suggest <button> for role='button'", isFlagged())
    }

    fun testHtml_spanWithNavigationRole_flag() {
        myFixture.configureByText("test.html", """<span role="navigation"></span>""")
        assertTrue("Should suggest <nav> for role='navigation'", isFlagged())
    }

    fun testHtml_divWithHeadingRole_flag() {
        myFixture.configureByText("test.html", """<div role="heading" aria-level="1">Title</div>""")
        assertTrue("Should suggest <h1>-<h6> for role='heading'", isFlagged())
    }

    fun testHtml_divWithCheckboxRole_flag() {
        myFixture.configureByText("test.html", """<div role="checkbox"></div>""")
        assertTrue("Should suggest input[type=checkbox] for role='checkbox'", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_nativeButtonWithButtonRole_noFlag() {
        // Already a <button> → redundant-roles' concern, not this rule.
        myFixture.configureByText("test.html", """<button role="button">Save</button>""")
        assertFalse("Should NOT flag native <button>", isFlagged())
    }

    fun testHtml_presentationRole_noFlag() {
        myFixture.configureByText("test.html", """<div role="presentation"></div>""")
        assertFalse("Should NOT flag role without a native tag", isFlagged())
    }

    fun testHtml_noRole_noFlag() {
        myFixture.configureByText("test.html", """<div>content</div>""")
        assertFalse("Should NOT flag element without role", isFlagged())
    }

    // ==================== QuickFix (regression for the ChangeUtil.copyElement crash) ====================

    fun testHtml_quickFixConvertsToButton() {
        myFixture.configureByText("test.html", """<div role="button">Save</div>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Change element to <button>") }
        assertNotNull("Convert-to-button quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        val text = myFixture.file.text
        assertTrue("Element should become <button>", text.contains("<button"))
        assertFalse("role attribute should be removed", text.contains("role"))
        assertFalse("original <div> should be gone", text.contains("<div"))
    }

    fun testHtml_noQuickFixForMultiTagRole() {
        // role="heading" maps to h1..h6 (ambiguous) → message only, no convert fix.
        myFixture.configureByText("test.html", """<div role="heading" aria-level="2">Title</div>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Change element to") }
        assertNull("No unsafe convert fix for ambiguous multi-tag role", fix)
    }
}

