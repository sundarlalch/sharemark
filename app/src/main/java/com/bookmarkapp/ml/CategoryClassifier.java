package com.bookmarkapp.ml;

import android.content.Context;

import com.bookmarkapp.data.model.Category;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CategoryClassifier {

    private final Context context;
    private final Map<String, String> domainCategoryMap;
    private final Map<String, Set<String>> keywordCategoryMap;

    public CategoryClassifier(Context context) {
        this.context = context;
        this.domainCategoryMap = initializeDomainMap();
        this.keywordCategoryMap = initializeKeywordMap();
    }

    public String classify(String url, String title, String description) {
        // First try domain-based classification
        String domainCategory = classifyByDomain(url);
        if (domainCategory != null && !domainCategory.equals(Category.OTHER)) {
            return domainCategory;
        }

        // Then try keyword-based classification
        String keywordCategory = classifyByKeywords(title, description);
        if (keywordCategory != null) {
            return keywordCategory;
        }

        // Default to Other
        return Category.OTHER;
    }

    private String classifyByDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) return Category.OTHER;

            host = host.toLowerCase();
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            // Check exact domain match
            if (domainCategoryMap.containsKey(host)) {
                return domainCategoryMap.get(host);
            }

            // Check if domain contains known patterns
            for (Map.Entry<String, String> entry : domainCategoryMap.entrySet()) {
                if (host.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        } catch (URISyntaxException e) {
            // Invalid URL, continue with other classification methods
        }

        return Category.OTHER;
    }

    private String classifyByKeywords(String title, String description) {
        String text = ((title != null ? title : "") + " " + (description != null ? description : "")).toLowerCase();

        Map<String, Integer> categoryScores = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : keywordCategoryMap.entrySet()) {
            String category = entry.getKey();
            Set<String> keywords = entry.getValue();
            int score = 0;

            for (String keyword : keywords) {
                if (text.contains(keyword.toLowerCase())) {
                    score++;
                }
            }

            if (score > 0) {
                categoryScores.put(category, score);
            }
        }

        // Return category with highest score
        String bestCategory = null;
        int highestScore = 0;

        for (Map.Entry<String, Integer> entry : categoryScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                bestCategory = entry.getKey();
            }
        }

        return bestCategory;
    }

    private Map<String, String> initializeDomainMap() {
        Map<String, String> map = new HashMap<>();

        // News & Articles
        map.put("nytimes.com", Category.NEWS_ARTICLES);
        map.put("bbc.com", Category.NEWS_ARTICLES);
        map.put("cnn.com", Category.NEWS_ARTICLES);
        map.put("reuters.com", Category.NEWS_ARTICLES);
        map.put("theguardian.com", Category.NEWS_ARTICLES);
        map.put("washingtonpost.com", Category.NEWS_ARTICLES);
        map.put("medium.com", Category.NEWS_ARTICLES);
        map.put("substack.com", Category.NEWS_ARTICLES);
        map.put("news.ycombinator.com", Category.NEWS_ARTICLES);

        // Social Media
        map.put("twitter.com", Category.SOCIAL_MEDIA);
        map.put("x.com", Category.SOCIAL_MEDIA);
        map.put("facebook.com", Category.SOCIAL_MEDIA);
        map.put("instagram.com", Category.SOCIAL_MEDIA);
        map.put("linkedin.com", Category.SOCIAL_MEDIA);
        map.put("reddit.com", Category.SOCIAL_MEDIA);
        map.put("tiktok.com", Category.SOCIAL_MEDIA);
        map.put("threads.net", Category.SOCIAL_MEDIA);
        map.put("mastodon.social", Category.SOCIAL_MEDIA);

        // Shopping
        map.put("amazon.com", Category.SHOPPING);
        map.put("ebay.com", Category.SHOPPING);
        map.put("walmart.com", Category.SHOPPING);
        map.put("target.com", Category.SHOPPING);
        map.put("etsy.com", Category.SHOPPING);
        map.put("aliexpress.com", Category.SHOPPING);
        map.put("shopify.com", Category.SHOPPING);
        map.put("bestbuy.com", Category.SHOPPING);

        // Entertainment/Video
        map.put("youtube.com", Category.ENTERTAINMENT);
        map.put("youtu.be", Category.ENTERTAINMENT);
        map.put("netflix.com", Category.ENTERTAINMENT);
        map.put("hulu.com", Category.ENTERTAINMENT);
        map.put("disneyplus.com", Category.ENTERTAINMENT);
        map.put("twitch.tv", Category.ENTERTAINMENT);
        map.put("spotify.com", Category.ENTERTAINMENT);
        map.put("vimeo.com", Category.ENTERTAINMENT);
        map.put("imdb.com", Category.ENTERTAINMENT);

        // Development/Tech
        map.put("github.com", Category.DEVELOPMENT);
        map.put("gitlab.com", Category.DEVELOPMENT);
        map.put("stackoverflow.com", Category.DEVELOPMENT);
        map.put("developer.android.com", Category.DEVELOPMENT);
        map.put("developer.apple.com", Category.DEVELOPMENT);
        map.put("npmjs.com", Category.DEVELOPMENT);
        map.put("pypi.org", Category.DEVELOPMENT);
        map.put("docs.microsoft.com", Category.DEVELOPMENT);
        map.put("aws.amazon.com", Category.DEVELOPMENT);
        map.put("cloud.google.com", Category.DEVELOPMENT);

        // Education
        map.put("coursera.org", Category.EDUCATION);
        map.put("udemy.com", Category.EDUCATION);
        map.put("edx.org", Category.EDUCATION);
        map.put("khanacademy.org", Category.EDUCATION);
        map.put("udacity.com", Category.EDUCATION);
        map.put("wikipedia.org", Category.EDUCATION);
        map.put("britannica.com", Category.EDUCATION);

        // Finance
        map.put("bloomberg.com", Category.FINANCE);
        map.put("wsj.com", Category.FINANCE);
        map.put("investopedia.com", Category.FINANCE);
        map.put("yahoo.com/finance", Category.FINANCE);
        map.put("cnbc.com", Category.FINANCE);
        map.put("marketwatch.com", Category.FINANCE);
        map.put("coinbase.com", Category.FINANCE);
        map.put("robinhood.com", Category.FINANCE);

        // Travel
        map.put("booking.com", Category.TRAVEL);
        map.put("airbnb.com", Category.TRAVEL);
        map.put("expedia.com", Category.TRAVEL);
        map.put("tripadvisor.com", Category.TRAVEL);
        map.put("kayak.com", Category.TRAVEL);
        map.put("hotels.com", Category.TRAVEL);

        // Food & Recipes
        map.put("allrecipes.com", Category.FOOD);
        map.put("foodnetwork.com", Category.FOOD);
        map.put("epicurious.com", Category.FOOD);
        map.put("bonappetit.com", Category.FOOD);
        map.put("seriouseats.com", Category.FOOD);
        map.put("doordash.com", Category.FOOD);
        map.put("ubereats.com", Category.FOOD);
        map.put("grubhub.com", Category.FOOD);

        // Sports
        map.put("espn.com", Category.SPORTS);
        map.put("nba.com", Category.SPORTS);
        map.put("nfl.com", Category.SPORTS);
        map.put("mlb.com", Category.SPORTS);
        map.put("bleacherreport.com", Category.SPORTS);
        map.put("sportsillustrated.com", Category.SPORTS);

        return map;
    }

    private Map<String, Set<String>> initializeKeywordMap() {
        Map<String, Set<String>> map = new HashMap<>();

        Set<String> newsKeywords = new HashSet<>();
        newsKeywords.add("news");
        newsKeywords.add("article");
        newsKeywords.add("breaking");
        newsKeywords.add("headline");
        newsKeywords.add("report");
        newsKeywords.add("journalism");
        map.put(Category.NEWS_ARTICLES, newsKeywords);

        Set<String> shoppingKeywords = new HashSet<>();
        shoppingKeywords.add("buy");
        shoppingKeywords.add("shop");
        shoppingKeywords.add("price");
        shoppingKeywords.add("cart");
        shoppingKeywords.add("checkout");
        shoppingKeywords.add("discount");
        shoppingKeywords.add("sale");
        shoppingKeywords.add("product");
        map.put(Category.SHOPPING, shoppingKeywords);

        Set<String> entertainmentKeywords = new HashSet<>();
        entertainmentKeywords.add("video");
        entertainmentKeywords.add("watch");
        entertainmentKeywords.add("movie");
        entertainmentKeywords.add("show");
        entertainmentKeywords.add("stream");
        entertainmentKeywords.add("episode");
        entertainmentKeywords.add("music");
        entertainmentKeywords.add("song");
        map.put(Category.ENTERTAINMENT, entertainmentKeywords);

        Set<String> devKeywords = new HashSet<>();
        devKeywords.add("code");
        devKeywords.add("programming");
        devKeywords.add("developer");
        devKeywords.add("api");
        devKeywords.add("github");
        devKeywords.add("repository");
        devKeywords.add("software");
        devKeywords.add("documentation");
        devKeywords.add("tutorial");
        map.put(Category.DEVELOPMENT, devKeywords);

        Set<String> educationKeywords = new HashSet<>();
        educationKeywords.add("learn");
        educationKeywords.add("course");
        educationKeywords.add("education");
        educationKeywords.add("university");
        educationKeywords.add("school");
        educationKeywords.add("lesson");
        educationKeywords.add("study");
        map.put(Category.EDUCATION, educationKeywords);

        Set<String> financeKeywords = new HashSet<>();
        financeKeywords.add("stock");
        financeKeywords.add("invest");
        financeKeywords.add("market");
        financeKeywords.add("finance");
        financeKeywords.add("banking");
        financeKeywords.add("crypto");
        financeKeywords.add("trading");
        map.put(Category.FINANCE, financeKeywords);

        Set<String> travelKeywords = new HashSet<>();
        travelKeywords.add("travel");
        travelKeywords.add("hotel");
        travelKeywords.add("flight");
        travelKeywords.add("vacation");
        travelKeywords.add("destination");
        travelKeywords.add("booking");
        travelKeywords.add("trip");
        map.put(Category.TRAVEL, travelKeywords);

        Set<String> foodKeywords = new HashSet<>();
        foodKeywords.add("recipe");
        foodKeywords.add("cook");
        foodKeywords.add("food");
        foodKeywords.add("restaurant");
        foodKeywords.add("meal");
        foodKeywords.add("ingredients");
        foodKeywords.add("cuisine");
        map.put(Category.FOOD, foodKeywords);

        Set<String> sportsKeywords = new HashSet<>();
        sportsKeywords.add("sports");
        sportsKeywords.add("game");
        sportsKeywords.add("team");
        sportsKeywords.add("score");
        sportsKeywords.add("player");
        sportsKeywords.add("championship");
        sportsKeywords.add("league");
        map.put(Category.SPORTS, sportsKeywords);

        return map;
    }
}
