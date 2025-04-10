package forms;
import app.Main;
import forms.listeners.AddBookListener;
import forms.listeners.DeleteBookListener;
import forms.tables.BookTableModel;

import javax.swing.*;

public class ManageBooksForm extends JFrame {
    private JLabel manageBooksTitle;
    private JButton addABookButton;
    private JButton updateBookButton;
    private JButton deleteBookButton;
    private JPanel manageBooksFrame;
    private JTable bookTable;

    public ManageBooksForm(){
        var books = Main.libraryService.bookRepository.getAll();

        setTitle("Manage books");
        setSize(500, 300);
        setVisible(true);
        setLocationRelativeTo(null);
        setContentPane(manageBooksFrame);
        bookTable.setModel(new BookTableModel(books));

        addABookButton.addActionListener(new AddBookListener(bookTable));
        deleteBookButton.addActionListener(new DeleteBookListener(bookTable));
    }
}
