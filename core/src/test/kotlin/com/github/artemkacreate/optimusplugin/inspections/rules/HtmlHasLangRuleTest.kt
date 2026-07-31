package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for HtmlHasLangRule: <html> must have a lang attribute.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class HtmlHasLangRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_htmlWithoutLang_flagged() {
        myFixture.configureByText("test.html", """<html><head></head><body></body></html>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <html> without lang", highlights.any { it.description?.contains("must have a lang") == true })
    }

    fun testHtml_htmlWithEmptyLang_flagged() {
        myFixture.configureByText("test.html", """<html lang=""><head></head><body></body></html>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <html> with empty lang", highlights.any { it.description?.contains("must have a lang") == true })
    }

    fun testHtml_htmlWithBlankLang_flagged() {
        myFixture.configureByText("test.html", """<html lang="   "><head></head><body></body></html>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <html> with blank lang", highlights.any { it.description?.contains("must have a lang") == true })
    }

    fun testHtml_htmlWithValidLang_noFlag() {
        myFixture.configureByText("test.html", """<html lang="en"><head></head><body></body></html>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <html> with valid lang", highlights.any { it.description?.contains("must have a lang") == true })
    }

    fun testHtml_htmlWithLangDe_noFlag() {
        myFixture.configureByText("test.html", """<html lang="de"><head></head><body></body></html>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <html> with lang='de'", highlights.any { it.description?.contains("must have a lang") == true })
    }

    fun testHtml_nonHtmlTag_noFlag() {
        myFixture.configureByText("test.html", """<div lang=""><p>Not html tag</p></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-html tags even with empty lang", highlights.any { it.description?.contains("must have a lang") == true })
    }

    // ==================== Angular ====================

    fun testAngular_htmlWithBoundLang_noFlag() {
        myFixture.configureByText("test.html", """<html [lang]="locale"><body></body></html>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <html> with [lang] (Angular)", highlights.any { it.description?.contains("must have a lang") == true })
    }
}
