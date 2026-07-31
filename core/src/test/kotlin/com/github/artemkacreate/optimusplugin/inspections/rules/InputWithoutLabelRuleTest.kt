package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for InputWithoutLabelRule: <input> must have an associated label.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class InputWithoutLabelRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_inputWithoutLabel_flagged() {
        myFixture.configureByText("test.html", """<input type="text">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag <input> without label", highlights.any { it.description?.contains("label") == true || it.description?.contains("aria-label") == true })
    }

    fun testHtml_inputWithAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<input type="text" aria-label="Name">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <input> with aria-label", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }

    fun testHtml_inputWithAriaLabelledby_noFlag() {
        myFixture.configureByText("test.html", """<input type="text" aria-labelledby="label-id">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <input> with aria-labelledby", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }

    fun testHtml_inputWithId_andMatchingLabel_noFlag() {
        myFixture.configureByText("test.html", """
            <label for="name">Name</label>
            <input type="text" id="name">
        """.trimIndent())
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <input> with matching label[for]", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }

    fun testHtml_inputTypeHidden_noFlag() {
        myFixture.configureByText("test.html", """<input type="hidden" name="token">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <input type='hidden'>", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }

    fun testHtml_inputTypeSubmit_noFlag() {
        myFixture.configureByText("test.html", """<input type="submit" value="Send">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <input type='submit'>", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }

    fun testHtml_inputTypeButton_noFlag() {
        myFixture.configureByText("test.html", """<input type="button" value="Click">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag <input type='button'>", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }

    fun testHtml_nonInputTag_noFlag() {
        myFixture.configureByText("test.html", """<div type="text"></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-input tags", highlights.any { it.description?.contains("must have") == true && it.description?.contains("label") == true })
    }
}
