public class TextJustifier{
    public static String[] justifyText(String[] words, int maxWidth) {
        int n = words.length;
        String[] result = new String[n];
        int lineCount = 0;

        int i = 0;
        while (i < n) {
            int lineStart = i;
            int lineLength = words[i].length();
            i++;

            while (i < n && lineLength + words[i].length() + (i - lineStart) <= maxWidth) {
                lineLength += words[i].length();
                i++;
            }

            int wordCount = i - lineStart;
            int spaceCount = maxWidth - lineLength;

            if (i == n || wordCount == 1) {
                StringBuilder line = new StringBuilder(words[lineStart]);
                for (int j = lineStart + 1; j < i; j++) {
                    line.append(" ").append(words[j]);
                }
                while (line.length() < maxWidth) {
                    line.append(" ");
                }
                result[lineCount++] = line.toString();
            } else {

                int spaceSlots = wordCount - 1;
                int spaceBetween = spaceCount / spaceSlots;
                int extraSpaces = spaceCount % spaceSlots;

                StringBuilder line = new StringBuilder();
                for (int j = lineStart; j < i - 1; j++) {
                    line.append(words[j]);
                    int spacesToAdd = spaceBetween + (j - lineStart < extraSpaces ? 1 : 0);
                    for (int k = 0; k < spacesToAdd; k++) {
                        line.append(" ");
                    }
                }
                line.append(words[i - 1]);

                result[lineCount++] = line.toString();
            }
        }

        String[] finalResult = new String[lineCount];
        for (int j = 0; j < lineCount; j++) {
            finalResult[j] = result[j];
        }
        return finalResult;
    }
}


