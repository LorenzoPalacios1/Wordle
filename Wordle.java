import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public class Wordle extends JFrame implements KeyListener, ActionListener {
    // Main JPanels; used to group other instances
    protected JPanel panelMain;
    protected JPanel charBoxes;

    // Self-explanatory text
    protected JLabel title;
    protected JLabel messageLabel;

    // Dark mode and retryButton buttons
    protected JButton retryButton;
    protected JToggleButton darkModeToggle;

    // These variables keep track of guesses and attempts
    protected JPanel activePanel;
    protected int attempt = 0;

    // Used for either dark or light mode; starts off in light
    protected Boolean isDarkMode = false;
    protected Color currentBackgroundScheme = new Color(238, 238, 238);
    protected Color currentTextScheme = new Color(0, 0, 0);

    // For easy access to the active char boxes
    // This is necessary because panel.getComponent() returns a type of 'Component',
    // which is a super class of 'JFormattedTextField' (the charbox) and thus lacks
    // necessary functions and features
    protected JFormattedTextField activePanelCharBoxes[];

    // THE WORDle
    protected static String generatedWord;

    // Banned characters when the player guesses incorrectly
    protected String bannedLetters = "";

    // Initializing function main()
    public static void main(String args[]) throws ParseException, IOException, URISyntaxException {
        JFrame frame = new Wordle();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Destroys the window upon the process dying

        // Word generation
        do {
            generatedWord = RandomWord.generateWord();
        } while (!WordCheck.checkWord(generatedWord));
        // Set visibility last so the player doesn't have the opportunity to screw
        // anything up preemptively
        frame.setVisible(true);
    }

    // Driving constructor
    Wordle() throws ParseException {
        // Naming the window
        super("Wordle");

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
        ImageIcon sunIcon = createImageIcon("sun.png"); // Used as an icon for 'darkModeToggle'
        ImageIcon retryIcon = createImageIcon("reload.png"); // Used as an icon for 'retryButton'

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
        setContentPane(panelMain);
    }

    // Creates an image for use as an icon
    // Returns null if failed
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            return null;
        }
    }

    // Assembles a 'numBoxes' amount of JFormattedTextFields that only accept a
    // single character and puts them into a JPanel 'charBoxPanel' which is then
    // added as a child instance of 'charBoxes'
    protected void createCharBoxRow(int numBoxes) throws ParseException {
        final MaskFormatter charOnlyFormatter = new MaskFormatter("?"); // Allows only single alphabetical characters
        final GridLayout layout = new GridLayout(1, 5); // One row, five columns; used for 'charBoxPanel' generation
        final Dimension panelSize = new Dimension(240, 40); // Size of each char box row

        // Creating a JPanel to hold the generated char boxes
        final JPanel charBoxPanel = new JPanel(layout);
        charBoxPanel.setPreferredSize(panelSize);

        // Creating the char boxes
        JFormattedTextField newCharBox;
        for (int i = 0; i < numBoxes; i++) {
            newCharBox = new JFormattedTextField(charOnlyFormatter);
            newCharBox.setPreferredSize(new Dimension(40, 40));
            newCharBox.setBackground(currentBackgroundScheme);
            newCharBox.setEnabled(false); // resetActiveCharPanel() enables as necessary
            charBoxPanel.add(newCharBox);
        }
        // Adds the row of char boxes to the main JPanel 'charBoxes'
        charBoxes.add(charBoxPanel);
    }

    // Sets an active row (panel) of char boxes
    // If 'activePanel' is NOT initialized, then it assigns it a panel of char boxes
    // If 'activePanel' is initialized, it disables the previous row and removes its
    // KeyListeners while assigning the next row of char boxes
    protected void resetActiveCharPanel() {
        // Removing previous active panel's KeyListeners and disabling them
        if (activePanel != null) {
            for (JFormattedTextField charBox : activePanelCharBoxes) {
                charBox.removeKeyListener(this);
                charBox.setEnabled(false);
            }
            activePanel = null;
        }

        // Player has a maximum of six(6) attempts
        if (attempt < 6) {
            // Setting 'activePanel'
            activePanel = (JPanel) charBoxes.getComponent(attempt);

            // Instantiating 'activePanelCharBoxes'
            activePanelCharBoxes = new JFormattedTextField[activePanel.getComponentCount()]; // Array length should be 5
            for (int i = 0; i < activePanelCharBoxes.length; i++) {
                if (activePanel.getComponent(i) instanceof JFormattedTextField) {
                    activePanelCharBoxes[i] = (JFormattedTextField) activePanel.getComponent(i);
                }
            }

            // Adding new KeyListeners to the newly set active panel
            // Also enabling the new active row for player input
            for (JFormattedTextField charBox : activePanelCharBoxes) {
                charBox.addKeyListener(this);
                charBox.setEnabled(true);
            }
            // Setting the cursor to the first box
            activePanelCharBoxes[0].requestFocusInWindow();
        }
    }

    // Returns a String that consists of the characters the player has inputted into
    // the current 'activePanel'
    protected String readActiveCharPanel() {
        String word = "";

        // Reading chars from 'activePanel' into String 'word'
        for (JFormattedTextField charBox : activePanelCharBoxes) {
            word += charBox.getText();
        }

        return word;
    }

    // Takes a word and acts on the current 'activePanel'
    // This function changes char box colors, increments the player's current
    // 'attempt' as necessary, and adds banned chars to 'bannedLetters'
    protected void interpretInputtedWord(String word) {
        final Color charCorrectPlace = new Color(0, 155, 0); // Green
        final Color charInString = new Color(180, 113, 50); // Yellow
        final Color charNotFound = new Color(155, 0, 0); // Red

        try {
            if (WordCheck.checkWord(word)) {
                // Making sure banned letters aren't repeated and notifying the user as
                // necessary
                // Stops after finding a single banned letter, hence 'index == -1'
                int index = -1;
                for (int i = 0; i < bannedLetters.length() && index == -1; i++) {
                    index = word.indexOf(bannedLetters.charAt(i));
                }

                // Continues if a banned letter wasn't found
                if (index == -1) {
                    String currentChar; // Used to determine if a single character is present in the 'generatedWord'
                    // using String.contains(), which requires a CharSequence (basically a String)
                    // and doesn't accept a char
                    Boolean correctGuess = true; // Will turn to false if a character is incorrect
                    // Used to determine if a guess is a winning one or not

                    // Iterating through the passed word
                    for (int i = 0; i < word.length(); i++) {
                        currentChar = word.substring(i, i + 1); // Assigning the current char to be read as a String if
                                                                // needed

                        if (word.charAt(i) == generatedWord.charAt(i)) {
                            activePanelCharBoxes[i].setBackground(charCorrectPlace);
                        } else if (currentChar != null && generatedWord.contains(currentChar)) {
                            activePanelCharBoxes[word.indexOf(currentChar)].setBackground(charInString);
                            correctGuess = false;
                        } else {
                            activePanelCharBoxes[i].setBackground(charNotFound);
                            bannedLetters += currentChar; // Adding the character to 'bannedLetters'
                            correctGuess = false;
                        }
                    }

                    // We increment 'attempt' here so that when printing the player's current
                    // attempt it's actually correct and not behind by one
                    // (remember, 'attempt' is used as an index for resetCharBoxRow() to identify
                    // the next row of char
                    // boxes out of the JPanel 'charBoxes')
                    attempt++;
                    // If the 'generatedWord' was guessed correctly
                    if (correctGuess) {
                        for (JFormattedTextField charbox : activePanelCharBoxes) {
                            charbox.setEnabled(false);
                        }
                        if (attempt == 1) { // If the player SOMEHOW guessed correctly with their first attempt
                            notifyPlayer(String.format("Wow! You managed to guess \"%s\" in a single attempt!",
                                    generatedWord));
                        } else { // If the player guesses correct after having made more than one attempt
                            notifyPlayer(String.format("Correct! You managed to guess \"%s\" in %d attempts!",
                                    generatedWord, attempt));
                        }
                    } else if (attempt == 6) { // If the player failed on their final guess, notify the player
                        notifyPlayer(String.format("You failed to guess \"%s\".", generatedWord));
                        resetActiveCharPanel();
                    } else { // Otherwise, reset the currently active row of char boxes to the next
                        resetActiveCharPanel();
                    }
                } else { // If the passed string contains letters stored in 'bannedLetters'
                    notifyPlayer("Your guess contains banned letters.");
                }
            } else { // If the passed string is not a valid English word
                notifyPlayer("Input an actual word this time.");
            }
        } catch (IOException io) {
            io.printStackTrace();
        } catch (URISyntaxException uri) {
            uri.printStackTrace();
        }
    }

    // Clears all colors and text from the char boxes set by interpretInputtedWord()
    // Sets 'attempt' to 0, bannedLetters to empty string, and generates a new word
    // Will also clear 'messageLabel'
    // Does NOT regenerate any other parts of the UI or reset other elements' color
    // scheme
    protected void restart() {
        // Prevents player from dragging performance if they haven't made at least one
        // guess since restart() is fairly time-consuming
        if (attempt != 0) {
            try {
                // Resetting modifiable game variables
                attempt = 0;
                bannedLetters = "";
                previousInput = "";

                // Generating another word
                do {
                    generatedWord = RandomWord.generateWord();
                } while (!WordCheck.checkWord(generatedWord));

                // Getting all of the JPanels in 'charBoxes'
                // Using a standard for-loop so that type-casting can take place for
                // 'currentPanel' since if we had used charBoxes.getComponents(), the returned
                // array would be of type 'Component', which is a superclass of JPanel and lacks
                // necessary features
                for (int i = 0; i < charBoxes.getComponentCount(); i++) {
                    JPanel currentPanel = (JPanel) charBoxes.getComponent(i);

                    // Getting and modifying all of the char boxes in 'currentPanel'
                    for (int x = 0; x < currentPanel.getComponentCount(); x++) {
                        JFormattedTextField currentCharBox = (JFormattedTextField) currentPanel.getComponent(x);

                        // We only need to clear boxes that have text within them
                        if (currentCharBox.getText().matches("[a-zA-Z]+")) {
                            currentCharBox.setBackground(currentBackgroundScheme);
                            currentCharBox.setForeground(currentTextScheme);
                            currentCharBox.setText(null);
                            currentCharBox.setEnabled(false); // resetActiveCharPanel() enables as necessary
                        }
                    }
                }

                // Clearing 'messageLabel' and resetting the 'activePanel' to the first row of char boxes
                notifyPlayer(null);
                resetActiveCharPanel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { // If the player has NOT made at least one(1) guess
            notifyPlayer("You haven't made a guess yet, so why restart?");
        }
    }

    // Switches from light to dark mode and vice versa
    protected void colorSchemeChange() {
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
        for (Component item : panelMain.getComponents()) {
            if (item instanceof JLabel) {
                JLabel currentLabel = (JLabel) item;
                currentLabel.setForeground(currentTextScheme);
            }
        }

        // Recoloring all char boxes that aren't already marked or colored by
        // interpretInputtedWord()
        for (int charBoxRow = attempt; charBoxRow < charBoxes.getComponentCount(); charBoxRow++) {
            JPanel currentPanel = (JPanel) charBoxes.getComponent(charBoxRow);

            for (int i = 0; i < currentPanel.getComponentCount(); i++) {
                currentPanel.getComponent(i).setBackground(currentBackgroundScheme);
                currentPanel.getComponent(i).setForeground(currentTextScheme);
            }
        }
    }

    // Notifications for the player
    protected void notifyPlayer(String message) {
        messageLabel.setText(message);
    }

    // Associated key events (only keyReleased() used)
    public void keyPressed(KeyEvent key) {
    }

    // Assembles a string from the char boxes upon releasing the enter key
    // Sends the string to 'interpretInputtedWord' if the string is valid
    // Otherwise, warns the player appropriately
    protected String previousInput = "";

    public void keyReleased(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            final String inputtedString = readActiveCharPanel().toLowerCase().trim();

            // Tracking the previous inputted String lets us cut back on calling
            // checkWord(), especially if the player's entry is gibberish
            if (inputtedString.length() == 5 && !inputtedString.equals(previousInput)) {
                interpretInputtedWord(inputtedString);
            } else if (inputtedString.length() != 5) {
                notifyPlayer("Your guess must contain five letters.");
            } else {
                notifyPlayer("Don't reuse the same guess twice in a row.");
            }
            previousInput = inputtedString;
        }
    }

    public void keyTyped(KeyEvent key) {
    }

    // For button events
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == retryButton) {
            restart();
        }
        if (event.getSource() == darkModeToggle) {
            colorSchemeChange();
        }
    }
}