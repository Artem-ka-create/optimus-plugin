package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for NoRedudantRolesRule: an explicit role equal to the element's implicit
 * native role is redundant.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class NoRedudantRolesRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("is redundant for the") == true }

    // ==================== Should flag ====================

    fun testHtml_buttonWithButtonRole_flag() {
        myFixture.configureByText("test.html", """<button role="button">Save</button>""")
        assertTrue("Should flag <button role='button'>", isFlagged())
    }

    fun testHtml_navWithNavigationRole_flag() {
        myFixture.configureByText("test.html", """<nav role="navigation"></nav>""")
        assertTrue("Should flag <nav role='navigation'>", isFlagged())
    }

    fun testHtml_ulWithListRole_flag() {
        myFixture.configureByText("test.html", """<ul role="list"></ul>""")
        assertTrue("Should flag <ul role='list'>", isFlagged())
    }

    fun testHtml_anchorWithHrefAndLinkRole_flag() {
        myFixture.configureByText("test.html", """<a href="/x" role="link">Home</a>""")
        assertTrue("Should flag <a href role='link'>", isFlagged())
    }

    fun testHtml_inputTextWithTextboxRole_flag() {
        myFixture.configureByText("test.html", """<input type="text" role="textbox"/>""")
        assertTrue("Should flag <input type='text' role='textbox'>", isFlagged())
    }

    fun testHtml_topLevelHeaderWithBannerRole_flag() {
        myFixture.configureByText("test.html", """<header role="banner"></header>""")
        assertTrue("Should flag top-level <header role='banner'>", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_anchorWithoutHrefWithLinkRole_noFlag() {
        // <a> without href has no implicit link role.
        myFixture.configureByText("test.html", """<a role="link">x</a>""")
        assertFalse("Should NOT flag <a> without href", isFlagged())
    }

    fun testHtml_passwordInputWithTextboxRole_noFlag() {
        // password input has no implicit textbox role.
        myFixture.configureByText("test.html", """<input type="password" role="textbox"/>""")
        assertFalse("Should NOT flag <input type='password'>", isFlagged())
    }

    fun testHtml_fileInputWithTextboxRole_noFlag() {
        myFixture.configureByText("test.html", """<input type="file" role="textbox"/>""")
        assertFalse("Should NOT flag <input type='file'>", isFlagged())
    }

    fun testHtml_nestedHeaderWithBannerRole_noFlag() {
        // header inside <article> is generic, so role="banner" is not redundant.
        myFixture.configureByText("test.html", """<article><header role="banner"></header></article>""")
        assertFalse("Should NOT flag nested <header role='banner'>", isFlagged())
    }

    fun testHtml_nonRedundantRole_noFlag() {
        myFixture.configureByText("test.html", """<div role="button">x</div>""")
        assertFalse("Should NOT flag a non-implicit role", isFlagged())
    }

    fun testHtml_noRole_noFlag() {
        myFixture.configureByText("test.html", """<nav></nav>""")
        assertFalse("Should NOT flag element without role", isFlagged())
    }

    // ==================== QuickFix ====================

    fun testHtml_quickFixRemovesRedundantRole() {
        myFixture.configureByText("test.html", """<button role="button">Save</button>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Delete redundant role attribute") }
        assertNotNull("Delete-role quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertFalse("role attribute should be removed", myFixture.file.text.contains("role"))
    }
}

