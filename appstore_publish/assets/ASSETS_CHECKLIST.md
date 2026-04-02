# Store Assets Checklist — ShareMark

All assets required for a complete Google Play Store listing.

---

## App Icon

| Asset         | Size          | Format | Notes                                    |
|---------------|---------------|--------|------------------------------------------|
| Hi-res icon   | 512 x 512 px  | PNG    | No alpha/transparency on outer 10%       |

- Source: `icon.jpeg` in the project root (already provided).
- Action: Resize to 512x512 PNG. Use `ResizeIcon.java` (already in project root).

**Command to generate:**
```bash
# Compile and run the provided ResizeIcon utility
javac ResizeIcon.java
java ResizeIcon icon.jpeg 512 512 appstore_publish/assets/icon_512x512.png
```

---

## Feature Graphic (required)

| Asset           | Size           | Format | Notes                          |
|-----------------|----------------|--------|--------------------------------|
| Feature graphic | 1024 x 500 px  | PNG/JPG | Shown at top of store listing |

- Design suggestion: Dark/neutral background, ShareMark logo centered, tagline "Save links. Find them fast."
- Tools: Canva, Figma, Adobe Express (free options available)
- Place output at: `appstore_publish/assets/feature_graphic_1024x500.png`

---

## Screenshots (Phone)

Minimum 2 required, up to 8 recommended.

| Slot | Recommended Content                  | Min Size       | Max Size  |
|------|--------------------------------------|----------------|-----------|
| 1    | Home screen with bookmark list       | 320 x 320 px   | 3840 px   |
| 2    | Category view (filtered)             |                |           |
| 3    | Search results screen                |                |           |
| 4    | Share sheet — adding a bookmark      |                |           |
| 5    | Favorites tab                        |                |           |
| 6    | Empty state / onboarding screen      |                |           |

**Screenshot Requirements:**
- Format: PNG or JPEG
- Aspect ratio: 16:9 or 9:16 (portrait recommended)
- Recommended: 1080 x 1920 px (portrait, full HD)
- No device frame required (Play Console can add frames)
- No transparent backgrounds

**How to capture:**
1. Install the release APK on a physical device or emulator.
2. Navigate to each screen listed above.
3. Use `adb shell screencap -p /sdcard/screen.png && adb pull /sdcard/screen.png` or use Android Studio's screenshot tool.
4. Save to: `appstore_publish/assets/screenshots/screen_01.png` etc.

---

## Screenshots (Tablet — optional but recommended)

| Device Type | Recommended Size  | Notes                            |
|-------------|-------------------|----------------------------------|
| 7-inch      | 1200 x 1920 px    | Use Android emulator AVD         |
| 10-inch     | 1920 x 1200 px    | Landscape layout looks great     |

---

## Graphic Assets Summary Table

| Asset                  | Status     | Location                                          |
|------------------------|------------|---------------------------------------------------|
| App Icon 512x512 PNG   | Pending    | `assets/icon_512x512.png`                         |
| Feature Graphic        | Pending    | `assets/feature_graphic_1024x500.png`             |
| Phone Screenshot 1     | Pending    | `assets/screenshots/screen_01.png`                |
| Phone Screenshot 2     | Pending    | `assets/screenshots/screen_02.png`                |
| Phone Screenshot 3     | Pending    | `assets/screenshots/screen_03.png`                |
| Phone Screenshot 4     | Pending    | `assets/screenshots/screen_04.png`                |
| Phone Screenshot 5     | Pending    | `assets/screenshots/screen_05.png`                |

---

## Screenshot Tips for Higher Ratings

- Use real content in screenshots (not empty states).
- Add 3-4 realistic sample bookmarks: a news article, a YouTube video, a GitHub repo, a recipe.
- Optionally overlay short text captions (e.g., "One tap to save any link") using Canva.
- Use consistent status bar (airplane mode, 100% battery, clean time like 9:41).
