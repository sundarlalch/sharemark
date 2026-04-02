# Google Play Store — Publishing Guide for ShareMark

## Overview

| Field         | Value                      |
|---------------|----------------------------|
| App Name      | ShareMark                  |
| Package ID    | com.bookmarkapp            |
| Version       | 1.0 (versionCode 1)        |
| Min Android   | 8.0 (API 26)               |
| Target Android| 14 (API 34)                |
| Signing Key   | sharemark-release.jks      |

---

## Phase 1 — Prepare Your Google Play Account

### Step 1: Create a Google Play Developer Account
1. Go to https://play.google.com/console
2. Sign in with a Google account.
3. Pay the one-time $25 USD registration fee.
4. Complete identity verification (name, address, phone).
5. Accept the Developer Distribution Agreement.

> NOTE: Account approval can take 24–48 hours for new accounts.

---

## Phase 2 — Build a Signed Release APK / AAB

### Step 2: Generate a Release AAB (Recommended) or APK
Google Play strongly prefers **Android App Bundle (.aab)** over APK.

**Build Release AAB using Gradle:**
```bash
cd <project-root>
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

**Build Release APK (alternative):**
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/sharemark.apk`

> The signing config is already set up in `app/build.gradle` with `sharemark-release.jks`.

### Step 3: Verify the Build
```bash
# Verify the APK is signed correctly
apksigner verify --verbose app/build/outputs/apk/release/sharemark.apk

# Check AAB
bundletool validate --bundle=app/build/outputs/bundle/release/app-release.aab
```

---

## Phase 3 — Create the App in Google Play Console

### Step 4: Create a New App
1. Open Google Play Console → **All apps** → **Create app**.
2. Fill in:
   - **App name:** ShareMark
   - **Default language:** English (United States)
   - **App or game:** App
   - **Free or paid:** Free
3. Accept policies and click **Create app**.

---

## Phase 4 — Complete Store Listing

### Step 5: Fill in Store Listing Details
Navigate to **Grow > Store presence > Main store listing**.

Use the content from: `store_listing/app_description.md`

Required fields:
- App name (30 chars max)
- Short description (80 chars max)
- Full description (4000 chars max)
- App icon (512x512 PNG)
- Feature graphic (1024x500 PNG)
- At least 2 phone screenshots (min 320px, max 3840px on any side)

See `assets/ASSETS_CHECKLIST.md` for full asset specifications.

### Step 6: Add Release Notes
Navigate to **Release notes** section when uploading your release.
Use content from: `store_listing/release_notes.md`

---

## Phase 5 — Configure App Details

### Step 7: Content Rating (IARC)
1. Go to **Policy > App content > Content rating**.
2. Click **Start questionnaire**.
3. Category: **Utility**
4. Answer questions (see `technical/CONTENT_RATING_ANSWERS.md` for guidance).
5. Submit to receive rating (expect: **Everyone**).

### Step 8: Target Audience
1. Go to **Policy > App content > Target audience**.
2. Select: **18 and over** (or specify if targeting younger audiences).

### Step 9: App Category & Tags
1. Go to **Grow > Store presence > Store settings**.
2. Category: **Productivity**
3. Tags: Bookmark, Browser, URL Saver, Read Later, Link Manager

### Step 10: Privacy Policy
1. Go to **Policy > App content > Privacy policy**.
2. Paste or link to your Privacy Policy URL.

> The full Privacy Policy text is in `legal/privacy_policy.md`.
> Host it on GitHub Pages, your website, or a free service like policies.me.

### Step 11: Data Safety Section
1. Go to **Policy > App content > Data safety**.
2. Fill based on `technical/DATA_SAFETY.md`.
   - Data collected: None shared with third parties
   - Data encrypted in transit: Yes
   - User can delete data: Yes

---

## Phase 6 — Upload the Release

### Step 12: Create a Production Release
1. Go to **Release > Production**.
2. Click **Create new release**.
3. Upload your `.aab` or `.apk` file.
4. Fill in **Release name** (e.g., `1.0`) and **Release notes**.
5. Click **Save** then **Review release**.

### Step 13: Review and Publish
1. Fix any warnings shown in the review screen.
2. Click **Start rollout to production** when ready.
3. Choose rollout percentage (start with 20% for gradual rollout).

> Initial review typically takes **1–3 business days** for new apps.

---

## Phase 7 — Post-Publish

### Step 14: Monitor Rollout
- Check **Android vitals** for crash rates and ANRs.
- Monitor **Ratings & reviews** for user feedback.
- Track **Statistics** for installs and retention.

### Step 15: Respond to Reviews
- Respond to user reviews within 48 hours.
- Address reported bugs quickly with patch releases.

---

## Checklist Summary

- [ ] Google Play Developer Account created and verified
- [ ] Release AAB/APK built and verified
- [ ] App created in Play Console
- [ ] Store listing filled in (description, icon, screenshots)
- [ ] Feature graphic uploaded
- [ ] Content rating questionnaire completed
- [ ] Privacy Policy URL added
- [ ] Data Safety section completed
- [ ] Target audience configured
- [ ] Category set to Productivity
- [ ] Release uploaded
- [ ] Release reviewed and published
