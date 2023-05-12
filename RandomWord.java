import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RandomWord {
    public static String generateWord() throws IOException {
        final URLConnection wordSource = new URL("https://random-word-api.herokuapp.com/word?length=5")
                .openConnection();
        wordSource.setReadTimeout(3000);

        final InputStream word = wordSource.getInputStream();

        // Retrieving raw word
        byte data[] = word.readAllBytes();

        /*
         * The raw returned word contains two prefixed and suffixed brackets and double
         * quotes, so in the below for-loop we start at an index of 2 and end 2 early
         */
        String formattedWord = "";

        for (int i = 2; i < data.length - 2; i++) {
            formattedWord += (char) data[i];
        }

        // Returning word
        if (!formattedWord.isEmpty()) {
            return formattedWord;
        }
        return null;
    }

    // Tests
    public static void main(String[] args) throws IOException {
        String words[] = new String[5];

        for (int i = 0; i < words.length; i++) {
            words[i] = generateWord();

            if (words[i].isEmpty()) {
                System.err.printf("Test %d string is blank\n", i);
                continue;
            }

            if (words[i].length() != 5)
                System.err.printf("Test %d returned string length not equal to 5\nReturned data: %s\n", i, words[i]);

            if (!words[i].matches("[^a-zA-Z]"))
                System.err.printf("Test %d returned non-alphabetical characters\nReturned data: %s\n", i, words[i]);
        }
    }
}