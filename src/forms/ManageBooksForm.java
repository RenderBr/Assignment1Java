package forms;
import app.Main;
import db.annotations.Column;
import forms.tables.BookTableModel;
import models.Book;

import javax.swing.*;
import java.util.ArrayList;

public class ManageBooksForm extends JFrame {
    private JLabel manageBooksTitle;
    private JButton addABookButton;
    private JButton updateBookButton;
    private JButton deleteBookButton;
    private JPanel manageBooksFrame;
    private JTable bookTable;

    public ManageBooksForm(){
        var type = Book.class;
        ArrayList<String> columnNames = new ArrayList<>();

        for(var field : type.getDeclaredFields()){
            var annotations = field.getAnnotations();

            var columnAnnotation = field.getAnnotation(Column.class);

            if(columnAnnotation == null){
                continue;
            }

            columnNames.add(columnAnnotation.name());
        }

        var books = Main.libraryService.bookRepository.getAll();

        if(books.isEmpty()){
            return;
        }

        bookTable = new JTable(new BookTableModel(books));

        setTitle("Manage books");
        setSize(500, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(manageBooksFrame);
        manageBooksFrame.add(bookTable);

    }
}
