package com.bookmarkapp.ml;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.util.MetadataExtractor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebPageAnalyzer {

    private final Context context;
    private final CategoryClassifier classifier;
    private final MetadataExtractor metadataExtractor;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public WebPageAnalyzer(Context context) {
        this.context = context;
        this.classifier = new CategoryClassifier(context);
        this.metadataExtractor = new MetadataExtractor();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void analyzeUrl(String url, AnalysisCallback callback) {
        executorService.execute(() -> {
            try {
                // Extract metadata from the webpage
                MetadataExtractor.WebPageMetadata metadata = metadataExtractor.extract(url);

                // Classify the page
                String categoryName = classifier.classify(
                        url,
                        metadata.getTitle(),
                        metadata.getDescription()
                );

                // Extract domain
                String domain = extractDomain(url);

                // Create bookmark object
                Bookmark bookmark = new Bookmark(
                        url,
                        metadata.getTitle() != null ? metadata.getTitle() : domain,
                        metadata.getDescription(),
                        metadata.getThumbnailUrl(),
                        null, // Category ID will be set later
                        System.currentTimeMillis(),
                        false,
                        domain
                );

                // Callback on main thread
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onAnalysisComplete(bookmark, categoryName);
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onAnalysisError(e);
                    }
                });
            }
        });
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host != null && host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public interface AnalysisCallback {
        void onAnalysisComplete(Bookmark bookmark, String categoryName);
        void onAnalysisError(Exception e);
    }
}
