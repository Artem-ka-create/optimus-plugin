package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ScopeRuleTest : BasePlatformTestCase() {

    private companion object {
        const val MESSAGE = "The 'scope' attribute is only valid on <th> elements"
    }

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    private fun isFlagged(): Boolean =
        myFixture.doHighlighting().any { it.description?.contains(MESSAGE) == true }

    private fun flaggedCount(): Int =
        myFixture.doHighlighting().count { it.description?.contains(MESSAGE) == true }

    // ==================== Flagged: scope on non-<th> ====================

    fun testHtml_scopeOnSpan_flagged() {
        myFixture.configureByText("test.html", """<span scope>X</span>""")
        assertTrue("Should flag scope on <span>", isFlagged())
    }

    fun testHtml_scopeOnDiv_flagged() {
        myFixture.configureByText("test.html", """<div scope="col">X</div>""")
        assertTrue("Should flag scope on <div>", isFlagged())
    }

    fun testHtml_scopeOnTd_flagged() {
        myFixture.configureByText(
            "test.html",
            """<table><tr><td scope="row">X</td></tr></table>"""
        )
        assertTrue("Should flag scope on <td>", isFlagged())
    }

    // ==================== Not flagged ====================

    fun testHtml_scopeOnTh_notFlagged() {
        myFixture.configureByText(
            "test.html",
            """<table><tr><th scope="col">Name</th></tr></table>"""
        )
        assertFalse("Should NOT flag scope on <th>", isFlagged())
    }

    fun testHtml_thWithoutScope_notFlagged() {
        myFixture.configureByText(
            "test.html",
            """<table><tr><th>Name</th></tr></table>"""
        )
        assertFalse("Should NOT flag <th> without scope", isFlagged())
    }

    fun testHtml_elementWithoutScope_notFlagged() {
        myFixture.configureByText("test.html", """<div class="cell">X</div>""")
        assertFalse("Should NOT flag element without scope", isFlagged())
    }

    // ==================== Multiple elements ====================

    fun testHtml_mixed_onlyNonThFlagged() {
        myFixture.configureByText(
            "test.html",
            """
            <table>
                <tr>
                    <th scope="col">Valid</th>
                    <td scope="row">Invalid</td>
                    <th>NoScope</th>
                </tr>
            </table>
            """.trimIndent()
        )
        assertEquals("Should flag exactly the <td> with scope", 1, flaggedCount())
    }

    // ==================== Quick fix ====================

    fun testHtml_quickFix_removesScopeAttribute() {
        myFixture.configureByText("test.html", """<div scope="col">X</div>""")
        val fix = myFixture.getAllQuickFixes().firstOrNull { it.text == "Remove scope attribute" }
        assertNotNull("Remove scope attribute quick fix should be available", fix)
        myFixture.launchAction(fix!!)
        myFixture.checkResult("""<div>X</div>""")
    }
}
