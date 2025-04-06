package forms;

import forms.listeners.ManageBooksListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuForm extends JFrame {
    private JButton returnABookButton;
    private JButton manageBorrowersButton;
    private JButton manageBooksButton;
    private JLabel mainMenuTitle;
    private JLabel nameLabel;
    private JPanel mainFrame;

    public MainMenuForm(){
        setTitle("Library Book Management Service");
        setSize(500, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainFrame);

        manageBooksButton.addActionListener(new ManageBooksListener());
    }

}
