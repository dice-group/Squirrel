package com.squirrel.Utilities;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * nearly obsolete class that does the same like Thymeleaf, only smarter^^
 * <p>
 * http://www.thymeleaf.org/:
 * Thymeleaf is a modern server-side Java template engine for both web and standalone environments.
 * Thymeleaf's main goal is to bring elegant natural templates to your development workflow — HTML that can be correctly displayed in browsers and also work as static prototypes, allowing for stronger collaboration in development teams.
 * With modules for Spring Framework, a host of integrations with your favourite tools, and the ability to plug in your own functionality, Thymeleaf is ideal for modern-day HTML5 JVM web development — although there is much more it can do.
 *
 * @author Philipp Heinisch
 */
public abstract class TemplateHelper {

    public static String replace(String input, Map<String, List<String>> replacements) {
        Pattern pattern = Pattern.compile("\\$\\{\\w+\\}");
        StringBuilder output = new StringBuilder(input);
        Matcher m = pattern.matcher(output.toString());

        while (m.find()) {
            String replaceStringKey = m.group().substring(2, m.group().length() - 1);
            Optional<Map.Entry<String, List<String>>> match = replacements.entrySet().stream().filter(e -> e.getKey().equals(replaceStringKey)).findFirst();
            final StringBuilder replaceString = new StringBuilder("");
            match.ifPresent(matchEntry -> {
                if (matchEntry.getValue().isEmpty()) {
                    replaceString.append("No data to replace...");
                } else if (matchEntry.getValue().size() == 1) {
                    replaceString.append(matchEntry.getValue().get(0));
                } else {
                    replaceString.append("<ul>");
                    matchEntry.getValue().forEach(matchEntryElement -> replaceString.append("<li>" + matchEntryElement + "</li>"));
                    replaceString.append("</ul>");
                }
            });
            output = output.replace(m.start(), m.end(), replaceString.toString());
            m = pattern.matcher(output.toString());
        }

        return output.toString();
    }
}
