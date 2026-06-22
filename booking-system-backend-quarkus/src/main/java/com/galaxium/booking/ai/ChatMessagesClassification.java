package com.galaxium.booking.ai;

import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Singleton
public class ChatMessagesClassification {

    static List<String> keywordsToFind = Arrays.
        asList("bookings", "flights", "destination", "time", "user", "booking", "detail");

    public boolean validConversation(String text) {
        return containsAnyKeyword(text, keywordsToFind);
    }

    /**
     * Checks if the input text contains ANY of the specified keywords as whole words.
     * Case-insensitive match.
     */
    public static boolean containsAnyKeyword(String text, List<String> keywords) {
        if (text == null || keywords == null || keywords.isEmpty()) {
            return false;
        }

        // 1. Escape keywords to prevent issues with regex special characters and join with '|' (OR operator)
        String regexPattern = keywords.stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|"));

        // 2. Wrap inside word boundary markers (\b) to match full words only
        // Case-insensitive flag enabled (Pattern.CASE_INSENSITIVE)
        Pattern pattern = Pattern.compile("\\b(" + regexPattern + ")\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        // 3. Returns true if at least one match is found
        return matcher.find();
    }
}
