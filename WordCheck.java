import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WordCheck {
    // This function checks if a given string is an actual word
    // Returns true if the passed word exists, otherwise false
    public static boolean checkWord(String word) throws MalformedURLException, IOException {
        final URLConnection dictionary = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                .openConnection();
        dictionary.setReadTimeout(10000);

        // If the word exists, return
        try {
            final InputStream response = dictionary.getInputStream();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
        // For anything other than FileNotFoundException
        catch (Exception other) {
            System.err.printf("Exception message: %s\n Passed data: %s\n Trace:\n", other.getMessage(), word);
            other.printStackTrace();
            return false;
        }
    }

    // Tests
    // A completely successful run will print out a success message
    // The origin of the error message will likely come from the 'checkWord'
    // function
    public static void main(String[] args) throws IOException {
        String word;

        for (int i = 0; i < 5; i++) {
            word = RandomWord.generateWord();

            if (word == null) {
                System.err.printf("Test %d returned null\n", i);
                continue;
            }

            if (checkWord(word)) {
                System.out.printf("WordCheck Test %d success: %s is a word\n", i, word);
            } else {
                System.out.printf("WordCheck Test %d success: %s is NOT a word\n", i, word);
            }
        }

        System.out.println("WordCheck tests complete");
    }
}