package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for NonIntElementToNonIntRoleRule: an interactive element must not have a
 * non-interactive ARIA role.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class NonIntElementToNonIntRoleRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("non-interactive role") == true }

    // ==================== Should flag ====================

    fun testHtml_buttonWithArticleRole_flag() {
        myFixture.configureByText("test.html", """<button role="article">Text</button>""")
        assertTrue("Should flag <button role='article'>", isFlagged())
    }

    fun testHtml_anchorWithHrefAndPresentationRole_flag() {
        myFixture.configureByText("test.html", """<a href="/x" role="presentation">Link</a>""")
        assertTrue("Should flag <a href role='presentation'>", isFlagged())
    }

    fun testHtml_inputWithImgRole_flag() {
        myFixture.configureByText("test.html", """<input type="text" role="img"/>""")
        assertTrue("Should flag <input role='img'>", isFlagged())
    }

    fun testHtml_uppercaseRole_flag() {
        myFixture.configureByText("test.html", """<button role="ARTICLE">Text</button>""")
        assertTrue("Should flag role value case-insensitively", isFlagged())
    }

    fun testHtml_roleTokenList_flag() {
        myFixture.configureByText("test.html", """<button role="presentation none">Text</button>""")
        assertTrue("Should flag first token of a role list", isFlagged())
    }

    fun testHtml_selectWithListRole_flag() {
        myFixture.configureByText("test.html", """<select role="list"></select>""")
        assertTrue("Should flag <select role='list'>", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_anchorWithoutHref_noFlag() {
        // <a> without href is not interactive.
        myFixture.configureByText("test.html", """<a role="article">Text</a>""")
        assertFalse("Should NOT flag <a> without href", isFlagged())
    }

    fun testHtml_hiddenInput_noFlag() {
        myFixture.configureByText("test.html", """<input type="hidden" role="article"/>""")
        assertFalse("Should NOT flag hidden input", isFlagged())
    }

    fun testHtml_interactiveRole_noFlag() {
        myFixture.configureByText("test.html", """<button role="button">Text</button>""")
        assertFalse("Should NOT flag interactive role", isFlagged())
    }

    fun testHtml_noRole_noFlag() {
        myFixture.configureByText("test.html", """<button>Text</button>""")
        assertFalse("Should NOT flag element without role", isFlagged())
    }

    fun testHtml_nonInteractiveElement_noFlag() {
        myFixture.configureByText("test.html", """<div role="article">Text</div>""")
        assertFalse("Should NOT flag non-interactive element", isFlagged())
    }

    fun testHtml_videoWithoutControls_noFlag() {
        myFixture.configureByText("test.html", """<video role="img"></video>""")
        assertFalse("Should NOT flag <video> without controls", isFlagged())
    }

    // ==================== QuickFixes ====================

    fun testHtml_quickFixChangeToDiv() {
        myFixture.configureByText("test.html", """<button role="article">Text</button>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Change element to <div>") }
        assertNotNull("Change-to-div quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertTrue("Element should become <div>", myFixture.file.text.contains("<div"))
        assertFalse("Element should no longer be <button>", myFixture.file.text.contains("<button"))
    }

    fun testHtml_quickFixChangeRoleToInteractive() {
        myFixture.configureByText("test.html", """<button role="article">Text</button>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Change role to") }
        assertNotNull("Change-role quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertTrue("Role should be changed to button", myFixture.file.text.contains("""role="button""""))
    }
}

