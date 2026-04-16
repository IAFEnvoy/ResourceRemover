package com.iafenvoy.resource.remover.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringMatcher {
    private final List<Pattern> patterns;

    public StringMatcher(List<String> patternList) {
        this.patterns = patternList.stream()
                .map(String::trim) //Trim whitespace
                .filter(s -> !s.isBlank() && !s.startsWith("#")) //Start with # is comment line
                .map(StringMatcher::buildPattern)
                .toList();
    }

    public boolean match(@NotNull String s) {
        return this.patterns.stream().anyMatch(rule -> rule.matcher(s).matches());
    }

    private static Pattern buildPattern(String expr) {
        try {
            // 情况1：包含正则元字符 → 按正则处理
            if (hasRegexMetaChars(expr)) {
                return Pattern.compile(expr);
            }

            // 情况2：包含 * 但无其他正则元字符 → 通配符
            if (expr.contains("*")) {
                String regex = "^" + Pattern.quote(expr).replace("*", "\\E.*\\Q") + "$";
                return Pattern.compile(regex);
            }

            // 情况3：普通字符串 → 全匹配
            return Pattern.compile("^" + Pattern.quote(expr) + "$");
        } catch (PatternSyntaxException e) {
            // 正则写错 → 降级成全匹配
            return Pattern.compile("^" + Pattern.quote(expr) + "$");
        }
    }

    private static boolean hasRegexMetaChars(String s) {
        return s.matches(".*[.+?|()\\[\\]{}^$\\\\].*");
    }
}