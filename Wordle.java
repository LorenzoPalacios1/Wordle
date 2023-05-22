import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;

public class Wordle extends JFrame implements KeyListener, ActionListener {
    protected JPanel panelMain;
    protected JPanel charBoxes;

    protected JLabel title;
    protected JLabel messageLabel;

    // Dark mode and retry buttons
    protected JToggleButton retry;
    protected JToggleButton darkModeToggle;

    // These variables keep track of guesses and attempts
    protected JPanel activePanel;
    protected int attempt = 0;

    // For easy access to the char boxes
    // This is necessary because panel.getComponent() returns a type of 'Component',
    // which is a super class of 'JFormattedTextField' (the charbox) and thus lacks
    // necessary functions and features
    protected JFormattedTextField activePanelCharBoxes[];

    // THE WORDle
    protected static String generatedWord;

    // Banned characters when the player guesses incorrectly
    protected String bannedLetters = "";

    // main()
    public static void main(String args[]) throws ParseException, IOException, URISyntaxException {
        JFrame frame = new Wordle();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Word generation
        do {
            generatedWord = RandomWord.generateWord();
            System.out.println(generatedWord);
        } while (!WordCheck.checkWord(generatedWord));
        frame.setVisible(true);
    }

    // Driving constructor
    Wordle() throws ParseException {
        super("Wordle");

        panelMain = new JPanel();
        panelMain.setPreferredSize(new Dimension(600, 400));

        // Title creation
        title = new JLabel();
        title.setPreferredSize(new Dimension(300, 150));
        title.setFont(new Font("Arial", 1, 60));
        title.setText("Wordle");

        messageLabel = new JLabel();
        messageLabel.setPreferredSize(new Dimension(400, 30));
        messageLabel.setFont(new Font("Arial", 2, 16));

        // Panel to arrange char boxes into a vertical column
        charBoxes = new JPanel(new GridLayout(6, 1));

        // Creating six rows of char-only input boxes
        for (int i = 0; i < 6; i++) {
            createCharBoxRow(5);
        }
        // Setting the active panel to the first set of char boxes created
        resetActiveCharPanel();

        // Creating retry and dark mode buttons
        ImageIcon sunIcon = new ImageIcon(java.net.URL );

        retry = new JToggleButton();
        retry.setPreferredSize(new Dimension(50, 50));
        darkModeToggle = new JToggleButton();
        darkModeToggle.setPreferredSize(new Dimension(50, 50));

        panelMain.add(title);
        panelMain.add(charBoxes);
        panelMain.add(messageLabel);
        panelMain.add(retry);
        panelMain.add(darkModeToggle);

        setContentPane(panelMain);
    }

    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // Assembles a 'numBoxes' amount of JFormattedTextFields that only accept a
    // single character and puts them into a JPanel 'charBoxPanel' which is then
    // added as a child instance of 'charBoxes'
    protected void createCharBoxRow(int numBoxes) throws ParseException {
        final MaskFormatter charOnlyFormatter = new MaskFormatter("?");
        final GridLayout layout = new GridLayout(1, 5);
        final Dimension panelSize = new Dimension(240, 40);

        final JPanel charBoxPanel = new JPanel(layout);
        charBoxPanel.setPreferredSize(panelSize);

        JFormattedTextField newCharBox;
        for (int i = 0; i < numBoxes; i++) {
            newCharBox = new JFormattedTextField(charOnlyFormatter);
            newCharBox.setPreferredSize(new Dimension(40, 40));
            newCharBox.setEnabled(false);

            charBoxPanel.add(newCharBox);
        }
        charBoxes.add(charBoxPanel);
    }

    // Sets an active row (panel) of char boxes
    // If 'activePanel' is NOT initialized, then it assigns it a panel of char boxes
    // If 'activePanel' is initialized, it disables the previous row and removes its
    // KeyListeners while assigning the next row of char boxes
    protected void resetActiveCharPanel() {
        // Removing previous active panel's KeyListeners if they exist
        if (activePanel != null) {
            for (Component item : activePanel.getComponents()) {
                item.removeKeyListener(this);
                item.setEnabled(false);
                activePanel = null;
            }
        }

        // Player has a maximum of six(6) attempts
        if (attempt < 6) {
            // Setting 'activePanel'
            activePanel = (JPanel) charBoxes.getComponent(attempt);

            // Instantiating 'activePanelCharBoxes'
            activePanelCharBoxes = new JFormattedTextField[5];
            for (int i = 0; i < activePanelCharBoxes.length; i++) {
                if (activePanel.getComponent(i) instanceof JFormattedTextField) {
                    activePanelCharBoxes[i] = (JFormattedTextField) activePanel.getComponent(i);
                }
            }

            // Adding new KeyListeners to the newly set active panel
            // Also enabling the new active row for player input
            for (JFormattedTextField charBox : activePanelCharBoxes) {
                charBox.addKeyListener(this);
                charBox.addActionListener(this);
                charBox.setEnabled(true);
            }
            activePanelCharBoxes[0].requestFocusInWindow();
        }
    }

    protected String readActiveCharPanel() {
        String word = "";

        // Reading chars from 'activePanel' into String 'word'
        for (int i = 0; i < activePanelCharBoxes.length; i++) {
            if (activePanel.getComponent(i) instanceof JFormattedTextField) {
                activePanelCharBoxes[i] = (JFormattedTextField) activePanel.getComponent(i);
                word += activePanelCharBoxes[i].getText();
            }
        }

        return word;
    }

    // Takes a word and acts on the current 'activePanel'
    // This function changes char box colors, increments the player's current
    // 'attempt' as necessary, and adds banned chars to 'bannedLetters'
    protected void interpretInputtedWord(String word) {
        final Color charNotFound = new Color(155, 0, 0);
        final Color charInString = new Color(180, 113, 50);
        final Color charCorrectPlace = new Color(0, 155, 0);

        try {
            if (WordCheck.checkWord(word)) {
                // Making sure banned letters aren't repeated and notifying the user as
                // necessary
                int index = -1;
                for (int i = 0; i < bannedLetters.length(); i++) {
                    index = word.indexOf(bannedLetters.charAt(i));
                }

                // Continues if a banned letter wasn't found
                if (index == -1) {
                    String currentChar;

                    for (int i = 0; i < word.length(); i++) {
                        currentChar = word.substring(i, i + 1);
                        if (word.charAt(i) == generatedWord.charAt(i)) {
                            activePanelCharBoxes[i].setBackground(charCorrectPlace);
                            // Using 'currentChar' in the below line because contains() requires a
                            // 'CharSequence' to be passed (basically a 'String'), so we can't use charAt()
                        } else if (currentChar != null && generatedWord.contains(currentChar)) {
                            activePanelCharBoxes[word.indexOf(currentChar)].setBackground(charInString);
                        } else {
                            activePanelCharBoxes[i].setBackground(charNotFound);
                            bannedLetters += currentChar;
                        }
                    }
                    attempt++;
                    resetActiveCharPanel();
                } else {
                    notifyPlayer("Your guess contains banned letters.");
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        } catch (URISyntaxException uri) {
            uri.printStackTrace();
        }
    }

    protected void retry() throws MalformedURLException, IOException, URISyntaxException {
        attempt = 0;
        do {
            generatedWord = RandomWord.generateWord();
        } while (!WordCheck.checkWord(generatedWord));
    }

    // Notifications
    protected void notifyPlayer(String message) {
        messageLabel.setText(message);
    }

    // Associated key events (only keyReleased() used)
    public void keyPressed(KeyEvent key) {
    }

    public void keyReleased(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            final String inputtedString = readActiveCharPanel().trim();

            if (inputtedString.length() == 5) {
                interpretInputtedWord(inputtedString);
            } else {
                notifyPlayer("Your guess must contain five letters.");
            }
        }
    }

    public void keyTyped(KeyEvent key) {
    }

    // For button events
    public void actionPerformed(ActionEvent event) {

    }
}