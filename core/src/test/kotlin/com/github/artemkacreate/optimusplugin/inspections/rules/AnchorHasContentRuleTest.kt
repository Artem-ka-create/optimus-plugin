package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for AnchorHasContentRule: <a> must have accessible text content.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class AnchorHasContentRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_anchorWithText_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page">Click here</a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag <a> with text",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }

    fun testHtml_anchorWithChildElement_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page"><span>Link</span></a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag <a> with child element",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }

    fun testHtml_anchorWithAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page" aria-label="Go to page"></a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag <a> with aria-label",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }

    fun testHtml_anchorWithAriaLabelledby_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page" aria-labelledby="link-text"></a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag <a> with aria-labelledby",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }

    fun testHtml_anchorWithImage_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page"><img src="icon.png" alt="Go"/></a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag <a> with child img",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }

    fun testHtml_nonAnchorTag_noFlag() {
        myFixture.configureByText("test.html", """<div></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag non-anchor tags",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }

    fun testHtml_anchorWithDynamicAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<a href="/page" :aria-label="linkLabel"></a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse(
            "Should NOT flag <a> with :aria-label",
            highlights.any { it.description?.contains("must have") == true && it.description?.contains("text") == true })
    }
}
