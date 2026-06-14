package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for NoAccessKeyRule: elements should not use the accesskey attribute.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class NoAccessKeyRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_elementWithAccessKey_flagged() {
        myFixture.configureByText("test.html", """<button accesskey="s">Save</button>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag element with accesskey", highlights.any { it.description?.contains("accesskey") == true || it.description?.contains("accessKey") == true })
    }

    fun testHtml_linkWithAccessKey_flagged() {
        myFixture.configureByText("test.html", """<a href="/home" accesskey="h">Home</a>""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <a> with accesskey", highlights.any { it.description?.contains("accesskey") == true || it.description?.contains("accessKey") == true })
    }

    fun testHtml_inputWithAccessKey_flagged() {
        myFixture.configureByText("test.html", """<input type="text" accesskey="n" aria-label="Name">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag input with accesskey", highlights.any { it.description?.contains("accesskey") == true || it.description?.contains("accessKey") == true })
    }

    fun testHtml_elementWithoutAccessKey_noFlag() {
        myFixture.configureByText("test.html", """<button>Save</button>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag element without accesskey", highlights.any { it.description?.contains("accesskey") == true || it.description?.contains("accessKey") == true })
    }

    fun testHtml_multipleElements_mixedFlags() {
        myFixture.configureByText("test.html", """
            <div>
                <button accesskey="s">Save</button>
                <button>Cancel</button>
                <a href="/" accesskey="h">Home</a>
            </div>
        """.trimIndent())
        val highlights = myFixture.doHighlighting().filter { it.description?.contains("accesskey") == true || it.description?.contains("accessKey") == true }
        assertEquals("Should flag exactly 2 elements with accesskey", 2, highlights.size)
    }
}
