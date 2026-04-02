# App Store Optimization (ASO) Strategy — ShareMark

ASO improves your app's discoverability on Google Play without paid advertising.

---

## Target Keywords

### Primary Keywords (high intent)
- bookmark manager android
- url saver app
- link saver
- save links from any app
- bookmark organizer

### Secondary Keywords
- read later app
- web clipper android
- article saver
- offline bookmarks
- browser bookmark sync alternative

### Long-tail Keywords
- save links without account
- bookmark app no login
- share to save url android
- auto categorize bookmarks

---

## App Name Optimization
Current: `ShareMark`

Recommended (if you want keyword in title):
`ShareMark - Bookmark Manager`

This adds "Bookmark Manager" as indexed keywords while keeping the brand name.
**Character count:** 30 (exactly at limit)

---

## Short Description Optimization

Current proposal:
`Save, organize & revisit any link — instantly from any app.`

Alternative with more keywords:
`Smart bookmark & link saver. Auto-categorize URLs from any app.`

---

## Competitor Analysis Notes

Apps to study on Play Store:
- **Pocket** — large brand, but requires account/login (ShareMark's advantage: no account)
- **Raindrop.io** — powerful but complex (ShareMark's advantage: simplicity)
- **Instapaper** — reading-focused (different niche)
- **Shiori** — similar concept, different UX

**ShareMark's differentiators to emphasize:**
1. No account required
2. Fully offline / local-first
3. Share from ANY app (not just browser)
4. ML auto-categorization

---

## Ratings Strategy

- Respond to every 1-3 star review within 48 hours.
- Implement an in-app review prompt (Google Play In-App Review API) after the user saves their 5th bookmark.
- Never incentivize or request reviews in exchange for anything.

**In-App Review API integration (future):**
```java
ReviewManager manager = ReviewManagerFactory.create(context);
Task<ReviewInfo> request = manager.requestReviewFlow();
request.addOnCompleteListener(task -> {
    if (task.isSuccessful()) {
        ReviewInfo reviewInfo = task.getResult();
        Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
        flow.addOnCompleteListener(task2 -> { /* review flow finished */ });
    }
});
```

---

## Update Cadence Recommendation

Google Play's algorithm rewards apps that update regularly:
- Aim for at least one update per month initially
- Even small bug fixes count as updates
- Each update is an opportunity for fresh release notes

---

## Localization Opportunities (Future)

High-value languages to add store listing translations:
1. Spanish (es) — large Android market
2. Portuguese/Brazil (pt-BR) — huge Android user base
3. German (de)
4. French (fr)
5. Japanese (ja)
6. Hindi (hi)

UI localization is separate (requires Android string resource files per locale).
