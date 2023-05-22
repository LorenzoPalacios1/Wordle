import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URLConnection;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

public class WordCheck {
    // This function checks if a given string is an actual word
    // Returns true if the passed word exists, otherwise false
    public static boolean checkWord(String word) throws MalformedURLException, IOException, URISyntaxException {
        final URLConnection dictionary = new URI("https://api.dictionaryapi.dev/api/v2/entries/en/" + word).toURL()
                .openConnection();
        dictionary.setReadTimeout(10000);

        // If the word exists, return true
        try {
            dictionary.getInputStream();
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
    public static void main(String[] args) throws IOException, URISyntaxException {
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