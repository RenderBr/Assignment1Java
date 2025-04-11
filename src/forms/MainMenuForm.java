package forms;

import forms.listeners.OpenModelManagerListener;
import models.Book;
import models.BorrowedBook;
import models.Borrower;

import javax.swing.*;

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

        manageBooksButton.addActionListener(new OpenModelManagerListener<>(Book.class));
        manageBorrowersButton.addActionListener(new OpenModelManagerListener<>(Borrower.class));
        returnABookButton.addActionListener(new OpenModelManagerListener<>(BorrowedBook.class));
    }

}
