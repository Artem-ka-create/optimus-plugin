package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for NoNonIntElementToIntRoleRule: a non-interactive element must not have
 * an interactive ARIA role.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class NoNonIntElementToIntRoleRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("non-interactive element has an interactive role") == true }

    // ==================== Should flag ====================

    fun testHtml_liWithButtonRole_flag() {
        myFixture.configureByText("test.html", """<li role="button">Item</li>""")
        assertTrue("Should flag <li role='button'>", isFlagged())
    }

    fun testHtml_headingWithLinkRole_flag() {
        myFixture.configureByText("test.html", """<h1 role="link">Title</h1>""")
        assertTrue("Should flag <h1 role='link'>", isFlagged())
    }

    fun testHtml_imgWithCheckboxRole_flag() {
        myFixture.configureByText("test.html", """<img src="x.png" role="checkbox"/>""")
        assertTrue("Should flag <img role='checkbox'>", isFlagged())
    }

    fun testHtml_uppercaseRole_flag() {
        myFixture.configureByText("test.html", """<li role="BUTTON">Item</li>""")
        assertTrue("Should flag role case-insensitively", isFlagged())
    }

    fun testHtml_roleTokenList_flag() {
        myFixture.configureByText("test.html", """<li role="button none">Item</li>""")
        assertTrue("Should flag first token of a role list", isFlagged())
    }

    fun testHtml_anchorWithoutHrefWithButtonRole_flag() {
        // <a> without href is non-interactive, so an interactive role is a violation.
        myFixture.configureByText("test.html", """<a role="button">Click</a>""")
        assertTrue("Should flag <a> without href + interactive role", isFlagged())
    }

    fun testHtml_audioWithoutControlsWithButtonRole_flag() {
        myFixture.configureByText("test.html", """<audio role="button"></audio>""")
        assertTrue("Should flag <audio> without controls + interactive role", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_liWithNonInteractiveRole_noFlag() {
        myFixture.configureByText("test.html", """<li role="listitem">Item</li>""")
        assertFalse("Should NOT flag non-interactive role", isFlagged())
    }

    fun testHtml_liWithoutRole_noFlag() {
        myFixture.configureByText("test.html", """<li>Item</li>""")
        assertFalse("Should NOT flag element without role", isFlagged())
    }

    fun testHtml_anchorWithHrefWithButtonRole_noFlag() {
        // <a href> is interactive → not this rule's concern.
        myFixture.configureByText("test.html", """<a href="/x" role="button">Click</a>""")
        assertFalse("Should NOT flag <a href> (interactive)", isFlagged())
    }

    fun testHtml_audioWithControlsWithButtonRole_noFlag() {
        // <audio controls> is interactive → not this rule's concern.
        myFixture.configureByText("test.html", """<audio controls role="button"></audio>""")
        assertFalse("Should NOT flag <audio controls> (interactive)", isFlagged())
    }

    fun testHtml_divWithButtonRole_noFlag() {
        // <div> is generic (not in the strict non-interactive set) → allowed.
        myFixture.configureByText("test.html", """<div role="button">Click</div>""")
        assertFalse("Should NOT flag <div> (generic element)", isFlagged())
    }

    fun testHtml_interactiveElement_noFlag() {
        // <button> is interactive, handled by a different rule.
        myFixture.configureByText("test.html", """<button role="button">Click</button>""")
        assertFalse("Should NOT flag interactive element", isFlagged())
    }

    // ==================== QuickFixes ====================

    fun testHtml_quickFixRemovesRole() {
        myFixture.configureByText("test.html", """<li role="button">Item</li>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Remove interactive role") }
        assertNotNull("Remove-role quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertFalse("role attribute should be removed", myFixture.file.text.contains("role"))
    }

    fun testHtml_quickFixChangeToDiv() {
        myFixture.configureByText("test.html", """<li role="button">Item</li>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Change element to <div>") }
        assertNotNull("Change-to-div quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertTrue("Element should become <div>", myFixture.file.text.contains("<div"))
        assertFalse("Element should no longer be <li>", myFixture.file.text.contains("<li"))
    }
}


