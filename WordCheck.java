import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

abstract public class WordCheck {
    final protected static String dictionaryURL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    // This function checks if a given string is an actual word
    // Returns true if the passed word exists, otherwise false
    public static boolean checkWord(String word) {
        try {
            // Ensuring the given String contains only alphabetical characters
            if (word.matches("[a-zA-Z]+")) {
                final URLConnection dictionary = new URI(dictionaryURL + word).toURL()
                        .openConnection();
                dictionary.setReadTimeout(10000);

                // If the passed String is a true English word, return true
                // 'dictionary' will have no InputStream if the word is invalid
                dictionary.getInputStream();
                return true;
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        // For anything other than FileNotFoundException
        catch (Exception other) {
            System.err.printf("Exception message: %s\n Passed data: %s\n Trace: ", other.getMessage(), word);
            other.printStackTrace();
            return false;
        }
        return false;
    }

    // Removes any lines in 'file' that are not valid English words
    // Will iterate through the entire passed 'file' line by line
    // This function overwrites the contents of 'file' entirely, setting it blank
    // before later writing the filtered version of words separated by lines
    // Will an array of the removed Strings
    public static String[] removeNonEnglishInFile(File file) {
        // Used for the return value; later converted to generic array
        ArrayList<String> removedStrings = new ArrayList<String>();

        try {
            // This marks the input segment of the function
            Scanner reader = new Scanner(file);

            // Used later to output filtered content back into the 'file'
            ArrayList<String> buffer = new ArrayList<String>();

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
            FileWriter filteredOutput = new FileWriter(file, false);
            for (String word : buffer) {
                filteredOutput.write(word + '\n'); // Appending a new line to each word entry
            }
            filteredOutput.close();

        } catch (FileNotFoundException invalidFileName) { // In case an invalid file was given
            System.err.printf("%s\nPassed file was not found\nThis is likely due to an improper file name",
                    invalidFileName.getMessage());
        } catch (Exception other) { // Catches anything else; if this runs, something catastrophic probably happened
            other.printStackTrace();
        }
        // Converting to a generic array type for the return
        String[] returnedArray = new String[removedStrings.size()];
        removedStrings.toArray(returnedArray);
        return returnedArray;
    }

    // Functionally identical to the above removeNonEnglishInFile(), except taking a
    // 'fileName' String parameter instead of a direct File
    public static String[] removeNonEnglishInFile(String fileName) {
        File file = new File(fileName); // Opening the file referred to by 'fileName'

        return removeNonEnglishInFile(file);
    }

    // Tests
    // A completely successful run will print out a success message
    // The origin of the error message will likely come from the 'checkWord'
    // function
    public static void main(String[] args) {
        System.out.println("Beginning test runs on class WordCheck");

        System.out.println("Testing checkWord() method");

        String word;
        for (int i = 0; i < 5; i++) {
            word = RandomWord.generateWord(5);

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
        System.out.println("checkWord() tests complete\n");

        // Now testing removeNonEnglishInFile()
        System.out.println("Testing removeNonEnglishInFile() method");

        RandomWord.generateWordsInFile(3, 6);
        System.out.printf("removeNonEnglishInFile() removed %d Strings from the passed test file\n",
                removeNonEnglishInFile("generated_words.txt").length);
        System.out.println("removeNonEnglishInFile() tests complete\n");

        System.out.println("\nAll WordCheck tests complete\n");
    }
}