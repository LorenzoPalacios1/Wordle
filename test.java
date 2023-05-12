import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;

public class test extends JFrame implements ActionListener {
    private JPanel panel;

    private JFormattedTextField text;
    private MaskFormatter charOnly;

    public static void main(String[] args) throws ParseException, IOException {
        JFrame frame = new test();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        System.out.println(RandomWord.generateWord());
    }

    test() throws ParseException {
        super("char box test");

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(600, 400));

        // Creating char-only input boxes
        charOnly = new MaskFormatter("?");
        for (int i = 0; i < 5; i++) {
            text = new JFormattedTextField(charOnly);
            text.setPreferredSize(new Dimension(25, 25));

            panel.add(text);
            text.addActionListener(this);
        }

        setContentPane(panel);
    }

    public void actionPerformed(ActionEvent event) {
        System.out.println(event);
    }
}