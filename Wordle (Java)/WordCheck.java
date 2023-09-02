import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final public class WordCheck {
    final protected static String dictionaryURL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    /**
     * This function checks if a given string is a valid English word.
     * 
     * @param word
     * @return {@code true} if the passed word exists, otherwise {@code false}.
     */
    public static boolean checkWord(final String word) {
        try {
            // Ensuring the given String contains only alphabetical characters
            if (word.length() > 1 && word.matches("[a-zA-Z]+")) {
                final URLConnection dictionary = new URI(dictionaryURL + word).toURL()
                        .openConnection();
                dictionary.setReadTimeout(10000);

                // If the passed String is a true English word, return true
                // 'dictionary' will have no InputStream if the word is invalid
                dictionary.getInputStream();
                return true;
            }
        } catch (final FileNotFoundException e) {
            return false;
        }
        // For anything other than FileNotFoundException
        catch (final Exception other) {
            System.err.printf("Exception message: %s\n Passed data: %s\n Trace: ", other.getMessage(), word);
            other.printStackTrace();
        }
        return false;
    }

    /**
     * Removes any lines in 'file' that are not valid English words.
     * Will iterate through the entire passed 'file' line by line.
     * This function overwrites the contents of 'file' entirely, setting it blank
     * before later writing the filtered version of words separated by lines
     * 
     * @param file
     * @return An array of the removed {@code String}s from 'file'.
     */
    public static String[] removeNonEnglishInFile(final File file) {
        // Used for the return value; later converted to generic array
        final ArrayList<String> removedStrings = new ArrayList<String>();

        try {
            // This marks the input segment of the function
            final Scanner reader = new Scanner(file);

            // Used later to output filtered content back into the 'file'
            final ArrayList<String> buffer = new ArrayList<String>();

            String currentLineContents = "";
            while (reader.hasNextLine()) {
                currentLineContents = reader.nextLine().trim();
                // Checking if the current line's String is a valid English word, and adding it
                // to 'buffer' if so
                if (checkWord(currentLineContents)) {
                    buffer.add(currentLineContents);
                } else { // Otherwise, add the String to 'removedStrings'
                    removedStrings.add(currentLineContents);
                }
            }
            reader.close();

            // This marks the output segment of the function
            final FileWriter filteredOutput = new FileWriter(file, false);
            for (final String word : buffer) {
                filteredOutput.write(word + '\n'); // Appending a new line to each word entry
            }
            filteredOutput.close();

        } catch (final FileNotFoundException invalidFileName) { // In case an invalid file was given
            System.err.printf("%s\nPassed file was not found\nThis is likely due to an improper file name",
                    invalidFileName.getMessage());
        } catch (final Exception other) { // Catches anything else; if this runs, something catastrophic probably
                                          // happened
            other.printStackTrace();
        }
        // Converting 'removedStrings' to a generic String array type for the return
        return removedStrings.toArray(new String[removedStrings.size()]);
    }

    public static String[] removeNonEnglishInArray(final String[] words) {
        // Container for the valid Strings
        final ArrayList<String> validWords = new ArrayList<String>();

        // Reading the Strings within 'words'
        for (final String word : words) {
            // If the String is a valid English word, add it to 'validWords'
            if (checkWord(word)) {
                validWords.add(word);
            }
        }
        // Converting 'validWords' to a generic String array type for the return
        return validWords.toArray(new String[validWords.size()]);
    }

    /**
     * Removes any lines in 'file' that are not valid English words.
     * Will iterate through the entire passed 'file' line by line.
     * This function overwrites the contents of 'file' entirely, setting it blank
     * before later writing the filtered version of words separated by lines
     * 
     * @param fileName
     * @return An array of the removed {@code String}s from 'file'.
     */
    public static String[] removeNonEnglishInFile(final String fileName) {
        final File file = new File(fileName); // Opening the file referred to by 'fileName'

        return removeNonEnglishInFile(file);
    }

    // Tests
    // A completely successful run will print out a success message
    // The origin of the error message will likely come from the 'checkWord'
    // function
    public static void main(final String[] args) {
        final long startTime = System.nanoTime() / 1000000; // Division by one million to convert to milliseconds

        /*
         * We test three different methods, hence an array size of 3.
         * We store these times because the values are necessary in determining how much
         * time each of the tests took.
         * You can see their usage in the printf() statements below, usually following a
         * test case for a function.
         */
        final long elapsedTimePerTest[] = new long[3];

        System.out.println("Beginning test runs on class WordCheck");

        System.out.println("Testing checkWord() method");

        for (int i = 0; i < 5; i++) {
            final String word = RandomWord.generateWord();

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

        // Calculating how much time the test took
        elapsedTimePerTest[0] = System.nanoTime() / 1000000 - startTime;
        System.out.printf("checkWord() tests complete after %d ms\n\n", elapsedTimePerTest[0]);

        final int TEST_CASE_AMOUNT_OF_WORDS = new Random().nextInt(3, 6);
        final int TEST_CASE_LENGTH_OF_WORDS = new Random().nextInt(RandomWord.MIN_WORD_LENGTH,
                RandomWord.MAX_WORD_LENGTH);

        // Now testing removeNonEnglishInFile()
        System.out.println("Testing removeNonEnglishInFile() method");

        RandomWord.generateWordsInFile(TEST_CASE_AMOUNT_OF_WORDS, TEST_CASE_LENGTH_OF_WORDS);
        System.out.printf("removeNonEnglishInFile() removed %d String(s) from the passed test file\n",
                removeNonEnglishInFile("generated_words.txt").length);

        // Calculating how much time the test took
        elapsedTimePerTest[1] = System.nanoTime() / 1000000 - (startTime + elapsedTimePerTest[0]);
        System.out.printf("removeNonEnglishInFile() tests complete after %d ms\n\n", elapsedTimePerTest[1]);

        // Now testing removeNonEnglishInArray()
        System.out.println("Testing removeNonEnglishInArray() method");

        // ExecutorService used for 'Future' usage to help prevent null array returns
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        // Retrieves an array of random words
        final Future<String[]> randomWordsArr = executor.submit(() -> {
            return RandomWord.generateWordsInArray(TEST_CASE_AMOUNT_OF_WORDS, TEST_CASE_LENGTH_OF_WORDS);
        });

        try {
            // Retrieving the test input array
            final String[] stringArrayIn = randomWordsArr.get();

            // Calling removeNonEnglishInArray() while printing the contents of
            // 'stringArrayIn' so that the output array will be ready as soon as possible
            final Future<String[]> filteredWordsArray = executor.submit(() -> {
                return removeNonEnglishInArray(stringArrayIn);
            });

            // Printing the contents of the test input array
            System.out.println("Contents of test input array:");
            for (final String word : stringArrayIn) {
                System.out.print(word + " ");
            }

            // Getting the returned value(s) from removeNonEnglishInArray() and printing the returned contents
            System.out.println("\nContents of test output array:");
            final String[] stringArrayOut = filteredWordsArray.get();
            for (final String word : stringArrayOut) {
                System.out.print(word + " ");
            }

            // Printing how many Strings were removed from 'stringArrayIn' by
            // removeNonEnglishInArray()
            System.out.printf("\ngenerateWordsInArray() removed %d String(s) from the test input array\n",
                    stringArrayIn.length - stringArrayOut.length);
        } catch (final InterruptedException interrupt) {
            System.err.println("generateWordsInArray() interrupted; skipping removeNonEnglishInArray() test case\n Trace:\n");
            interrupt.printStackTrace();
        } catch (final NullPointerException nullFound) {
            System.err.println("Could not fetch input or output\n Trace:\n");
            nullFound.printStackTrace();
        } catch (final Exception other) { // For everything else
            other.printStackTrace();
        }
        executor.close();

        // Calculating how much time the test took
        elapsedTimePerTest[2] = System.nanoTime() / 1000000
                - (startTime + elapsedTimePerTest[0] + elapsedTimePerTest[1]);
        System.out.printf("removeNonEnglishInArray() tests complete after %d ms\n\n", elapsedTimePerTest[2]);

        System.out.printf("\nAll WordCheck tests complete after %d ms\n\n", System.nanoTime() / 1000000 - startTime);
    }
}