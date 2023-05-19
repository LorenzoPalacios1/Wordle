import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RandomWord {
    // Generates a single five letter word per call
    // Returns a word upon success and null upon failure
    public static String generateWord() throws IOException {
        final URLConnection wordSource = new URL("https://random-word-api.herokuapp.com/word?length=5")
                .openConnection();
        wordSource.setReadTimeout(5000);

        final InputStream word = wordSource.getInputStream();

        // The raw return from 'wordSource' contains a bracket and double quotes at its
        // beginning and end, so we discard the first two bytes below:
        word.read();
        word.read();

        // readAllBytes() is a function in newer Java versions, so it's unused
        // to preserve backwards compatability
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
    // A completely successful run will print out a success message
    // Otherwise, any relevant data will be printed to 'System.err'
    public static void main(String[] args) throws IOException {
        String word;

        boolean failed;
        for (int i = 0; i < 5; i++) {
            failed = false;
            word = generateWord();

            if (word == null) {
                System.err.printf("RandomWord Test %d returned null\n", i);
                continue;
            }

            if (word.length() != 5) {
                System.err.printf("RandomWord Test %d returned string length not equal to 5\nReturned data: %s\n", i,
                        word);
                failed = true;
            }

            // Ensuring the returned word contains ONLY alphabetical characters
            if (!word.matches("[a-zA-Z]+")) {
                System.err.printf("RandomWord Test %d returned non-alphabetical characters\nReturned data: %s\n", i,
                        word);
                failed = true;
            }

            if (!failed)
                System.out.printf("RandomWord Test %d success: %s\n", i, word);
        }

        System.out.println("RandomWord tests complete");
    }
}