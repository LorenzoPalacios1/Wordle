import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WordCheck {
    // This function checks if a given string is an actual word
    public static boolean checkWord(String word) throws MalformedURLException, IOException {
        final URLConnection dictionary = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                .openConnection();
        dictionary.setReadTimeout(10000);

        // Catching anything that isn't a word
        try {
            final InputStream response = dictionary.getInputStream();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Tests
    // A completely successful run will print out a success message
    // Otherwise, any relevant data will be printed to 'System.err'
    public static void main(String[] args) throws IOException {
        String words[] = new String[5];

        for (int i = 0; i < words.length; i++) {
            words[i] = RandomWord.generateWord();

            if (words[i] == null) {
                System.err.printf("Test %d returned null\n", i);
                continue;
            }

            if (checkWord(words[i])) {
                System.out.printf("WordCheck Test %d success: %s is a word\n", i, words[i]);
            } else {
                System.out.printf("WordCheck Test %d success: %s is NOT a word\n", i, words[i]);
            }
        }

        System.out.println("WordCheck tests complete");
    }
}