package forms.listeners;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddBookListener implements ActionListener {
    private final JTable table;

    public AddBookListener(JTable jTable){
        table = jTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextField bookNameField = new JTextField();
        JTextField bookAuthorField = new JTextField();
        JTextField bookCopiesField = new JTextField();

        JPanel panel = new JPanel();
        panel.add(new JLabel("Book Name:"));
        panel.add(bookNameField);

        panel.add(new JLabel("Author:"));
        panel.add(bookAuthorField);

        panel.add(new JLabel("Available Copies:"));
        panel.add(bookCopiesField);
    }
}
