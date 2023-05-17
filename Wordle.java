import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;

public class Wordle extends JFrame implements KeyListener {
    private JPanel panelMain;
    private JPanel charBoxes;

    private JTextArea title;

    // These variables keep track of guesses and attempts
    private JPanel activePanel;
    private int attempt = 0;

    // main()
    public static void main(String[] args) throws ParseException, IOException {
        JFrame frame = new Wordle();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Driving constructor
    Wordle() throws ParseException {
        super("Wordle");

        panelMain = new JPanel();
        panelMain.setPreferredSize(new Dimension(600, 400));

        // Title creation
        title = new JTextArea("Wordle");
        title.setPreferredSize(new Dimension(300, 150));

        // Panel to arrange char boxes into a vertical column
        charBoxes = new JPanel(new GridLayout(6, 1));

        // Creating char-only input boxes
        for (int i = 0; i < 6; i++) {
            createCharBoxRow(5);
        }
        // Setting the active panel to the first set of char boxes created
        resetActiveCharPanel();

        panelMain.add(title);
        panelMain.add(charBoxes);
        activePanel.addKeyListener(this);

        setContentPane(panelMain);
    }

    private void createCharBoxRow(int numBoxes) throws ParseException {
        final MaskFormatter charOnlyFormatter = new MaskFormatter("?");
        final GridLayout layout = new GridLayout(1, 5);
        final Dimension panelSize = new Dimension(240, 40);

        JPanel charBoxPanel = new JPanel(layout);
        charBoxPanel.setPreferredSize(panelSize);

        JFormattedTextField newCharBox;
        for (int i = 0; i < numBoxes; i++) {
            newCharBox = new JFormattedTextField(charOnlyFormatter);
            newCharBox.setPreferredSize(new Dimension(40, 40));

            charBoxPanel.add(newCharBox);
        }
        charBoxes.add(charBoxPanel);
    }

    private void resetActiveCharPanel() {
        // Removing previous active panel's keylisteners if they're existent
        if (activePanel != null) {
            for (Component charBox : activePanel.getComponents()) {
                charBox.removeKeyListener(this);
            }
        }

        // Adding new keylisteners to the newly set active panel
        activePanel = (JPanel) charBoxes.getComponent(attempt);
        for (Component charBox : activePanel.getComponents()) {
            charBox.addKeyListener(this);
        }
    }

    private String readActiveCharPanel() {
        String word = "";

        // Instantiating a temporary array that will hold all charboxes within the
        // active panel
        // This is necessary because getComponent() returns a type of Component, which
        // is a super class of JFormattedTextField (the charbox)
        JFormattedTextField temp[] = new JFormattedTextField[activePanel.getComponentCount()];
        for (int i = 0; i < temp.length; i++) {
            if (activePanel.getComponent(i) instanceof JFormattedTextField) {
                temp[i] = (JFormattedTextField) activePanel.getComponent(i);
            }
        }

        // Actual reading
        for (JFormattedTextField charBox : temp) {
            word += charBox.getText();
        }
        return word;
    }

    // Associated key events (only keyReleased used)
    public void keyPressed(KeyEvent key) {
    }

    public void keyReleased(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            String inputtedString = readActiveCharPanel().trim();

            if (inputtedString.length() == 5) {
                System.out.println(inputtedString);
            }
        }
    }

    public void keyTyped(KeyEvent key) {
    }
}