package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.GlobalAccessibilityInspection
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for ImgRedundantAltRule: <img> alt should not contain "image", "picture", or "photo".
 *
 * Note: JSX/TSX/Vue tests are excluded because BasePlatformTestCase does not load
 * the JavaScript/Vue language plugins needed to parse those file types as XML.
 */
class ImgRedundantAltRuleTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(GlobalAccessibilityInspection::class.java)
    }

    // ==================== HTML ====================

    fun testHtml_altWithRedundantImage_flagged() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" alt="image of a cat">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag alt containing 'image'", highlights.any { it.description?.contains("redundant") == true || it.description?.contains("image") == true })
    }

    fun testHtml_altWithRedundantPhoto_flagged() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" alt="photo of a cat">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag alt containing 'photo'", highlights.any { it.description?.contains("redundant") == true || it.description?.contains("photo") == true })
    }

    fun testHtml_altWithRedundantPicture_flagged() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" alt="picture of a cat">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag alt containing 'picture'", highlights.any { it.description?.contains("redundant") == true || it.description?.contains("picture") == true })
    }

    fun testHtml_altWithProperText_noFlag() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" alt="A fluffy orange cat">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag alt without redundant words", highlights.any { it.description?.contains("redundant") == true })
    }

    fun testHtml_altEmpty_noFlag() {
        myFixture.configureByText("test.html", """<img src="spacer.gif" alt="">""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag empty alt", highlights.any { it.description?.contains("redundant") == true })
    }

    fun testHtml_nonImgTag_noFlag() {
        myFixture.configureByText("test.html", """<div alt="image of something"></div>""")
        val highlights = myFixture.doHighlighting()
        assertFalse("Should NOT flag non-img tags", highlights.any { it.description?.contains("redundant") == true })
    }

    fun testHtml_altWithRedundantCaseInsensitive_flagged() {
        myFixture.configureByText("test.html", """<img src="cat.jpg" alt="IMAGE of a cat">""")
        val highlights = myFixture.doHighlighting()
        assertTrue("Should flag case-insensitive redundant word", highlights.any { it.description?.contains("redundant") == true || it.description?.contains("IMAGE") == true || it.description?.contains("image") == true })
    }
}
