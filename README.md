# AmpyStudio — Organic SVG UI Editor 1.0 prototype

Offline, dependency-free browser editor. Clone or download this repository and open `index.html` in a modern browser, or serve the directory with `python -m http.server 8080` and visit `http://localhost:8080`. `viewer.html` is the earlier JSVG runtime for the original AmpyMatrix schema.

## Workflow
- New / Demo; insert components or one of 64 EXOGLYPH symbols. Select and drag in edit mode. Use Connect to select two objects. Undo/redo, duplicate, delete, layer order and snap grid are available.
- Inspector: geometry mode (sharp, circular fillet, quadratic organic), radius, stroke, size, position, rotation, color, fill, opacity, glow, source/target glyph, motion duration, effect, anchor and simulated tag.
- Timeline: choose an object, seek, select a track, enter a value, add a key. Click a key to seek to it, then delete or replace it. Five tracks: morph, scale, rotation, opacity and glow. Preview loops or plays until the end. Morph samples actual SVG paths and preserves exact endpoints.
- Preview starts the simulated pump; Change flow switches A → B → dual → A. Both pipes remain fixed. Fault inhibits flow, ACK leaves the fault latched, Reset requires a new start. No real PLC communication.
- Save JSVG writes SVG XML with embedded JSON in the SVG metadata. Open JSVG reconstructs editable objects from the supported project schema; export SVG produces a static image. Example: `examples/OrganicDemo.jsvg`.
- Original `examples/AmpyMatrix.jsvg` is supported by `viewer.html`; importing it in Studio is a simplified metadata migration (not lossless).

## Implementation status
The standalone `index.html` embeds the SVG engine, editor logic and responsive stylesheet; `viewer.html` contains the legacy viewer.

This is a first integration, not a complete production HMI designer. Motion presets include several shared/simplified visual approximations rather than independent complete engines: radial/iris share the window deformation base, and several particle/fragment effects share a decorative geometry base. The extrusion is layered SVG, not a full 3D mesh. Physics values are persisted, but no general physical solver is integrated. The event system is limited to click/warning/fault and simulated pump controls; the tag field is descriptive. There is no node graph, native installer, actual PLC connector, arbitrary SVG path editor, or complete migration of external SVG projects. JSVG metadata does not execute in a normal SVG viewer. The legacy viewer is not a general player for the new editor schema; use Preview within Studio to play new projects. Imported XML is parsed, not inserted into the live page; the renderer reconstructs supported objects from validated metadata.

## Smoke testing
JavaScript syntax checked with Node. Chromium (Playwright, inline copy of the HTML/CSS/JS) verified initial rendering, 64 glyphs, 7 example objects, preview, route switch, fault blocking, duplicate/undo, JSVG download and reimport. Native file:// navigation was blocked by the test environment policy, so tested with set_content. No independent verification of every motion preset, mobile device compatibility, 120Hz performance or safety behavior.

## Android / Pixel 7 Pro

The `android/` project packages the existing standalone editor in an offline native WebView, with portrait touch tabs, Android document open/save, API 36 targeting and a high-refresh display preference. See [Android instructions](android/README.md). Debug APK builds are configured in [GitHub Actions](https://github.com/Frak89/AmpyStudio/actions/workflows/android.yml); download the `AmpyStudio-Android` artifact after a successful run. Device tests and performance measurements are pending.

## AmpyNode (1.1.0)
Open AmpyNode above the canvas. Add event, condition and action nodes; select a node to edit properties; use source, port and destination to connect them. The demo warning event sets a tag and triggers an alarm animation; applying a simulated DB_Core.Flow tag above 80 selects dual flow, otherwise route A. Logic, links and initial tags are embedded in JSVG. Runtime overrides and tag values are simulation-only. Execution stops at a repeated node or 256 steps. No arbitrary imported code or live PLC connection. Android panel available via AmpyNode button. This visual graph is schematic, with automatic layout, not a free-position node editor yet.
