import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

public class test extends JFrame implements ActionListener {
    private JPanel panelMain;

    public static void main(String[] args) throws ParseException {
        JFrame frame = new test();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void createCharBoxRow(int numBoxes) throws ParseException {
        final MaskFormatter charOnlyFormatter = new MaskFormatter("?");
        final GridLayout layout = new GridLayout(1, 5);
        final Dimension panelSize = new Dimension(75, 30);

        JPanel charBoxPanel = new JPanel(layout);
        charBoxPanel.setPreferredSize(panelSize);

        JFormattedTextField newCharBox;
        for (int i = 0; i < numBoxes; i++) {
            newCharBox = new JFormattedTextField(charOnlyFormatter);
            newCharBox.setPreferredSize(new Dimension(25, 25));

            charBoxPanel.add(newCharBox);
            newCharBox.addActionListener(this);
        }
        panelMain.add(charBoxPanel);
    }

    test() throws ParseException {
        super("char box test");

        panelMain = new JPanel();
        panelMain.setPreferredSize(new Dimension(600, 400));

        // Creating char-only input boxes
        for (int i = 0; i < 5; i++) {
            createCharBoxRow(5);
        }

        setContentPane(panelMain);
    }

    public void actionPerformed(ActionEvent event) {
        System.out.println(event);
    }
}