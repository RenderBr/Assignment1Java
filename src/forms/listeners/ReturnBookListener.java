/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package forms.listeners;

import app.Main;
import models.Book;
import models.BorrowedBook;
import models.BorrowedBookDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Calendar;
import java.util.Objects;

public class ReturnBookListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // Create dropdowns
        JComboBox<BorrowedBookDisplay> borrowedBookBox = new JComboBox<>();

        // Populate dropdowns
        for (BorrowedBook book : Main.libraryService.borrowedBooksRepository.filter(bb-> bb.return_date == null)) {
            borrowedBookBox.addItem(BorrowedBookDisplay.from(book));
        }

        // Date pickers
        Calendar calendar = Calendar.getInstance();
        JSpinner yearSpinner = new JSpinner(new SpinnerDateModel(calendar.getTime(), null, null, Calendar.YEAR));

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));
        panel.add(new JLabel("Select Book:"));
        panel.add(borrowedBookBox);

        panel.add(Box.createHorizontalStrut(10));

        panel.add(new JLabel("Returned on:"));
        panel.add(yearSpinner);

        int option = JOptionPane.showConfirmDialog(null, panel, "Return Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            var selectedBook = ((BorrowedBookDisplay) Objects.requireNonNull(borrowedBookBox.getSelectedItem())).borrowedBook;

            // Validate selections
            if (selectedBook == null) {
                JOptionPane.showMessageDialog(null, "Please select a book.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update the return date
            Date returnDate = new Date(((java.util.Date) yearSpinner.getValue()).getTime());

            selectedBook.return_date = returnDate;

            // Update the book's available copies
            Book book = Main.libraryService.bookRepository.findById(selectedBook.bookId);

            if (book != null) {
                book.availableCopies++;
                Main.libraryService.bookRepository.update(book);
            }

            // Save the updated borrowed book record
            Main.libraryService.borrowedBooksRepository.update(selectedBook);

            JOptionPane.showMessageDialog(null, "Book returned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
