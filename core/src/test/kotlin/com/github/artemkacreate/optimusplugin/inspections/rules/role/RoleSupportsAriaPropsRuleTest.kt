package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for RoleSupportsAriaPropsRule: an element may only use aria-* attributes
 * that its ARIA role supports (own + required + global).
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class RoleSupportsAriaPropsRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("does not support") == true }

    // ==================== Should flag ====================

    fun testHtml_linkWithAriaChecked_flag() {
        myFixture.configureByText("test.html", """<span role="link" aria-checked="true">X</span>""")
        assertTrue("Should flag aria-checked on role='link'", isFlagged())
    }

    fun testHtml_listitemWithAriaSelected_flag() {
        myFixture.configureByText("test.html", """<div role="listitem" aria-selected="true">Item</div>""")
        assertTrue("Should flag aria-selected on role='listitem'", isFlagged())
    }

    fun testHtml_headingWithAriaRequired_flag() {
        myFixture.configureByText("test.html", """<div role="heading" aria-level="1" aria-required="true">T</div>""")
        assertTrue("Should flag aria-required on role='heading'", isFlagged())
    }

    fun testHtml_bannerWithWidgetProp_flag() {
        // banner supports globals only — aria-checked is unsupported
        myFixture.configureByText("test.html", """<div role="banner" aria-checked="true"></div>""")
        assertTrue("Should flag widget prop on landmark role", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_checkboxWithAriaChecked_noFlag() {
        myFixture.configureByText("test.html", """<div role="checkbox" aria-checked="false">Accept</div>""")
        assertFalse("aria-checked is supported by checkbox", isFlagged())
    }

    fun testHtml_globalAriaLabel_noFlag() {
        myFixture.configureByText("test.html", """<span role="link" aria-label="Home">🏠</span>""")
        assertFalse("aria-label is global — supported by any role", isFlagged())
    }

    fun testHtml_sliderWithValueProps_noFlag() {
        myFixture.configureByText(
            "test.html",
            """<div role="slider" aria-valuenow="5" aria-valuemin="0" aria-valuemax="10"></div>"""
        )
        assertFalse("value props are supported by slider", isFlagged())
    }

    fun testHtml_noRole_noFlag() {
        myFixture.configureByText("test.html", """<div aria-checked="true">Text</div>""")
        assertFalse("Should NOT flag when element has no role", isFlagged())
    }

    fun testHtml_invalidAriaAttr_noFlag() {
        // invalid aria-* names are handled by AriaPropsRule, not this rule
        myFixture.configureByText("test.html", """<div role="link" aria-nonsense="x"></div>""")
        assertFalse("Should NOT flag invalid aria attr here", isFlagged())
    }

    // ==================== QuickFix ====================

    fun testHtml_quickFixRemovesUnsupportedAttr() {
        myFixture.configureByText("test.html", """<span role="link" aria-checked="true">X</span>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("Remove unsupported ARIA attribute") }
        assertNotNull("Remove-unsupported quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertFalse("aria-checked should be removed", myFixture.file.text.contains("aria-checked"))
    }
}

