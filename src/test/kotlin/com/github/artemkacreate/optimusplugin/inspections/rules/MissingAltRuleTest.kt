package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for MissingAltRule: <img> must have an alt attribute.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class MissingAltRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_imgWithoutAlt_flagged() {
        myFixture.configureByText("test.html", """<img src="cat.jpg">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <img> without alt", highlights.any { it.description?.contains("alt") == true })
    }

    fun testHtml_imgWithAlt_noFlag() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" alt="A cat">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <img> with alt", highlights.any { it.description?.contains("must have an alt") == true })
    }

    fun testHtml_imgWithEmptyAlt_noFlag() {
        myFixture.configureByText("test.html", """<img src="spacer.gif" alt="">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <img> with empty alt (decorative)", highlights.any { it.description?.contains("must have an alt") == true })
    }

    fun testHtml_imgWithAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" aria-label="A cute cat">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <img> with aria-label", highlights.any { it.description?.contains("must have an alt") == true })
    }

    fun testHtml_imgWithoutAltSelfClosing_flagged() {
        myFixture.configureByText("test.html", """<img src="a.jpg"/>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag self-closing <img/> without alt", highlights.any { it.description?.contains("alt") == true })
    }

    fun testHtml_nonImgTag_noFlag() {
        myFixture.configureByText("test.html", """<div src="cat.jpg"></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-img tags", highlights.any { it.description?.contains("must have an alt") == true })
    }

    // ==================== Angular ====================

    fun testAngular_imgWithBoundAlt_noFlag() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" [alt]="altText">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <img> with [alt] (Angular)", highlights.any { it.description?.contains("must have an alt") == true })
    }
}
