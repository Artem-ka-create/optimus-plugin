package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for TabIndexNoPositiveRule: tabindex should not have a positive value.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class TabIndexNoPositiveRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_tabindexPositive_flagged() {
        myFixture.configureByText("test.html", """<div tabindex="5">Content</div>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag tabindex > 0", highlights.any { it.description?.contains("tabindex") == true || it.description?.contains("tabIndex") == true })
    }

    fun testHtml_tabindexOne_flagged() {
        myFixture.configureByText("test.html", """<input type="text" tabindex="1" aria-label="Field">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag tabindex=1", highlights.any { it.description?.contains("tabindex") == true || it.description?.contains("tabIndex") == true })
    }

    fun testHtml_tabindexZero_noFlag() {
        myFixture.configureByText("test.html", """<div tabindex="0">Focusable</div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag tabindex=0", highlights.any { it.description?.contains("positive") == true })
    }

    fun testHtml_tabindexNegative_noFlag() {
        myFixture.configureByText("test.html", """<div tabindex="-1">Not in tab order</div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag tabindex=-1", highlights.any { it.description?.contains("positive") == true })
    }

    fun testHtml_noTabindex_noFlag() {
        myFixture.configureByText("test.html", """<div>Normal</div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag element without tabindex", highlights.any { it.description?.contains("tabindex") == true || it.description?.contains("tabIndex") == true })
    }

    fun testHtml_tabindexLargePositive_flagged() {
        myFixture.configureByText("test.html", """<button tabindex="99">Click</button>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag tabindex=99", highlights.any { it.description?.contains("tabindex") == true || it.description?.contains("tabIndex") == true })
    }
}
