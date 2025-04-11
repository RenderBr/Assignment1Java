package forms;

import forms.listeners.BorrowBookListener;
import forms.listeners.OpenModelManagerListener;
import models.Book;
import models.Borrower;

import javax.swing.*;

public class MainMenuForm extends JFrame {
    private JButton borrowBookButton;
    private JButton manageBorrowersButton;
    private JButton manageBooksButton;
    private JLabel mainMenuTitle;
    private JLabel nameLabel;
    private JPanel mainFrame;
    private JButton returnBookButton;

    public MainMenuForm(){
        setTitle("Library Book Management Service");
        setSize(500, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainFrame);

        manageBooksButton.addActionListener(new OpenModelManagerListener<>(Book.class));
        manageBorrowersButton.addActionListener(new OpenModelManagerListener<>(Borrower.class));

        borrowBookButton.addActionListener(new BorrowBookListener());
    }

}
