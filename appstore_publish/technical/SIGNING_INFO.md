# Signing Information — ShareMark

IMPORTANT: Keep your keystore file and passwords secure. Never commit them to a public repository.

---

## Keystore Details

| Field            | Value                           |
|------------------|---------------------------------|
| Keystore file    | `app/sharemark-release.jks`     |
| Key alias        | `sharemark`                     |
| Keystore type    | JKS                             |

> Passwords are stored separately and should NOT be committed to version control.
> If using CI/CD, store passwords in environment variables or secret managers (e.g., GitHub Secrets, Google Secret Manager).

---

## Build Commands

### Build Release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/sharemark.apk
```

### Build Release AAB (recommended for Play Store)
```bash
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

### Verify APK Signature
```bash
apksigner verify --verbose --print-certs app/build/outputs/apk/release/sharemark.apk
```

### Check Keystore Info
```bash
keytool -list -v -keystore app/sharemark-release.jks -alias sharemark
```

---

## Google Play App Signing (Recommended)

Google Play offers **App Signing by Google Play**, which:
- Adds an extra layer of protection for your signing key
- Allows recovery if you lose your key
- Is required for AABs

**To enroll:**
1. When uploading your first release in Play Console, you'll be offered to opt into App Signing.
2. Google will manage the final signing key used on devices.
3. You sign the AAB with your "upload key" (your existing keystore).
4. Keep your upload keystore safe — it's used to authenticate uploads.

---

## Key Backup Instructions

Store the following securely (password manager, encrypted backup, cloud key vault):
1. `sharemark-release.jks` — the keystore file
2. Keystore password
3. Key alias name
4. Key password

**WARNING:** If you lose your keystore, you CANNOT update your app on Google Play. A new keystore = a new app listing.

---

## CI/CD Integration (Optional)

For automated builds, add to your CI environment:
```bash
export KEYSTORE_PATH=/path/to/sharemark-release.jks
export KEYSTORE_PASSWORD=<from-secret-manager>
export KEY_ALIAS=sharemark
export KEY_PASSWORD=<from-secret-manager>
```

Then reference in `app/build.gradle`:
```groovy
signingConfigs {
    release {
        storeFile file(System.getenv("KEYSTORE_PATH"))
        storePassword System.getenv("KEYSTORE_PASSWORD")
        keyAlias System.getenv("KEY_ALIAS")
        keyPassword System.getenv("KEY_PASSWORD")
    }
}
```
