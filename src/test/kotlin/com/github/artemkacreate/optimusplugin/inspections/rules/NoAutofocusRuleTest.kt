package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for NoAutofocusRule: elements should not use the autofocus attribute.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class NoAutofocusRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_inputWithAutofocus_flagged() {
        myFixture.configureByText("test.html", """<input type="text" autofocus>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag element with autofocus", highlights.any { it.description?.contains("autofocus") == true })
    }

    fun testHtml_inputWithAutofocusValue_flagged() {
        myFixture.configureByText("test.html", """<input type="text" autofocus="true">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag element with autofocus='true'", highlights.any { it.description?.contains("autofocus") == true })
    }

    fun testHtml_buttonWithAutofocus_flagged() {
        myFixture.configureByText("test.html", """<button autofocus>Submit</button>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag button with autofocus", highlights.any { it.description?.contains("autofocus") == true })
    }

    fun testHtml_inputWithoutAutofocus_noFlag() {
        myFixture.configureByText("test.html", """<input type="text" placeholder="Name">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag element without autofocus", highlights.any { it.description?.contains("autofocus") == true })
    }

    fun testHtml_textareaWithAutofocus_flagged() {
        myFixture.configureByText("test.html", """<textarea autofocus></textarea>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag textarea with autofocus", highlights.any { it.description?.contains("autofocus") == true })
    }

    fun testHtml_multipleElements_mixedFlags() {
        myFixture.configureByText("test.html", """
            <div>
                <input type="text" autofocus>
                <input type="password">
                <button autofocus>Go</button>
            </div>
        """.trimIndent())
        val highlights = myFixture.doHighlighting().filter { it.description?.contains("autofocus") == true }
        assertEquals("Should flag exactly 2 elements with autofocus", 2, highlights.size)
    }
}
