# Pre-Launch Technical Checklist — ShareMark

Complete every item before submitting to Google Play.

---

## Build & Signing

- [ ] Release AAB built successfully: `./gradlew bundleRelease`
- [ ] Build has no errors or warnings in Android Studio
- [ ] Signed with `sharemark-release.jks` (alias: `sharemark`)
- [ ] Signing verified: `apksigner verify --verbose <apk-path>`
- [ ] versionCode = 1, versionName = "1.0" confirmed in `app/build.gradle`
- [ ] `minifyEnabled` reviewed (currently `false` — consider enabling for production)
- [ ] ProGuard/R8 rules tested if minification is enabled

---

## Functionality Testing

- [ ] App installs cleanly on a fresh device (no prior install)
- [ ] App launches without crash on API 26 (Android 8.0) — minimum SDK
- [ ] App launches without crash on API 34 (Android 14) — target SDK
- [ ] Share intent works: share a URL from Chrome → ShareMark saves it
- [ ] Share intent works: share URL from other apps (YouTube, Twitter, Reddit)
- [ ] URL metadata fetched correctly (title, thumbnail)
- [ ] Auto-categorization assigns correct categories
- [ ] Favorites: marking and unmarking works
- [ ] Search: returns correct results, handles empty query
- [ ] Category filter: correctly shows bookmarks per category
- [ ] Delete bookmark: works, confirms with dialog
- [ ] Back navigation: all back button behaviors correct
- [ ] Rotation: UI survives screen rotation without data loss
- [ ] Dark mode: UI renders correctly in system dark mode

---

## Permissions Audit

The App requests only:
- `INTERNET` — required for metadata fetch and opening URLs
- `ACCESS_NETWORK_STATE` — used to check connectivity before network calls

- [ ] No unnecessary permissions declared
- [ ] All declared permissions have a clear user-facing justification
- [ ] `android:usesCleartextTraffic="false"` — HTTPS-only confirmed

---

## Performance

- [ ] App launch time < 2 seconds on mid-range device
- [ ] No ANR (Application Not Responding) events during testing
- [ ] RecyclerView scrolls smoothly with 100+ bookmarks
- [ ] Room database queries complete off main thread (confirmed via StrictMode)
- [ ] Memory usage stays stable after browsing many bookmarks

---

## Google Play Policy Compliance

- [ ] App does not request permissions not used
- [ ] No misleading descriptions or screenshots
- [ ] Privacy Policy URL is live and accessible
- [ ] App does not violate content policies (no harmful, deceptive, or inappropriate content)
- [ ] No copyrighted assets used without rights
- [ ] App name "ShareMark" does not infringe on trademarks (verify at USPTO / Google trademark search)

---

## Metadata

- [ ] App name is under 30 characters: ✓ ("ShareMark" = 9 chars)
- [ ] Short description is under 80 characters: verify in `store_listing/app_description.md`
- [ ] Full description is under 4000 characters: verify in `store_listing/app_description.md`
- [ ] Release notes are under 500 characters: verify in `store_listing/release_notes.md`
- [ ] Category set to: **Productivity**
- [ ] Content rating questionnaire completed
- [ ] Data Safety section filled accurately

---

## Assets

- [ ] App icon 512x512 PNG uploaded
- [ ] Feature graphic 1024x500 PNG uploaded
- [ ] Minimum 2 phone screenshots uploaded
- [ ] Screenshots show real app content

---

## Post-Submit

- [ ] Monitor Play Console for review feedback
- [ ] Set up Android Vitals alerts for crashes > 1%
- [ ] Respond to first reviews within 48 hours
