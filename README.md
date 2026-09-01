# Optimus Accessibility

![Build](https://github.com/Artem-ka-create/optimus-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

**Optimus Accessibility** is a real-time web accessibility (a11y) inspection tool for JetBrains IDEs. It works with **React**, **Angular**, and **Vue**, as well as **vanilla HTML/JS/TS** — performing a single-pass PSI tree traversal to check your HTML, JS, JSX, TS, TSX, and Vue files — including injected template strings — against WCAG/ARIA accessibility rules as you type, with one-click quick fixes.

---

> 📌 **v1.0 Release Notice:**  
> This is the **1.0 release** of Optimus Accessibility. The real-time accessibility linter documented here is just one aspect of the plugin — the first part of a larger roadmap (see below).

---

## ✨ Features

- ⚡ **Real-Time Inspection:** One optimized traversal delegates each element to every active rule — no per-rule inspection overhead.
- 🎯 **27 Accessibility Rules:** Broad set of accessibility checks across General, Anchor, ARIA, and Role categories, covering WCAG & ARIA standards.
- 🛠️ **Automated Quick Fixes:** One-click IDE quick fixes to add/remove attributes, remove elements, or rename tags.
- 🌐 **Multi-Framework & Format Support:** Full inspection support for **React** (JSX/TSX), **Angular**, **Vue**, and **Vanilla HTML / JS / TS** (including injected template strings), distinguishing native HTML from framework components so rules aren't misapplied to custom elements.
- ⚙️ **Flexible Configuration:** Enable/disable rules individually in **Settings > Editor > Optimus Accessibility**, export your configuration to a shareable `optimus-accessibility.json`, or point the plugin at a project-level JSON config file to drive rule state directly (JSON mode takes priority over manual settings).

---

## 🔍 Rule Categories

- **General & Media:** Missing `alt` attributes, redundant image `alt` text, heading content presence, `iframe` titles, autofocus checks, `accesskey` disallowance, positive `tabindex` prevention.
- **Form Controls & Labels:** `<input>` without associated `<label>`, `label[for]` validation, associated interactive control checks.
- **Anchors & Links:** Link validity, ambiguous anchor text (e.g. "click here"), missing content in links.
- **ARIA Attributes:** Valid ARIA property names/values, active descendant checks, focusable element `aria-hidden` safety, valid `lang` codes.
- **Roles & Structure:** Semantic tag preferences over roles, element-to-role constraints, required/supported ARIA attributes per role, redundant roles removal.

---

## ⚙️ Project Configuration (`optimus-accessibility.json`)

You can control rules per project by placing an `optimus-accessibility.json` file in your project root:

```json
{
  "enabled": true,
  "rules": {
    "missingAlt": true,
    "inputWithoutLabel": true,
    "anchorHasContent": true,
    "ariaProps": true,
    "roleHasRequiredAriaProps": true
  }
}
```

---

## 🔮 Roadmap & Future Features

We are actively working on expanding Optimus Accessibility for future releases:

- 📊 **Deep Project-Level Analysis:** Whole-project accessibility auditing, compliance metrics, and report generation.
- 🤖 **AI-Powered Suggestions & Repairs:** Smart AI assistance for context-aware `alt` text generation, accessible ARIA labeling, and automatic refactoring.
- ⚛️ **Framework-Specific Rule Sets:** Today's rules are framework-aware but generic (the same 27 checks run everywhere); future rules will target patterns specific to Angular, React, Vue, and UI design systems.
- ✅ **Expanded WCAG Coverage:** Additional rules to cover more WCAG success criteria beyond the current 27.

---

## 📦 Installation

### From JetBrains Marketplace:
1. Open your JetBrains IDE (IntelliJ IDEA, WebStorm, etc.).
2. Go to **Settings/Preferences** > **Plugins** > **Marketplace**.
3. Search for **"Optimus Accessibility"**.
4. Click **Install** and restart the IDE if prompted.

### From Disk:
1. Download the latest release `.zip` from GitHub Releases.
2. Navigate to **Settings/Preferences** > **Plugins** > ⚙️ **Gear Icon** > **Install plugin from disk...**
3. Select the downloaded file.

---

## 📄 License

Copyright © 2026 Artem Petrenko

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.
