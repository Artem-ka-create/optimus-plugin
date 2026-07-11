package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for AnchorAmbigousTextRule: <a> must not use ambiguous link text.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class AnchorAmbigousTextRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("ambiguous") == true }

    // ==================== Should flag ====================

    fun testHtml_ambiguousText_flag() {
        myFixture.configureByText("test.html", """<a href="/page">Click here</a>""")
        assertTrue("Should flag ambiguous 'Click here'", isFlagged())
    }

    fun testHtml_ambiguousTextWithPunctuation_flag() {
        myFixture.configureByText("test.html", """<a href="/page">Read more!</a>""")
        assertTrue("Should flag ambiguous 'Read more!'", isFlagged())
    }

    fun testHtml_ambiguousTextNestedSpan_flag() {
        myFixture.configureByText("test.html", """<a href="/page"><span>learn more</span></a>""")
        assertTrue("Should flag ambiguous nested 'learn more'", isFlagged())
    }

    fun testHtml_ambiguousAriaLabel_flag() {
        myFixture.configureByText("test.html", """<a href="/page" aria-label="here">Documentation</a>""")
        assertTrue("Should flag ambiguous aria-label 'here'", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_descriptiveText_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page">Read the accessibility guide</a>""")
        assertFalse("Should NOT flag descriptive text", isFlagged())
    }

    fun testHtml_descriptiveAriaLabelOverridesText_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page" aria-label="Open pricing page">here</a>""")
        assertFalse("Should NOT flag when aria-label is descriptive", isFlagged())
    }

    fun testHtml_emptyAnchor_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page"></a>""")
        assertFalse("Should NOT flag empty anchor (handled by other rules)", isFlagged())
    }

    fun testHtml_nonAnchorTag_noFlag() {
        myFixture.configureByText("test.html", """<div>Click here</div>""")
        assertFalse("Should NOT flag non-anchor tags", isFlagged())
    }
}

