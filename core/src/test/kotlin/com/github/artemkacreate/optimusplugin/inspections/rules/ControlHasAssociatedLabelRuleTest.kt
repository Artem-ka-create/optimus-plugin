package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for ControlHasAssociatedLabelRule: interactive controls must have an
 * accessible name (label / aria-label / aria-labelledby / inner text).
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class ControlHasAssociatedLabelRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun hasFieldWarning(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("should have an associated") == true }

    private fun hasInnerTextWarning(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("should have inner text content") == true }

    private fun hasEmptyAriaWarning(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("present but empty") == true }

    // ==================== Form fields ====================

    fun testHtml_selectWithoutLabel_flag() {
        myFixture.configureByText("test.html", """<select></select>""")
        assertTrue("Should flag <select> without a label", hasFieldWarning())
    }

    fun testHtml_selectWithRoleButOnlyRole_flag() {
        myFixture.configureByText("test.html", """<select role="button"></select>""")
        assertTrue("role alone does not provide a label", hasFieldWarning())
    }

    fun testHtml_inputWithAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<input aria-label="Name"/>""")
        assertFalse("Should NOT flag input with aria-label", hasFieldWarning())
    }

    fun testHtml_inputNestedInLabel_noFlag() {
        myFixture.configureByText("test.html", """<label>Name <input/></label>""")
        assertFalse("Should NOT flag input nested in label", hasFieldWarning())
    }

    fun testHtml_inputWithLabelFor_noFlag() {
        myFixture.configureByText("test.html", """<label for="n">Name</label><input id="n"/>""")
        assertFalse("Should NOT flag input with matching label[for]", hasFieldWarning())
    }

    // ==================== Empty aria-label ====================

    fun testHtml_emptyAriaLabel_flag() {
        myFixture.configureByText("test.html", """<input aria-label=""/>""")
        assertTrue("Should flag empty aria-label", hasEmptyAriaWarning())
    }

    // ==================== button / a inner text (fixed branch) ====================

    fun testHtml_buttonWithoutText_flag() {
        myFixture.configureByText("test.html", """<button></button>""")
        assertTrue("Should flag <button> without inner text", hasInnerTextWarning())
    }

    fun testHtml_buttonWithText_noFlag() {
        myFixture.configureByText("test.html", """<button>Save</button>""")
        assertFalse("Should NOT flag <button> with inner text", hasInnerTextWarning())
    }

    fun testHtml_anchorWithoutText_flag() {
        myFixture.configureByText("test.html", """<a href="/x"></a>""")
        assertTrue("Should flag <a> without inner text", hasInnerTextWarning())
    }

    fun testHtml_anchorWithText_noFlag() {
        myFixture.configureByText("test.html", """<a href="/x">Home</a>""")
        assertFalse("Should NOT flag <a> with inner text", hasInnerTextWarning())
    }

    fun testHtml_buttonWithAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<button aria-label="Close"></button>""")
        assertFalse("Should NOT flag <button> with aria-label", hasInnerTextWarning())
    }
}

