import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

abstract public class RandomWord {
    // Generates a single word of 'wordLength' per call
    // Returns a word upon success and null upon failure
    public static String generateWord(int wordLength) {
        try {
            final URLConnection wordSource = new URI(
                    "https://random-word-api.herokuapp.com/word?length=" + wordLength)
                    .toURL()
                    .openConnection();
            wordSource.setConnectTimeout(5000);
            wordSource.setReadTimeout(5000);

            final InputStream word = wordSource.getInputStream();

            // The raw return from 'wordSource' contains a bracket and double quotes at its
            // beginning and end, so we discard the first two bytes below:
            word.read();
            word.read();

            // readAllBytes() is a function in newer Java versions, so it's unused
            // to preserve backwards compatibility
            String formattedWord = "";
            for (int i = 0; i < wordLength; i++) {
                formattedWord += (char) word.read();
            }

            // Returning word
            if (!formattedWord.isBlank()) {
                return formattedWord;
            }
        } catch (Exception other) {
            System.err.println("Exception in generateWord()");
            System.err.printf("Exception message: %s\n Passed arguments: %s\n Trace: ", other.getMessage(), wordLength);
            other.printStackTrace();
        }
        // Return null if a word could not be generated
        return null;
    }

    // Creates a file and generates a 'wordAmount' number of 'wordLength' words
    // Does NOT overwrite file data
    static public void generateWordsInFile(File file, int wordAmount, int wordLength) {
        if (wordAmount > 0 && wordLength > 0) {
            try {
                final URL webURL = new URI("https://random-word-api.herokuapp.com/word?length=" + wordLength)
                        .toURL();

                // Determines whether to use a default filename or not depending on if a valid
                // fileName was supplied
                FileWriter output = new FileWriter(file, true);
                
                URLConnection wordSource;
                InputStream in;

                // Reading the returned word from 'wordSource' and writing to 'fileName'
                for (int i = 0; i < wordAmount; i++) {
                    wordSource = webURL.openConnection();
                    in = wordSource.getInputStream();
                    in.read();
                    in.read();

                    // Reads the returned bytes
                    for (int j = 0; j < wordLength; j++) {
                        output.append((char) in.read());
                    }
                    // Newline to separate the previous word from the next
                    output.append('\n');
                }
                output.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Equivalent to the above generateWordsInFile(), except lacking a 'fileName'
    // String argument
    static public void generateWordsInFile(int wordAmount, int wordLength) {
        generateWordsInFile(new File("generated_words.txt"), wordAmount, wordLength);
    }

    // Equivalent to the above generateWordsInFile(), except taking a file name
    // String argument
    static public void generateWordsInFile(String fileName, int wordAmount, int wordLength) {
        if (fileName != null && (fileName.matches("[a-zA-Z]+") || fileName.matches("[0-9]+"))) {
            generateWordsInFile(new File(fileName), wordAmount, wordLength);
        } else { // If the file name is invalid, use a default
            generateWordsInFile(new File("generated_words.txt"), wordAmount, wordLength);
        }
    }

    // Tests
    // A completely successful run will print out a success message to 'System.out'
    // Otherwise, any relevant data will be printed to 'System.err'
    public static void main(String[] args) {
        System.out.println("Beginning test runs on class RandomWord");

        String word;

        boolean success;
        for (int i = 0; i < 5; i++) {
            success = true;
            word = generateWord(i + 2); // Generates words of linearly varying lengths

            if (word == null) {
                System.err.printf("generateWord() Test %d returned null\n", i);
                continue; // No point in running other checks if 'word' is null
            }

            // Ensuring the returned word contains ONLY alphabetical characters
            if (word.matches("[^a-zA-Z]+")) {
                System.err.printf("generatedWord() Test %d returned non-alphabetical characters\nReturned data: %s\n",
                        i,
                        word);
                success = false;
            }

            if (success)
                System.out.printf("generateWord() Test %d success: %s\n", i, word);
        }

        System.out.println("generateWord() tests complete\n");

        // Now testing generateWordsInFile()
        System.out.println("Testing generateWordsInFile() method");
        generateWordsInFile(3, 3);
        System.out.println("generateWordsInFile() tests complete\n");

        System.out.println("RandomWord tests complete\n");
    }
}