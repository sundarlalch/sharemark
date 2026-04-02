# ShareMark — Google Play Publishing Package

This folder contains everything needed to publish ShareMark on the Google Play Store.

---

## Folder Structure

```
appstore_publish/
├── README.md                          ← You are here (start here)
├── PUBLISHING_STEPS.md                ← Complete step-by-step publishing guide
│
├── store_listing/
│   ├── app_description.md             ← App name, short & full description, keywords
│   └── release_notes.md              ← What's new (v1.0 and template for future)
│
├── legal/
│   ├── privacy_policy.md             ← Privacy Policy (customize & host online)
│   └── terms_of_service.md           ← Terms of Service
│
├── assets/
│   └── ASSETS_CHECKLIST.md           ← Required graphics: icon, feature graphic, screenshots
│
└── technical/
    ├── PRE_LAUNCH_CHECKLIST.md       ← Technical readiness checklist
    ├── DATA_SAFETY.md                ← Answers for Google Play Data Safety section
    ├── CONTENT_RATING_ANSWERS.md     ← IARC content rating questionnaire answers
    ├── SIGNING_INFO.md               ← Keystore & signing build instructions
    └── ASO_STRATEGY.md               ← App Store Optimization strategy
```

---

## Quick Start — Publishing Order

1. **Read** `PUBLISHING_STEPS.md` for the full end-to-end guide.

2. **Customize legal docs** (fill in your name/email):
   - `legal/privacy_policy.md` — replace `[Your Name / Company Name]` and `[your-email@example.com]`
   - `legal/terms_of_service.md` — same replacements
   - Host these docs online (GitHub Pages, Notion, or any website)

3. **Prepare assets** per `assets/ASSETS_CHECKLIST.md`:
   - 512x512 app icon PNG
   - 1024x500 feature graphic
   - 2–8 phone screenshots

4. **Build the release AAB:**
   ```bash
   ./gradlew bundleRelease
   ```

5. **Complete the Play Console setup** using the documents in this folder.

---

## App Summary

| Field          | Value                    |
|----------------|--------------------------|
| App Name       | ShareMark                |
| Package ID     | com.bookmarkapp          |
| Version        | 1.0                      |
| versionCode    | 1                        |
| Min Android    | 8.0 (API 26)             |
| Target Android | 14 (API 34)              |
| Category       | Productivity             |
| Price          | Free                     |
| Content Rating | Everyone                 |
| Internet       | HTTPS only               |
| Account needed | No                       |
| Data collected | None                     |

---

## Items Requiring Your Input

Before submitting, replace the following placeholders across all documents:

| Placeholder                  | Replace with                          | Files affected            |
|------------------------------|---------------------------------------|---------------------------|
| `[Your Name / Company Name]` | Your real name or company name        | privacy_policy, ToS       |
| `[your-email@example.com]`   | Your developer contact email          | privacy_policy, ToS       |
| `[Your Country/State]`       | Your country and jurisdiction         | privacy_policy, ToS       |
| `[Your Jurisdiction]`        | Specific court jurisdiction           | privacy_policy, ToS       |
| Support URL in app_description | Your website or GitHub repo URL     | app_description            |
