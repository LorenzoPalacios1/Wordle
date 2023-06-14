import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Random;

abstract public class RandomWord {
    /**
     * The minimum amount of characters a returned word can contain.
     */
    final public static int MIN_WORD_LENGTH = 2;
    /**
     * The maximum amount of characters a returned word can contain.
     */
    final public static int MAX_WORD_LENGTH = 15;

    final static String defaultFileName = "generated_words.txt";

    /**
     * Generates a single word of length {@code wordLength} per call.
     * 
     * @param wordLength
     * @return A string of {@code wordLength} length, or {@code null} upon failure.
     */
    final public static String generateWord(final int wordLength) {
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
        } catch (final Exception other) {
            System.err.println("Exception in generateWord()");
            System.err.printf("Exception message: %s\n Passed arguments: %s\n Trace: ", other.getMessage(), wordLength);
            other.printStackTrace();
        }
        // Return null if a word could not be generated
        return null;
    }

    /**
     * Generates a single word per call.
     * 
     * @param wordLength
     * @return A {@code String} of random length (1 < length < 16), or {@code null}
     *         upon failure.
     */
    final public static String generateWord() {
        return generateWord(new Random().nextInt(MIN_WORD_LENGTH, MAX_WORD_LENGTH));
    }

    /**
     * Creates a file and generates a {@code wordAmount} number of words with
     * {@code wordLength} length.
     * Will overwrite the file's data entirely if {@code append} is {@code false}.
     * Default value for {@code append} is {@code true}, meaning this function will
     * NOT overwrite data.
     * 
     * @param file       (default: {@code generated_words.txt})
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     * @param append     (default: {@code true})
     */
    final public static void generateWordsInFile(final File file, final int wordAmount, final int wordLength,
            final boolean append) {
        if (wordAmount > 0 && wordLength > MIN_WORD_LENGTH) {
            try {
                final URLConnection wordSource = new URI(String.format(
                        "https://random-word-api.herokuapp.com/word?number=%d&length=%d", wordAmount, wordLength))
                        .toURL().openConnection();

                // Output file
                final FileWriter output = new FileWriter(file, append);

                // Reading the returned word from 'wordSource' and writing to 'fileName'
                final byte[] rawReturnedData = wordSource.getInputStream().readAllBytes();

                for (int i = 0; i < wordAmount; i++) {

                    // Reads the rest of the returned bytes
                    String buffer = "";

                    /*
                     * 'int j' is responsible for setting the index when reading from
                     * 'rawReturnedData'.
                     * 'int i' tells us which word (0th word, 1st word...) we should be retrieving
                     * from the array, but junk characters such as square brackets ([]),
                     * double quotes (""), and commas (","), are in the way, so we add 3 to
                     * 'wordLength' before multiplying by 'i'
                     * 
                     * For instance, let's say 'rawReturnedData' contains the following bytes turned
                     * characters, including the square brackets, double quotes, and commas:
                     * ["looie","ninny","bocce"]
                     * 
                     * When retrieving the first word, "looie", 'i' will equal 0, because "looie" is
                     * the first word to be retrieved, so 'j' will be initialized with a value of 0,
                     * causing the below for-loop to simply iterate through 'rawReturnedData' until
                     * it gets all the characters it needs, skipping over the bracket and double
                     * quote located at indexes 0 and 1, respectively.
                     * 
                     * However, when retrieving the second word (where 'i' equals 1), "ninny", there
                     * are now THREE characters in the way: a double quote, a comma, and another
                     * double quote. Thus, the initial value of 'j' will then be equal to the
                     * expression i(wordLength + 3), or simply 8, and so on and so forth
                     * 
                     * By adding the length of the words plus the length of these three
                     * miscellaneous characters before multiplying, we can safely extract the
                     * necessary characters from the byte array.
                     */
                    for (int j = i * (wordLength + 3); buffer.length() < wordLength; j++) {
                        if (Character.isAlphabetic(rawReturnedData[j])) {
                            buffer += (char) rawReturnedData[j];
                        }
                    }
                    // Adding the word to the file plus a line separator
                    output.append(buffer + System.lineSeparator());
                    buffer = ""; // Resetting buffer back to an empty String
                }
                output.close();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creates a file and generates a {@code wordAmount} number of words with
     * {@code wordLength} length.
     * Will overwrite the file's data entirely if {@code append} is {@code false}.
     * Default value for {@code append} is {@code true}, meaning this function will
     * NOT overwrite data.
     * 
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     */
    final public static void generateWordsInFile(final int wordAmount, final int wordLength) {
        generateWordsInFile(new File(defaultFileName), wordAmount, wordLength, true);
    }

    /**
     * Creates a file and generates a {@code wordAmount} number of words with
     * {@code wordLength} length.
     * Will overwrite the file's data entirely if {@code append} is {@code false}.
     * Default value for {@code append} is {@code true}, meaning this function will
     * NOT overwrite data.
     * 
     * @param fileName   (default: {@code generated_words.txt})
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     */
    final public static void generateWordsInFile(final String fileName, final int wordAmount, final int wordLength) {
        if (fileName != null && (fileName.matches("[a-zA-Z]+") || fileName.matches("[0-9]+"))) {
            generateWordsInFile(new File(fileName), wordAmount, wordLength, true);
        } else { // If the file name is invalid, use a default
            generateWordsInFile(new File(defaultFileName), wordAmount, wordLength, true);
        }
    }

    /**
     * Creates a file and generates a {@code wordAmount} number of words with
     * {@code wordLength} length.
     * Will overwrite the file's data entirely if {@code append} is {@code false}.
     * Default value for {@code append} is {@code true}, meaning this function will
     * NOT overwrite data.
     * 
     * @param fileName   (default: {@code generated_words.txt})
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     * @param append     (default: {@code true})
     */
    final public static void generateWordsInFile(final String fileName, final int wordAmount, final int wordLength,
            final boolean append) {
        if (fileName != null) {
            generateWordsInFile(new File(fileName), wordAmount, wordLength, append);
        } else { // If the file name is invalid, use a default
            generateWordsInFile(new File(defaultFileName), wordAmount, wordLength, append);
        }
    }

    /**
     * Creates a file and generates a {@code wordAmount} number of words with
     * {@code wordLength} length.
     * Will overwrite the file's data entirely if {@code append} is {@code false}.
     * Default value for {@code append} is {@code true}, meaning this function will
     * NOT overwrite data.
     * 
     * @param file       (default: {@code generated_words.txt})
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     * @param 0
     */
    final public static void generateWordsInFile(final File file, final int wordAmount, final int wordLength) {
        generateWordsInFile(file, wordAmount, wordLength, true);
    }

    /**
     * Generates a {@code String} array of size {@code wordAmount} containing words
     * of {@code wordLength} length.
     * 
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     * @return An array of {@code String}s, or {@code null} upon failure.
     */
    final public static String[] generateWordsInArray(final int wordAmount, final int wordLength) {
        if (wordAmount > 0 && wordLength > MIN_WORD_LENGTH) {
            try {
                final URLConnection wordSource = new URI(String.format(
                        "https://random-word-api.herokuapp.com/word?number=%d&length=%d", wordAmount, wordLength))
                        .toURL().openConnection();
                final byte[] rawReturnedData = wordSource.getInputStream().readAllBytes();

                final String[] wordsArray = new String[wordAmount];
                for (int i = 0; i < wordAmount; i++) {

                    // Reads the rest of the returned bytes
                    // Uses the same methodology as generateWordsInFile() to pull the words from
                    // 'rawReturnedData'
                    String buffer = "";
                    for (int j = i * (wordLength + 3); buffer.length() < wordLength; j++) {
                        if (Character.isAlphabetic(rawReturnedData[j])) {
                            buffer += (char) rawReturnedData[j];
                        }
                    }
                    // Adding the word to the array
                    wordsArray[i] = buffer;
                    buffer = ""; // Resetting buffer back to an empty String
                }
                return wordsArray;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Tests
    // A completely successful run will print out a success message to 'System.out'
    // Otherwise, any relevant data will be printed to 'System.err'
    public static void main(final String[] args) {
        final long startTime = System.nanoTime() / 1000000; // Division by one million to convert to milliseconds
        final long elapsedTimePerTest[] = new long[3]; // We test three different methods, hence a size of 3
        // We store these times because the values are necessary in determining how much
        // time each of the tests took
        // You can see their usage in the printf() statements below following a test
        // case for a function

        System.out.println("Beginning test runs on class RandomWord");

        // Random variables used across all test cases
        final int TEST_CASE_AMOUNT_OF_WORDS = new Random().nextInt(3, 7);
        final int TEST_CASE_LENGTH_OF_WORDS = new Random().nextInt(MIN_WORD_LENGTH, MAX_WORD_LENGTH);

        // Testing generateWord()
        System.out.println("Testing generateWord() method (no args)");
        String word;

        boolean success;
        for (int i = 0; i < TEST_CASE_AMOUNT_OF_WORDS; i++) {
            success = true;
            word = generateWord();

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

        // Calculating how much time the test took
        elapsedTimePerTest[0] = System.nanoTime() / 1000000 - startTime;
        System.out.printf("generateWord() tests complete after %d ms\n\n", elapsedTimePerTest[0]);

        // Testing generateWordsInFile()
        System.out.println("Testing generateWordsInFile() method");
        generateWordsInFile(TEST_CASE_AMOUNT_OF_WORDS, TEST_CASE_LENGTH_OF_WORDS, false);

        // Calculating how much time the test took
        elapsedTimePerTest[1] = System.nanoTime() / 1000000 - (startTime + elapsedTimePerTest[0]);
        System.out.printf("generateWordsInFile() tests complete after %d ms\n\n", elapsedTimePerTest[1]);

        // Testing generateWordsInArray()
        System.out.println("Testing generateWordsInArray() method");
        final String[] words = generateWordsInArray(TEST_CASE_AMOUNT_OF_WORDS, TEST_CASE_LENGTH_OF_WORDS);

        for (int i = 0; i < words.length; i++) {
            System.out.printf("generateWordsInArray() index %d contains String \"%s\"\n", i, words[i]);
        }

        // Calculating how much time the test took
        elapsedTimePerTest[2] = System.nanoTime() / 1000000
                - (startTime + elapsedTimePerTest[0] + elapsedTimePerTest[1]);
        System.out.printf("generateWordsInArray() tests complete after %d ms\n\n", elapsedTimePerTest[2]);

        System.out.printf("RandomWord tests complete after %d ms\n\n", System.nanoTime() / 1000000 - startTime);
    }

    /**
     * Creates a file and generates a {@code wordAmount} number of words with
     * {@code wordLength} length.
     * Will overwrite the file's data entirely if {@code append} is {@code false}.
     * Default value for {@code append} is {@code true}, meaning this function will
     * NOT overwrite data.
     * 
     * @param wordAmount (> 0)
     * @param wordLength (> 1)
     * @param append
     */
    private static void generateWordsInFile(final int wordAmount, final int wordLength, final boolean append) {
        generateWordsInFile(new File(defaultFileName), wordAmount, wordLength, append);
    }
}