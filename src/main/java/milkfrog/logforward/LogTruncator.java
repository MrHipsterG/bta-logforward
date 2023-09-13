package milkfrog.logforward;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogTruncator {

    // Pattern to match floating-point numbers
    private static final Pattern FLOAT_PATTERN = Pattern.compile("(-?\\d+\\.\\d+)");
    public static String truncateCoordinates(String line) {
        StringBuffer modifiedLine = new StringBuffer();

        // Matcher to find occurrences of the pattern in the line
        Matcher matcher = FLOAT_PATTERN.matcher(line);

        while (matcher.find()) {
            // Group 0 is the entire matched sequence
            String floatStr = matcher.group(0);
            int truncated = (int) Math.floor(Double.parseDouble(floatStr));
            // Replace the original coordinate with its truncated version
            matcher.appendReplacement(modifiedLine, Integer.toString(truncated));
        }
        // Append the remaining part of the line
        matcher.appendTail(modifiedLine);

        return modifiedLine.toString();
    }
}
