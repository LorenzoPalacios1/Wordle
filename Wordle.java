import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;

final public class Wordle implements KeyListener, ActionListener {
    // Init function
    final public static void main(final String[] args) throws ParseException {
        final caching cacheProcess = new caching();
        cacheProcess.start();

        final Wordle game = new Wordle();

        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Destroys the window upon the process dying

        game.generatedWord = generateWordle();

        // Set visibility last so the player doesn't have the opportunity to screw
        // anything up preemptively
        game.frame.setVisible(true);
    }

    /**
     * Will return a five letter word to be used as the Wordle.
     * Will use a cache file if it exists, otherwise will use the
     * {@code generateWord()} function from class {@code RandomWord}, which takes
     * time since it is dependant on a HTTP request.
     * 
     * @return A {@code String} containing a valid five-letter English word
     */
    final protected static String generateWordle() {
        String word = "";

        final File cache = new File("cache");
        try (Scanner cacheReader = new Scanner(cache)) {
            // Used to store the necessary line within the 'cache'
            String[] buffer;
            // 'cache.length() > 60' exists so we know there are at least ten lines of
            // possibly valid data
            if (cache.exists() && cache.length() > 60) {
                // '5 + lineSeparator()' since each line in the cache should
                // consist of five characters and the system's default line separator(s)
                // This is done to calculate the number of words in the cache
                // Final division by two to reduce the total amount of necessary reading by half
                buffer = new String[(int) cache.length() / (5 + System.lineSeparator().length()) / 2];
            } else {
                throw new FileNotFoundException();
            }
            // Determines which half of the file to scan through
            // A value of 'true' denotes that the first half should be used, so we continue
            // past this segment as normal
            // A value of 'false' denotes that the second half should be used, so we discard
            // the first half of entries in 'cache' in the below loop
            if (!new Random().nextBoolean()) {
                for (int i = 0; i < buffer.length; i++) {
                    cacheReader.nextLine();
                }
            }

            // Reading data from 'cache' into 'buffer'
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = cacheReader.nextLine();
            }

            // Accessing a random index of 'buffer' to return as the generated Wordle
            // Wrapped in a do-while to ensure the word is a valid English word with five
            // total letters
            do {
                word = buffer[new Random().nextInt(buffer.length)];
            } while (word.length() != 5 || !WordCheck.checkWord(word));
        } catch (final FileNotFoundException e) { // If the cache does not exist, use the slower generateWord()
            do {
                word = RandomWord.generateWord(5);
            } while (!WordCheck.checkWord(word));
        }
        System.out.println(word);
        return word;
    }

    /** THE WORDle */
    protected String generatedWord;

    /** Used to group the entire GUI. */
    final protected JPanel panelMain;

    /** Used to group rows of char boxes into a grid. */
    final protected JPanel charBoxes;

    /** Contains the game's title, "Wordle". */
    final protected JLabel title;
    /** A generic {@code JLabel} used to notify the player. */
    final protected JLabel messageLabel;

    /** Retry button. */
    final protected JButton retryButton;
    /** Background theme toggle button. */
    final protected JToggleButton darkModeToggle;

    /**
     * Raw JPanel that keeps track of the currently active row of char boxes that
     * the player has.
     */
    protected JPanel activePanelRow;
    /**
     * Tracks the player's current attempt; is otherwise used as an index for
     * functions.
     */
    protected int attempt = 0;

    /**
     * This value keeps track of whether the background is in darkmode or light mode
     */
    protected Boolean isDarkMode = false;
    /** The current color scheme of the game's background. */
    protected Color currentBackgroundScheme = new Color(238, 238, 238);
    /** The current color scheme of the game's text. */
    protected Color currentTextScheme = new Color(0, 0, 0); // Black text

    /**
     * Used for easy access to the active char boxes.
     * This is necessary because {@code panel.getComponent()} returns a type of
     * {@code Component}, which is a super class of {@code JTextField}
     * (the charbox) and thus lacks necessary functions and features.
     */
    protected JTextField[] activeCharBoxRow;

    /** A list of banned characters for when the player guesses incorrectly. */
    protected String bannedLetters = "";

    /**
     * Used to prevent the reusage of a given String twice in a row, thus preventing
     * unnecessary function calls.
     */
    protected String previousInput = "";

    /**
     * A {@code Color} used to notify the player that a char box contains a correct
     * letter in the correct position.
     */
    final Color charCorrectPlace = new Color(0, 155, 0); // Green
    /**
     * A {@code Color} used to notify the player that a char box contains a correct
     * letter, but in an incorrect position.
     */
    final Color charInString = new Color(180, 113, 50); // Yellow
    /**
     * A {@code Color} used to notify the player that a char box contains an
     * incorrect letter.
     */
    final Color charNotFound = new Color(155, 0, 0); // Red

    /**
     * Formatter used in the generation of {@code JTextField}s that forces
     * them to only accept a single alphabetical character.
     */
    final MaskFormatter charOnlyFormatter = new MaskFormatter("U");
    /** Layout used in every char box row. */
    final GridLayout layout = new GridLayout(1, 5);
    /** The size of each char box row */
    final Dimension panelSize = new Dimension(240, 40); // Size of each char box row

    /**
     * Used in the generation of char boxes to make them only take in one
     * alphabetical character at a time.
     */
    final charOnlyFilter filter = new charOnlyFilter();
    /**
     * Describes the default size for generated char boxes in
     * {@code createCharBoxRow()}.
     */
    final Dimension charBoxSize = new Dimension(40, 40);

    /**
     * Describes the default {@code Font} for generated char boxes in
     * {@code createCharBoxRow()}.
     */
    final Font charBoxFont = new Font("Arial", 1, 24);

    /** The primary window for the game. */
    protected JFrame frame;

    /** Driving constructor. */
    protected Wordle() throws ParseException {
        // Creating and naming the window
        frame = new JFrame("Wordle");

        // Creating the main panel for the UI
        panelMain = new JPanel();
        panelMain.setPreferredSize(new Dimension(600, 400));

        // Title creation
        title = new JLabel();
        title.setPreferredSize(new Dimension(300, 150));
        title.setFont(new Font("Arial", 1, 60));
        title.setText("Wordle");

        // Player notifier
        messageLabel = new JLabel();
        messageLabel.setPreferredSize(new Dimension(450, 30));
        messageLabel.setFont(new Font("Arial", 2, 16));

        // Panel to arrange char boxes into a vertical column
        charBoxes = new JPanel(new GridLayout(6, 1));

        // Creating six rows of char-only input boxes
        for (int i = 0; i < 6; i++) {
            createCharBoxRow(5);
        }
        // Setting the active panel to the first set of char boxes created
        resetActiveCharPanel();

        // Creating retryButton and dark mode buttons
        final ImageIcon sunIcon = createImageIcon("sun.png"); // Used as an icon for 'darkModeToggle'
        final ImageIcon retryIcon = createImageIcon("reload.png"); // Used as an icon for 'retryButton'

        retryButton = new JButton(retryIcon);
        retryButton.setToolTipText("Retry");
        retryButton.setPreferredSize(new Dimension(50, 50));
        retryButton.addActionListener(this);

        darkModeToggle = new JToggleButton(sunIcon);
        darkModeToggle.setToolTipText("Toggle Dark Mode");
        darkModeToggle.setPreferredSize(new Dimension(50, 50));
        darkModeToggle.addActionListener(this);

        // Throwing everything into the main panel
        panelMain.add(title);
        panelMain.add(charBoxes);
        panelMain.add(messageLabel);
        panelMain.add(retryButton);
        panelMain.add(darkModeToggle);

        // Finalizing
        frame.setContentPane(panelMain);
    }

    // Associated key events below; linked only to the char boxes and nothing else
    // as of now
    @Override
    final public void keyPressed(final KeyEvent key) {
    }

    /**
     * Assembles a string from the char boxes upon releasing the enter key
     * Sends the string to 'interpretGuess' if the string is valid
     * Otherwise, warns the player appropriately
     */
    @Override
    final public void keyReleased(final KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            final String inputtedString = readActiveCharPanel().toLowerCase().trim();

            // Tracking the previous inputted String lets us cut back on calling
            // checkWord(), especially if the player's entry is gibberish
            if (inputtedString.length() == 5 && !inputtedString.equals(previousInput)) {
                interpretGuess(inputtedString);
            } else if (inputtedString.length() != 5) {
                notifyPlayer("Your guess must contain five letters.");
            } else {
                notifyPlayer("Don't reuse the same guess twice in a row.");
            }
            previousInput = inputtedString;
        }
    }

    /**
     * Allows the player to conveniently replace the character within a given char
     * box without having to manually delete it.
     */
    @Override
    final public void keyTyped(final KeyEvent key) {
        if (Character.isAlphabetic(key.getKeyChar())) {
            final JTextField charBox = (JTextField) key.getSource();
            // We set the char box's text to null because if we try to directly assign the
            // associated character concatenated to an empty String, the DocumentFilter
            // attached to the char box will scream and holler (silently) and prevent the
            // text from being set.

            // This only works because this function is called before the char box's
            // 'Document' can update its text, which means that for a very brief moment, the
            // char box will have no text, thus allowing for the player's input to be
            // written.
            charBox.setText(null);
        }
    }

    /**
     * Used for button events - specifically with the retry button and the dark mode
     * toggle button.
     */
    @Override
    final public void actionPerformed(final ActionEvent event) {
        if (event.getSource() == retryButton) {
            restart();
        }
        if (event.getSource() == darkModeToggle) {
            colorSchemeChange();
        }
    }

    /**
     * Creates an image for use as an icon.
     * 
     * @return An {@code ImageIcon} on success, otherwise {@code null}
     */
    final protected ImageIcon createImageIcon(final String path) {
        final java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            return null;
        }
    }

    /**
     * Assembles a {@code numBoxes} amount of {@code JTextField}s that only
     * accept a single character and puts them into a {@code JPanel}
     * {@code charBoxPanel} which is then added as a child instance of
     * {@code charBoxes}.
     */
    final protected void createCharBoxRow(final int numBoxes) throws ParseException {
        // Creating a JPanel to hold the generated char boxes
        final JPanel charBoxPanel = new JPanel(layout);
        charBoxPanel.setPreferredSize(panelSize);

        // Creating the char boxes
        JTextField newCharBox;
        for (int i = 0; i < numBoxes; i++) {
            newCharBox = new JTextField();
            newCharBox.setFont(charBoxFont);
            newCharBox.setPreferredSize(charBoxSize);
            newCharBox.setBackground(currentBackgroundScheme);
            newCharBox.setHorizontalAlignment(JTextField.CENTER);
            // Applying the custom DocumentFilter to the char box
            ((AbstractDocument) newCharBox.getDocument()).setDocumentFilter(filter);
            newCharBox.setEnabled(false); // resetActiveCharPanel() enables boxes as necessary
            // Adding the new char box to the row
            charBoxPanel.add(newCharBox);
        }
        // Adds the row of char boxes to the main JPanel 'charBoxes'
        charBoxes.add(charBoxPanel);
    }

    /**
     * Sets an active row (panel) of char boxes.
     * If 'activePanelRow' is NOT initialized, then it assigns it a panel of char
     * boxes.
     * If 'activePanelRow' is initialized, it disables the previous row and
     * removes its KeyListeners while assigning the next row of char boxes.
     */
    final protected void resetActiveCharPanel() {

        // Removing previous active panel's KeyListeners and disabling them
        if (activePanelRow != null) {
            for (final JTextField charBox : activeCharBoxRow) {
                charBox.removeKeyListener(this);
                charBox.setEnabled(false);
            }
            activePanelRow = null;
        }

        // Player has a maximum of six(6) attempts
        if (attempt < 6) {
            // Setting 'activePanelRow'
            activePanelRow = (JPanel) charBoxes.getComponent(attempt);

            // Instantiating 'activeCharBoxRow', adding KeyListeners, and enabling the char
            // boxes
            activeCharBoxRow = new JTextField[activePanelRow.getComponentCount()]; // Array length should be 5
            for (int i = 0; i < activeCharBoxRow.length; i++) {
                if (activePanelRow.getComponent(i) instanceof JTextField) {
                    activeCharBoxRow[i] = (JTextField) activePanelRow.getComponent(i);
                    activeCharBoxRow[i].addKeyListener(this);
                    activeCharBoxRow[i].setEnabled(true);
                }
            }
            activeCharBoxRow[0].requestFocusInWindow(); // Setting the cursor to the first box
        }
    }

    /**
     * Reads and returns all of the char boxes' text in {@code activeCharBoxRow}.
     * 
     * @return A String that consists of the characters the player has inputted into
     *         the current 'activePanelRow'
     */
    final protected String readActiveCharPanel() {
        String word = "";

        // Reading chars from 'activePanelRow' into String 'word'
        for (final JTextField charBox : activeCharBoxRow) {
            word += charBox.getText();
        }

        return word;
    }

    /**
     * Takes a word and interprets it.
     * This function calls either {@code win()}, or {@code incorrectGuess()}, and
     * adds wrong characters to {@code bannedLetters} in the case of the latter.
     */
    final protected void interpretGuess(final String guess) {
        // We check if the guess is correct upfront just in case in order to avoid
        // calling checkWord()
        if (guess.equals(generatedWord)) {
            // We increment 'attempt' here so that when printing the player's current
            // attempt isn't off by one, and because they still had to attempt, right?
            attempt++;
            win();
        } else if (WordCheck.checkWord(guess)) {
            // Making sure banned letters aren't repeated and notifying the user as
            // necessary
            // Stops after finding a single banned letter, hence 'index == -1'
            int index = -1;
            for (int i = 0; i < bannedLetters.length() && index == -1; i++) {
                index = guess.indexOf(bannedLetters.charAt(i));
            }

            // Continues if a banned letter wasn't found
            if (index == -1) {
                // We increment 'attempt' here so that when printing the player's current
                // attempt it's actually correct and not off by one, and because we don't count
                // guesses that contain banned or already used letters
                attempt++;
                incorrectGuess(guess);
            } else { // If the passed string contains letters stored in 'bannedLetters'
                notifyPlayer("Your guess contains banned letters.");
            }
        } else { // If the passed string is not a valid English word
            notifyPlayer("Input an actual word this time.");
        }
    }

    /**
     * If the guessed word is NOT correct.
     * Should only be called by {@code interpretGuess()}.
     * 
     * @param guess
     */
    final protected void incorrectGuess(final String guess) {
        for (int i = 0; i < guess.length(); i++) {
            // Preemptively assigning the current char as a String if needed
            final String currentChar = "" + guess.charAt(i);

            if (guess.charAt(i) == generatedWord.charAt(i)) {
                activeCharBoxRow[i].setBackground(charCorrectPlace);
            } else if (generatedWord.contains(currentChar)) {
                activeCharBoxRow[guess.indexOf(currentChar)].setBackground(charInString);
            } else {
                activeCharBoxRow[i].setBackground(charNotFound);
                bannedLetters += currentChar; // Adding the character to 'bannedLetters'
            }
        }
        // Enabling the next row of char boxes for the player and disabling the previous
        if (attempt == 6) { // If this was the player's last guess, notify them
            notifyPlayer(String.format("You failed to guess \"%s\".", generatedWord));
            resetActiveCharPanel();
        } else if (attempt != 6) { // Otherwise, just enable the next row of char boxes
            resetActiveCharPanel();
        }
    }

    final protected void win() {
        for (final JTextField charbox : activeCharBoxRow) {
            charbox.setEnabled(false);
            charbox.setBackground(charCorrectPlace);
        }

        // Notifying the player of their correct guess
        if (attempt == 1) { // If the player SOMEHOW guessed correctly with their first attempt
            notifyPlayer(String.format("Wow! You managed to guess \"%s\" in a single attempt!",
                    generatedWord));
        } else { // If the player guesses correct after having made more than one attempt
            notifyPlayer(String.format("Correct! You managed to guess \"%s\" in %d attempts!",
                    generatedWord, attempt));
        }
    }

    /**
     * Clears all colors and text from the char boxes set by
     * {@code interpretGuess()}.
     * Will also clear {@code messageLabel}.
     * Does NOT regenerate any other parts of the UI or reset other any elements'
     * color scheme
     */
    final protected void restart() {
        // Prevents player from dragging performance if they haven't made at least one
        // guess since calling restart() can be fairly time-consuming
        if (attempt != 0) {
            // Resetting modifiable game variables
            attempt = 0;
            bannedLetters = "";
            previousInput = "";

            // Generating another word
            generatedWord = generateWordle();

            // Getting all of the JPanels in 'charBoxes'
            // Using a standard for-loop so that type-casting can take place for
            // 'currentPanel' since if we had used charBoxes.getComponents(), the returned
            // array would be of type 'Component', which is a superclass of JPanel and lacks
            // necessary features
            for (int i = 0; i < charBoxes.getComponentCount(); i++) {
                final JPanel currentPanel = (JPanel) charBoxes.getComponent(i);

                // Getting and modifying all of the char boxes in 'currentPanel'
                for (int x = 0; x < currentPanel.getComponentCount(); x++) {
                    final JTextField currentCharBox = (JTextField) currentPanel.getComponent(x);

                    // We only need to clear boxes that have text within them
                    if (!currentCharBox.getText().isBlank()) {
                        currentCharBox.setBackground(currentBackgroundScheme);
                        currentCharBox.setForeground(currentTextScheme);
                        currentCharBox.setText(null);
                        currentCharBox.setEnabled(false); // resetActiveCharPanel() enables as necessary
                    }
                }
            }
            // Clearing 'messageLabel' and resetting the 'activePanelRow' to the first row
            // of char boxes
            notifyPlayer(null);
            resetActiveCharPanel();
        } else { // If the player has NOT made at least one(1) guess
            notifyPlayer("You haven't made a guess yet, so why restart?");
        }
    }

    /**
     * Switches from light theme to dark theme and vice versa.
     */
    final protected void colorSchemeChange() {
        if (isDarkMode) { // If dark mode, switch to light
            currentBackgroundScheme = new Color(238, 238, 238);
            currentTextScheme = new Color(0, 0, 0);
        } else { // If light mode, switch to dark
            currentBackgroundScheme = new Color(21, 21, 21);
            currentTextScheme = new Color(255, 255, 255);
        }
        isDarkMode = !isDarkMode; // Switch from true to false and vice versa

        // Setting the any miscellaneous text to the new color scheme
        panelMain.setBackground(currentBackgroundScheme);
        for (final Component item : panelMain.getComponents()) {
            if (item instanceof JLabel) {
                final JLabel currentLabel = (JLabel) item;
                currentLabel.setForeground(currentTextScheme);
            }
        }

        // Recoloring all char boxes that aren't already marked or colored by
        // interpretGuess()
        for (int charBoxRow = attempt; charBoxRow < charBoxes.getComponentCount(); charBoxRow++) {
            final JPanel currentPanel = (JPanel) charBoxes.getComponent(charBoxRow);

            for (int i = 0; i < currentPanel.getComponentCount(); i++) {
                currentPanel.getComponent(i).setBackground(currentBackgroundScheme);
                currentPanel.getComponent(i).setForeground(currentTextScheme);
            }
        }
    }

    /**
     * Notifies the player via the JTextLabel 'messageLabel'.
     * 
     * @param message
     */
    final protected void notifyPlayer(final String message) {
        messageLabel.setText(message);
    }
}

/**
 * Used to develop a cache of potential Wordles for quick access. This allows
 * the game to skip over some particularly long HTTP requests from
 * {@code RandomWord}
 */
final class caching extends Thread {
    public void run() {
        final File cache = new File("cache");

        // Adds onto the existing cache
        // Will continue until thread death or the file containing more than 250 lines
        // (words)
        // Each line in the cache should contain a five-letter word and line separator
        // character(s), hence the addition seen below
        while (this.isAlive() && (int) cache.length() / (5 + System.lineSeparator().length()) < 250) {
            RandomWord.generateWordsInFile(cache, 25, 5);
        }
    }
}

/**
 * Custom {@code DocumentFilter} used to filter the input into charboxes to
 * ensure there can only be one alphabetical character at a time.
 */
final class charOnlyFilter extends DocumentFilter {
    @Override
    public void replace(final FilterBypass fb, final int offset, final int length, final String text,
            final AttributeSet attrs)
            throws BadLocationException {
        // Check to ensure text does not go past one character and that that one
        // character is alphabetical
        if (text != null && fb.getDocument().getLength() + text.length() <= 1 && text.matches("[a-zA-Z]+")) {
            // Forcing uppercase for aesthetics
            super.replace(fb, offset, length, text.toUpperCase(), attrs);
        } else if (text == null) { // Allows for the clearing of char boxes
            super.replace(fb, offset, length, null, attrs);
        }
    }
}