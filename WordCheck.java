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
            byte temp[] = new byte[10 + word.length()];
            response.readNBytes(temp, 10, 10 + word.length());
            for (int i = 0; i < temp.length; i++)
                System.out.println(temp[i]);
        } catch (Exception e) {
            return false;
        }

        return false;
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
                System.out.printf("Test %d success: %s is a word\n", i, words[i]);
            }

        }

        System.out.println("Tests complete");
    }
}