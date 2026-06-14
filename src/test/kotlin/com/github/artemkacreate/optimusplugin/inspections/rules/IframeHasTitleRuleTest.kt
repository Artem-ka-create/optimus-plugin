package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for IframeHasTitleRule: <iframe> must have a non-empty title attribute.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class IframeHasTitleRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_iframeWithoutTitle_flagged() {
        myFixture.configureByText("test.html", """<iframe src="page.html"></iframe>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <iframe> without title", highlights.any { it.description?.contains("must have a title") == true })
    }

    fun testHtml_iframeWithEmptyTitle_flagged() {
        myFixture.configureByText("test.html", """<iframe src="page.html" title=""></iframe>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <iframe> with empty title", highlights.any { it.description?.contains("must have a title") == true })
    }

    fun testHtml_iframeWithBlankTitle_flagged() {
        myFixture.configureByText("test.html", """<iframe src="page.html" title="   "></iframe>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <iframe> with blank title", highlights.any { it.description?.contains("must have a title") == true })
    }

    fun testHtml_iframeWithValidTitle_noFlag() {
        myFixture.configureByText("test.html", """<iframe src="page.html" title="Embedded content"></iframe>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <iframe> with valid title", highlights.any { it.description?.contains("must have a title") == true })
    }

    fun testHtml_multipleIframes_mixedFlags() {
        myFixture.configureByText("test.html", """
            <div>
                <iframe src="a.html"></iframe>
                <iframe src="b.html" title="Video"></iframe>
                <iframe src="c.html" title=""></iframe>
            </div>
        """.trimIndent())
        val highlights = myFixture.doHighlighting().filter { it.description?.contains("must have a title") == true }
        assertEquals("Should flag exactly 2 iframes without valid title", 2, highlights.size)
    }

    fun testHtml_nonIframeTag_noFlag() {
        myFixture.configureByText("test.html", """<div src="page.html"></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-iframe tags", highlights.any { it.description?.contains("must have a title") == true })
    }

    // ==================== Angular ====================

    fun testAngular_iframeWithBoundTitle_noFlag() {
        myFixture.configureByText("test.html", """<iframe src="page.html" [title]="titleExpr"></iframe>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <iframe> with [title] (Angular)", highlights.any { it.description?.contains("must have a title") == true })
    }
}
