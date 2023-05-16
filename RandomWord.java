import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RandomWord {
    
    public static String generateWord() throws IOException {
        final URLConnection wordSource = new URL("https://random-word-api.herokuapp.com/word?length=5")
                .openConnection();
        wordSource.setReadTimeout(5000);

        final InputStream word = wordSource.getInputStream();

        /*
         * The raw returned word contains two prefixed and suffixed brackets and double
         * quotes, so we discard the first two bytes.
         */
        word.read();
        word.read();

        String formattedWord = "";
        for (int i = 0; i < 5; i++) {
            formattedWord += (char) word.read();
        }

        // Returning word
        if (!formattedWord.isBlank()) {
            return formattedWord;
        }
        return null;
    }

    // Tests
    public static void main(String[] args) throws IOException {
        String words[] = new String[5];

        boolean failed;
        for (int i = 0; i < words.length; i++) {
            failed = false;
            words[i] = generateWord();

            if (words[i] == null) {
                System.err.printf("Test %d returned null\n", i);
                continue;
            }

            if (words[i].length() != 5) {
                System.err.printf("Test %d returned string length not equal to 5\nReturned data: %s\n", i, words[i]);
                failed = true;
            }

            if (!words[i].matches("[a-zA-Z]+")) {
                System.err.printf("Test %d returned non-alphabetical characters\nReturned data: %s\n", i, words[i]);
                failed = true;
            }

            if (!failed)
                System.out.printf("Test %d success: %s\n", i, words[i]);
        }

        System.out.println("Tests complete");
    }
}