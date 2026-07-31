package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for AnchorIsValidRule: <a> must have a valid href (not '#', empty, or 'javascript:').
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class AnchorIsValidRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_anchorHrefHash_flagged() {
        myFixture.configureByText("test.html", """<a href="#">Link</a>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <a href='#'>", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_anchorHrefEmpty_flagged() {
        myFixture.configureByText("test.html", """<a href="">Link</a>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <a href=''>", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_anchorHrefJavascriptVoid_flagged() {
        myFixture.configureByText("test.html", """<a href="javascript:void(0)">Link</a>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <a href='javascript:void(0)'>", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_anchorHrefJavascriptColon_flagged() {
        myFixture.configureByText("test.html", """<a href="javascript:">Link</a>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <a href='javascript:'>", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_anchorValidHref_noFlag() {
        myFixture.configureByText("test.html", """<a href="/about">About</a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <a> with valid href", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_anchorFullUrl_noFlag() {
        myFixture.configureByText("test.html", """<a href="https://example.com">Example</a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <a> with full URL href", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_anchorNoHref_noFlag() {
        myFixture.configureByText("test.html", """<a>Link without href</a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <a> without href attribute", highlights.any { it.description?.contains("valid href") == true })
    }

    fun testHtml_multipleAnchors_mixedFlags() {
        myFixture.configureByText("test.html", """
            <nav>
                <a href="#">Bad</a>
                <a href="/good">Good</a>
                <a href="javascript:void(0)">Bad2</a>
            </nav>
        """.trimIndent())
        val highlights = myFixture.doHighlighting().filter { it.description?.contains("valid href") == true }
        assertEquals("Should flag exactly 2 invalid anchors", 2, highlights.size)
    }

    fun testHtml_nonAnchorTag_noFlag() {
        myFixture.configureByText("test.html", """<div href="#">Not an anchor</div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-anchor tags", highlights.any { it.description?.contains("valid href") == true })
    }

    // ==================== Angular ====================

    fun testAngular_anchorWithBoundHref_noFlag() {
        myFixture.configureByText("test.html", """<a [href]="dynamicUrl">Link</a>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <a> with [href] (Angular dynamic)", highlights.any { it.description?.contains("valid href") == true })
    }
}
