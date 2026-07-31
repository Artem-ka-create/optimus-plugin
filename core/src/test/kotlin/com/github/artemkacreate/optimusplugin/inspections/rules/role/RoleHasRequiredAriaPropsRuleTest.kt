package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for RoleHasRequiredAriaPropsRule: an element declaring an ARIA role must
 * also declare every ARIA state/property that role requires.
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class RoleHasRequiredAriaPropsRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains("requires") == true }

    // ==================== Should flag ====================

    fun testHtml_checkboxWithoutAriaChecked_flag() {
        myFixture.configureByText("test.html", """<div role="checkbox">Accept</div>""")
        assertTrue("Should flag role='checkbox' without aria-checked", isFlagged())
    }

    fun testHtml_sliderWithoutAriaValueNow_flag() {
        myFixture.configureByText("test.html", """<div role="slider"></div>""")
        assertTrue("Should flag role='slider' without aria-valuenow", isFlagged())
    }

    fun testHtml_comboboxMissingOneRequired_flag() {
        myFixture.configureByText("test.html", """<div role="combobox" aria-expanded="false"></div>""")
        assertTrue("Should flag role='combobox' missing aria-controls", isFlagged())
    }

    fun testHtml_uppercaseRole_flag() {
        myFixture.configureByText("test.html", """<div role="CHECKBOX">Accept</div>""")
        assertTrue("Should flag role value case-insensitively", isFlagged())
    }

    // ==================== Should NOT flag ====================

    fun testHtml_checkboxWithAriaChecked_noFlag() {
        myFixture.configureByText("test.html", """<div role="checkbox" aria-checked="false">Accept</div>""")
        assertFalse("Should NOT flag when aria-checked present", isFlagged())
    }

    fun testHtml_comboboxWithAllRequired_noFlag() {
        myFixture.configureByText(
            "test.html",
            """<div role="combobox" aria-controls="list" aria-expanded="false"></div>"""
        )
        assertFalse("Should NOT flag when all required props present", isFlagged())
    }

    fun testHtml_roleWithoutRequiredProps_noFlag() {
        myFixture.configureByText("test.html", """<div role="button">Click</div>""")
        assertFalse("Should NOT flag a role that has no required props", isFlagged())
    }

    fun testHtml_noRole_noFlag() {
        myFixture.configureByText("test.html", """<div>Text</div>""")
        assertFalse("Should NOT flag element without role", isFlagged())
    }

    // ==================== QuickFixes ====================

    fun testHtml_quickFixAddsMissingAriaChecked() {
        myFixture.configureByText("test.html", """<div role="checkbox">Accept</div>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text.contains("aria-checked") }
        assertNotNull("Add-missing-attribute quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        assertTrue("aria-checked should be added", myFixture.file.text.contains("aria-checked"))
    }
}

