package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for NoDistractingElementsRule: <marquee> and <blink> must not be used.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class NoDistractingElementsRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("distracting") == true }

    // ==================== Should flag ====================

    fun testHtml_marquee_flag() {
        myFixture.configureByText("test.html", """<marquee>Scrolling text</marquee>""")
        assertTrue("Should flag <marquee>", isFlagged())
    }

    fun testHtml_blink_flag() {
        myFixture.configureByText("test.html", """<blink>Blinking text</blink>""")
        assertTrue("Should flag <blink>", isFlagged())
    }

    fun testHtml_selfClosingMarquee_flag() {
        myFixture.configureByText("test.html", """<marquee/>""")
        assertTrue("Should flag self-closing <marquee>", isFlagged())
    }

    fun testHtml_uppercaseMarquee_flag() {
        // HTML tag names are case-insensitive.
        myFixture.configureByText("test.html", """<MARQUEE>Scrolling text</MARQUEE>""")
        assertTrue("Should flag <MARQUEE> (case-insensitive)", isFlagged())
    }

    fun testHtml_nestedMarquee_flag() {
        myFixture.configureByText("test.html", """<div><p><marquee>Hi</marquee></p></div>""")
        assertTrue("Should flag nested <marquee>", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_regularDiv_noFlag() {
        myFixture.configureByText("test.html", """<div>Regular content</div>""")
        assertFalse("Should NOT flag <div>", isFlagged())
    }

    fun testHtml_span_noFlag() {
        myFixture.configureByText("test.html", """<span>Text</span>""")
        assertFalse("Should NOT flag <span>", isFlagged())
    }

    // ==================== QuickFix ====================

    fun testHtml_quickFixRemovesMarquee() {
        myFixture.configureByText("test.html", """<div><marquee>Scrolling</marquee></div>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Remove distracting element") }
        assertNotNull("Quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertFalse("Marquee should be removed after quick fix", myFixture.file.text.contains("marquee"))
    }
}

