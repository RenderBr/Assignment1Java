/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
 */

package forms.listeners;

import app.Main;
import models.Book;
import models.BorrowedBook;
import models.Borrower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.sql.Date;

public class BorrowBookListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // Create dropdowns
        JComboBox<Book> bookComboBox = new JComboBox<>();
        JComboBox<Borrower> borrowerComboBox = new JComboBox<>();

        // Populate dropdowns
        for (Book book : Main.libraryService.bookRepository.getAll()) {
            bookComboBox.addItem(book);
        }

        for (Borrower borrower : Main.libraryService.borrowerRepository.getAll()) {
            borrowerComboBox.addItem(borrower);
        }

        // Date pickers
        Calendar calendar = Calendar.getInstance();
        JSpinner yearSpinner = new JSpinner(new SpinnerDateModel(calendar.getTime(), null, null, Calendar.YEAR));

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));
        panel.add(new JLabel("Select Book:"));
        panel.add(bookComboBox);
        panel.add(new JLabel("Select Borrower:"));
        panel.add(borrowerComboBox);

        panel.add(Box.createHorizontalStrut(10));

        panel.add(new JLabel("Borrowed on:"));
        panel.add(yearSpinner);

        int option = JOptionPane.showConfirmDialog(null, panel, "Borrow Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            Book selectedBook = (Book) bookComboBox.getSelectedItem();
            Borrower selectedBorrower = (Borrower) borrowerComboBox.getSelectedItem();

            // Validate selections
            if (selectedBook == null || selectedBorrower == null) {
                JOptionPane.showMessageDialog(null, "Please select both a book and a borrower.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!(selectedBook.availableCopies > 0)) {
                JOptionPane.showMessageDialog(null, "Selected book is currently not available.", "Unavailable Book", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get the selected date
            Date borrowedDate = new Date(((java.util.Date) yearSpinner.getValue()).getTime());

            // Attempt to borrow
            BorrowedBook borrowedBook = new BorrowedBook(selectedBook, selectedBorrower, borrowedDate);

            // Check if the book is already borrowed
            var existing = Main.libraryService.borrowedBooksRepository.filter(
                    bb -> bb.borrowerId == selectedBorrower.id
                            && bb.bookId == selectedBook.id && bb.return_date == null);


            if (!existing.isEmpty()) {
                JOptionPane.showMessageDialog(null, "This book is already borrowed by the selected borrower.", "Already Borrowed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Save the borrowed book
            BorrowedBook borrowedBookInsert = Main.libraryService.borrowedBooksRepository.insert(borrowedBook);

            selectedBook.availableCopies--;
            Main.libraryService.bookRepository.update(selectedBook);

            if (borrowedBookInsert != null) {
                JOptionPane.showMessageDialog(null, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to borrow the book. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
