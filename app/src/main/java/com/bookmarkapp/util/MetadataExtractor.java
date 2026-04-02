package com.bookmarkapp.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetadataExtractor {

    private static final int TIMEOUT_MS = 15000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    // Facebook serves full OG metadata to its own crawler UA without requiring login
    private static final String USER_AGENT_FACEBOOK = "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)";

    public WebPageMetadata extract(String url) {
        WebPageMetadata metadata = new WebPageMetadata();
        metadata.setUrl(url);

        // First, try to extract info from URL for social media sites
        extractFromUrlPattern(url, metadata);

        String lowerUrl = url.toLowerCase();
        boolean isFacebook  = lowerUrl.contains("facebook.com") || lowerUrl.contains("fb.com");
        boolean isInstagram = lowerUrl.contains("instagram.com");

        try {
            Connection connection = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .followRedirects(true)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true);

            if (isFacebook) {
                // Facebook serves OG metadata to its own externalhit crawler without a login wall
                connection.userAgent(USER_AGENT_FACEBOOK);
            } else if (isInstagram) {
                // Instagram returns OG tags for public posts when Sec-Fetch headers match a real browser
                connection.userAgent(USER_AGENT)
                          .header("Sec-Fetch-Dest", "document")
                          .header("Sec-Fetch-Mode", "navigate")
                          .header("Sec-Fetch-Site", "none")
                          .header("Sec-Fetch-User", "?1")
                          .header("Cache-Control", "max-age=0");
            } else {
                connection.userAgent(USER_AGENT);
            }

            Connection.Response response = connection.execute();

            // Check if we got a valid response
            if (response.statusCode() >= 200 && response.statusCode() < 400) {
                Document doc = response.parse();

                // Extract title (only if we don't have one from URL pattern)
                String title = extractTitle(doc);
                if (title != null && !title.isEmpty()) {
                    // Prefer scraped title over URL-based one, unless it's a login page
                    if (!isLoginPageTitle(title)) {
                        metadata.setTitle(title);
                    }
                }

                // Extract description
                String description = extractDescription(doc);
                if (description != null && !description.isEmpty() && !isLoginPageDescription(description)) {
                    metadata.setDescription(description);
                }

                // Extract thumbnail/image
                String thumbnailUrl = extractThumbnail(doc, url);
                if (thumbnailUrl != null) {
                    metadata.setThumbnailUrl(thumbnailUrl);
                }

                // Extract favicon
                String faviconUrl = extractFavicon(doc, url);
                metadata.setFaviconUrl(faviconUrl);
            }

        } catch (IOException e) {
            // URL pattern extraction already done, just continue
        }

        // Final fallback - ensure we have at least a basic title
        if (metadata.getTitle() == null || metadata.getTitle().isEmpty()) {
            metadata.setTitle(createFallbackTitle(url));
        }

        return metadata;
    }

    private void extractFromUrlPattern(String url, WebPageMetadata metadata) {
        String lowerUrl = url.toLowerCase();

        // Facebook
        if (lowerUrl.contains("facebook.com") || lowerUrl.contains("fb.com")) {
            metadata.setTitle(extractFacebookInfo(url));
            metadata.setDescription("Shared from Facebook");
            return;
        }

        // LinkedIn
        if (lowerUrl.contains("linkedin.com")) {
            metadata.setTitle(extractLinkedInInfo(url));
            metadata.setDescription("Shared from LinkedIn");
            return;
        }

        // Twitter/X
        if (lowerUrl.contains("twitter.com") || lowerUrl.contains("x.com")) {
            metadata.setTitle(extractTwitterInfo(url));
            metadata.setDescription("Shared from Twitter/X");
            return;
        }

        // Instagram
        if (lowerUrl.contains("instagram.com")) {
            metadata.setTitle(extractInstagramInfo(url));
            metadata.setDescription("Shared from Instagram");
            return;
        }

        // YouTube
        if (lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be")) {
            // YouTube usually allows scraping, but add fallback
            String videoId = extractYouTubeVideoId(url);
            if (videoId != null) {
                metadata.setThumbnailUrl("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg");
            }
            return;
        }
    }

    private String extractFacebookInfo(String url) {
        // Try to extract username or page name from URL
        // Patterns: /username, /pages/PageName, /groups/groupname, /share/p/xxx
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path != null && path.length() > 1) {
                String[] parts = path.split("/");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (!part.isEmpty() && !isCommonPathSegment(part)) {
                        // Try to make it readable
                        String readable = makeReadable(part);
                        if (readable.length() > 2) {
                            return "Facebook: " + readable;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return "Facebook Post";
    }

    private String extractLinkedInInfo(String url) {
        // Patterns: /in/username, /posts/username_activity-xxx, /company/name
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path != null) {
                // /in/username pattern
                Pattern inPattern = Pattern.compile("/in/([^/]+)");
                Matcher inMatcher = inPattern.matcher(path);
                if (inMatcher.find()) {
                    return "LinkedIn: " + makeReadable(inMatcher.group(1));
                }

                // /posts/username_activity pattern
                Pattern postPattern = Pattern.compile("/posts/([^_]+)");
                Matcher postMatcher = postPattern.matcher(path);
                if (postMatcher.find()) {
                    return "LinkedIn Post by " + makeReadable(postMatcher.group(1));
                }

                // /company/name pattern
                Pattern companyPattern = Pattern.compile("/company/([^/]+)");
                Matcher companyMatcher = companyPattern.matcher(path);
                if (companyMatcher.find()) {
                    return "LinkedIn: " + makeReadable(companyMatcher.group(1));
                }
            }
        } catch (Exception ignored) {}

        return "LinkedIn Post";
    }

    private String extractTwitterInfo(String url) {
        // Pattern: /username/status/xxx
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path != null) {
                Pattern statusPattern = Pattern.compile("/([^/]+)/status");
                Matcher matcher = statusPattern.matcher(path);
                if (matcher.find()) {
                    String username = matcher.group(1);
                    if (!username.isEmpty() && !username.equals("i")) {
                        return "Tweet by @" + username;
                    }
                }

                // Just username
                String[] parts = path.split("/");
                if (parts.length >= 2 && !parts[1].isEmpty()) {
                    return "Twitter: @" + parts[1];
                }
            }
        } catch (Exception ignored) {}

        return "Twitter Post";
    }

    private String extractInstagramInfo(String url) {
        // Patterns: /p/xxx, /reel/xxx, /username
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path != null) {
                if (path.contains("/p/") || path.contains("/reel/")) {
                    return "Instagram Post";
                }

                String[] parts = path.split("/");
                if (parts.length >= 2 && !parts[1].isEmpty()) {
                    return "Instagram: @" + parts[1];
                }
            }
        } catch (Exception ignored) {}

        return "Instagram Post";
    }

    private String extractYouTubeVideoId(String url) {
        // Patterns: ?v=xxx, /watch?v=xxx, youtu.be/xxx
        try {
            if (url.contains("youtu.be/")) {
                URI uri = new URI(url);
                String path = uri.getPath();
                if (path != null && path.length() > 1) {
                    return path.substring(1).split("[?&]")[0];
                }
            }

            Pattern pattern = Pattern.compile("[?&]v=([^&]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception ignored) {}

        return null;
    }

    private boolean isCommonPathSegment(String segment) {
        String lower = segment.toLowerCase();
        return lower.equals("share") || lower.equals("p") || lower.equals("posts") ||
               lower.equals("photo") || lower.equals("photos") || lower.equals("video") ||
               lower.equals("videos") || lower.equals("story") || lower.equals("stories") ||
               lower.equals("reel") || lower.equals("reels") || lower.equals("watch") ||
               lower.equals("feed") || lower.equals("groups") || lower.equals("pages") ||
               lower.equals("events") || lower.equals("in") || lower.equals("company") ||
               segment.matches("^[0-9]+$") || segment.length() > 30;
    }

    private String makeReadable(String text) {
        if (text == null) return "";

        try {
            text = URLDecoder.decode(text, "UTF-8");
        } catch (Exception ignored) {}

        // Replace common separators with spaces
        text = text.replaceAll("[-_.]", " ");

        // Capitalize first letter of each word
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (result.length() > 0) result.append(" ");
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }

        return result.toString();
    }

    private String createFallbackTitle(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host != null) {
                if (host.startsWith("www.")) {
                    host = host.substring(4);
                }
                // Capitalize first letter
                return Character.toUpperCase(host.charAt(0)) + host.substring(1);
            }
        } catch (Exception ignored) {}

        return "Bookmark";
    }

    private boolean isLoginPageTitle(String title) {
        if (title == null) return false;
        String lower = title.toLowerCase();
        // Match titles that are purely login/signup prompts, not content titles that happen to mention these words
        return lower.equals("log in to facebook") || lower.equals("log in | facebook") ||
               lower.equals("facebook – log in or sign up") || lower.equals("facebook - log in or sign up") ||
               lower.equals("instagram") || lower.equals("log in • instagram") ||
               lower.equals("linkedin: log in or sign up") ||
               (lower.startsWith("log in") && lower.length() < 40) ||
               (lower.startsWith("sign in") && lower.length() < 40);
    }

    private boolean isLoginPageDescription(String description) {
        if (description == null) return false;
        String lower = description.toLowerCase();
        // Match only descriptions that are clearly login-wall prompts, not content descriptions
        return lower.startsWith("log in or sign up") ||
               lower.startsWith("log in to facebook") ||
               lower.startsWith("connect with friends and the world") ||
               lower.startsWith("create an account or log in") ||
               lower.startsWith("log into instagram") ||
               lower.startsWith("sign up to see");
    }

    private String extractTitle(Document doc) {
        // Try Open Graph title first
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        if (ogTitle != null && ogTitle.hasAttr("content")) {
            return ogTitle.attr("content");
        }

        // Try Twitter card title
        Element twitterTitle = doc.selectFirst("meta[name=twitter:title]");
        if (twitterTitle != null && twitterTitle.hasAttr("content")) {
            return twitterTitle.attr("content");
        }

        // Fall back to page title
        Element titleElement = doc.selectFirst("title");
        if (titleElement != null) {
            return titleElement.text();
        }

        return null;
    }

    private String extractDescription(Document doc) {
        // Try Open Graph description first
        Element ogDesc = doc.selectFirst("meta[property=og:description]");
        if (ogDesc != null && ogDesc.hasAttr("content")) {
            return ogDesc.attr("content");
        }

        // Try Twitter card description
        Element twitterDesc = doc.selectFirst("meta[name=twitter:description]");
        if (twitterDesc != null && twitterDesc.hasAttr("content")) {
            return twitterDesc.attr("content");
        }

        // Try standard meta description
        Element metaDesc = doc.selectFirst("meta[name=description]");
        if (metaDesc != null && metaDesc.hasAttr("content")) {
            return metaDesc.attr("content");
        }

        return null;
    }

    private String extractThumbnail(Document doc, String baseUrl) {
        // Try Open Graph image first
        Element ogImage = doc.selectFirst("meta[property=og:image]");
        if (ogImage != null && ogImage.hasAttr("content")) {
            return resolveUrl(ogImage.attr("content"), baseUrl);
        }

        // Try Twitter card image
        Element twitterImage = doc.selectFirst("meta[name=twitter:image]");
        if (twitterImage != null && twitterImage.hasAttr("content")) {
            return resolveUrl(twitterImage.attr("content"), baseUrl);
        }

        // Try to find a large image in the content
        Elements images = doc.select("img[src]");
        for (Element img : images) {
            String src = img.attr("abs:src");
            String width = img.attr("width");
            String height = img.attr("height");

            // Look for images that are likely to be content images
            try {
                int w = width.isEmpty() ? 0 : Integer.parseInt(width);
                int h = height.isEmpty() ? 0 : Integer.parseInt(height);
                if (w >= 200 && h >= 200) {
                    return src;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    private String extractFavicon(Document doc, String baseUrl) {
        // Try apple-touch-icon first (usually higher quality)
        Element appleTouchIcon = doc.selectFirst("link[rel=apple-touch-icon]");
        if (appleTouchIcon != null && appleTouchIcon.hasAttr("href")) {
            return resolveUrl(appleTouchIcon.attr("href"), baseUrl);
        }

        // Try standard favicon
        Element favicon = doc.selectFirst("link[rel=icon]");
        if (favicon != null && favicon.hasAttr("href")) {
            return resolveUrl(favicon.attr("href"), baseUrl);
        }

        // Try shortcut icon
        Element shortcutIcon = doc.selectFirst("link[rel='shortcut icon']");
        if (shortcutIcon != null && shortcutIcon.hasAttr("href")) {
            return resolveUrl(shortcutIcon.attr("href"), baseUrl);
        }

        // Default to /favicon.ico
        try {
            java.net.URI uri = new java.net.URI(baseUrl);
            return uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveUrl(String url, String baseUrl) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Already absolute URL
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // Protocol-relative URL
        if (url.startsWith("//")) {
            return "https:" + url;
        }

        // Relative URL - resolve against base
        try {
            java.net.URI base = new java.net.URI(baseUrl);
            java.net.URI resolved = base.resolve(url);
            return resolved.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static class WebPageMetadata {
        private String url;
        private String title;
        private String description;
        private String thumbnailUrl;
        private String faviconUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getFaviconUrl() {
            return faviconUrl;
        }

        public void setFaviconUrl(String faviconUrl) {
            this.faviconUrl = faviconUrl;
        }
    }
}
