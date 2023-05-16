import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;

public class Wordle extends JFrame implements KeyListener {
    private JPanel panelMain;
    private JPanel charBoxes;

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
        // Removing previous active panel's keylisteners if existent
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
        System.out.println(activePanel.getComponent(0).getClass());
        /*
         * for (Component charBox : activePanel.getComponents()) {
         * word += charBox.getText();
         * }
         */
        return word;
    }

    // Driving constructor
    Wordle() throws ParseException {
        super("char box test");

        panelMain = new JPanel();
        panelMain.setPreferredSize(new Dimension(600, 400));

        // Panel to arrange char boxes into a vertical column
        charBoxes = new JPanel(new GridLayout(6, 1));

        // Creating char-only input boxes
        for (int i = 0; i < 6; i++) {
            createCharBoxRow(5);
        }

        // Setting the active panel to the first set of char boxes created
        resetActiveCharPanel();

        panelMain.add(charBoxes);
        activePanel.addKeyListener(this);

        setContentPane(panelMain);
    }

    // Associated key events (only keyReleased used)
    public void keyPressed(KeyEvent key) {
    }

    public void keyReleased(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            String inputtedString = readActiveCharPanel();
            System.out.println(inputtedString);
        }
    }

    public void keyTyped(KeyEvent key) {
    }
}