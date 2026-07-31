package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for HeadingHasContentRule: <h1>-<h6> must have accessible text content.
 *
 * Note: JSX/TSX/Vue "flagged" tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class HeadingHasContentRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_h1WithText_noFlag() {
        myFixture.configureByText("test.html", """<h1>Welcome</h1>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h1> with text", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testHtml_h1WithChildElement_noFlag() {
        myFixture.configureByText("test.html", """<h1><span>Title</span></h1>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h1> with child element", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testHtml_h1WithAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<h1 aria-label="Page title"></h1>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h1> with aria-label", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testHtml_h1WithAriaLabelledby_noFlag() {
        myFixture.configureByText("test.html", """<h1 aria-labelledby="title-id"></h1>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h1> with aria-labelledby", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testHtml_h2WithText_noFlag() {
        myFixture.configureByText("test.html", """<h2>Section</h2>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h2> with text", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testHtml_h3WithText_noFlag() {
        myFixture.configureByText("test.html", """<h3>Subsection</h3>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h3> with text", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testHtml_nonHeadingTag_noFlag() {
        myFixture.configureByText("test.html", """<div></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-heading tags", highlights.any { it.description?.contains("must have text content") == true })
    }

    // ==================== Angular ====================

    fun testAngular_headingWithInnerText_noFlag() {
        myFixture.configureByText("test.html", """<h1 [innerText]="title"></h1>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h1> with [innerText] (Angular)", highlights.any { it.description?.contains("must have text content") == true })
    }

    fun testAngular_headingWithInnerHTML_noFlag() {
        myFixture.configureByText("test.html", """<h2 [innerHTML]="titleHtml"></h2>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <h2> with [innerHTML] (Angular)", highlights.any { it.description?.contains("must have text content") == true })
    }
}
