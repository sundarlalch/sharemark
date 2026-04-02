package com.bookmarkapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.repository.BookmarkRepository;
import com.bookmarkapp.ml.WebPageAnalyzer;

import java.util.regex.Matcher;

public class ShareReceiverActivity extends AppCompatActivity {

    private BookmarkRepository repository;
    private WebPageAnalyzer webPageAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repository = new BookmarkRepository(getApplication());
        webPageAnalyzer = new WebPageAnalyzer(this);

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) {
            finishWithError("No data received");
            return;
        }

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            } else {
                finishWithError("Unsupported content type");
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            // Handle URLs opened directly
            if (intent.getData() != null) {
                processUrl(intent.getData().toString(), null);
            } else {
                finishWithError("No URL received");
            }
        } else {
            finishWithError("Unsupported action");
        }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);

        if (sharedText == null || sharedText.isEmpty()) {
            finishWithError("No text received");
            return;
        }

        // Extract URL from the shared text
        String url = extractUrl(sharedText);

        if (url == null) {
            finishWithError("No valid URL found in shared text");
            return;
        }

        // Extract the non-URL text (actual post caption/content shared from Facebook, Instagram, etc.)
        String postText = extractNonUrlText(sharedText, url, subject);

        processUrl(url, postText);
    }

    private String extractNonUrlText(String sharedText, String extractedUrl, String subject) {
        // Remove the URL from the shared text to isolate the post content
        String textWithoutUrl = sharedText.replace(extractedUrl, "").trim();
        // Clean up extra whitespace
        textWithoutUrl = textWithoutUrl.replaceAll("\\s+", " ").trim();

        // If subject is meaningful (Facebook often populates EXTRA_SUBJECT), combine with text
        if (subject != null && !subject.isEmpty()) {
            if (!textWithoutUrl.isEmpty() && !textWithoutUrl.equalsIgnoreCase(subject)) {
                return subject + " - " + textWithoutUrl;
            }
            return subject;
        }

        return textWithoutUrl.isEmpty() ? null : textWithoutUrl;
    }

    private String extractUrl(String text) {
        if (text == null) return null;

        // Try to find a URL in the text
        Matcher matcher = Patterns.WEB_URL.matcher(text);
        if (matcher.find()) {
            String url = matcher.group();

            // Ensure URL has a scheme
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            return url;
        }

        // Check if the whole text is a URL
        if (Patterns.WEB_URL.matcher(text.trim()).matches()) {
            String url = text.trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            return url;
        }

        return null;
    }

    private void processUrl(String url, String sharedText) {
        // Check if bookmark already exists
        repository.getBookmarkByUrl(url, existingBookmark -> {
            if (existingBookmark != null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bookmark already exists", Toast.LENGTH_SHORT).show();
                    openMainActivity();
                });
                return;
            }

            // Analyze the URL and save bookmark
            webPageAnalyzer.analyzeUrl(url, new WebPageAnalyzer.AnalysisCallback() {
                @Override
                public void onAnalysisComplete(Bookmark bookmark, String categoryName) {
                    // Use shared post text as description if scraping returned nothing useful
                    if (sharedText != null && !sharedText.isEmpty()) {
                        String desc = bookmark.getDescription();
                        if (desc == null || desc.isEmpty() || isGenericSocialDescription(desc)) {
                            bookmark.setDescription(sharedText);
                        }
                    }

                    // Find category ID and save
                    repository.getCategoryByName(categoryName, category -> {
                        if (category != null) {
                            bookmark.setCategoryId(category.getId());
                        }

                        repository.insert(bookmark, id -> {
                            runOnUiThread(() -> {
                                String title = bookmark.getTitle() != null ? bookmark.getTitle() : "Bookmark";
                                Toast.makeText(
                                        ShareReceiverActivity.this,
                                        "Saved: " + truncateText(title, 30),
                                        Toast.LENGTH_SHORT
                                ).show();
                                openMainActivity();
                            });
                        });
                    });
                }

                @Override
                public void onAnalysisError(Exception e) {
                    // Save with basic info even if analysis fails; use shared text as description
                    Bookmark basicBookmark = new Bookmark(
                            url,
                            null,
                            sharedText,
                            null,
                            null,
                            System.currentTimeMillis(),
                            false,
                            extractDomain(url)
                    );

                    repository.insert(basicBookmark, id -> {
                        runOnUiThread(() -> {
                            Toast.makeText(
                                    ShareReceiverActivity.this,
                                    "Bookmark saved",
                                    Toast.LENGTH_SHORT
                            ).show();
                            openMainActivity();
                        });
                    });
                }
            });
        });
    }

    private boolean isGenericSocialDescription(String description) {
        String lower = description.toLowerCase();
        return lower.equals("shared from facebook") || lower.equals("shared from instagram") ||
               lower.equals("shared from twitter/x") || lower.equals("shared from linkedin");
    }

    private String extractDomain(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String host = uri.getHost();
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return null;
        }
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private void openMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void finishWithError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webPageAnalyzer != null) {
            webPageAnalyzer.shutdown();
        }
    }
}
