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
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(calendar.get(Calendar.YEAR), 1900, 2100, 1));
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(calendar.get(Calendar.MONTH) + 1, 1, 12, 1));
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(calendar.get(Calendar.DAY_OF_MONTH), 1, 31, 1));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Select Book:"));
        panel.add(bookComboBox);
        panel.add(new JLabel("Select Borrower:"));
        panel.add(borrowerComboBox);
        panel.add(new JLabel("Return Year:"));
        panel.add(yearSpinner);
        panel.add(new JLabel("Return Month:"));
        panel.add(monthSpinner);
        panel.add(new JLabel("Return Day:"));
        panel.add(daySpinner);

        int option = JOptionPane.showConfirmDialog(null, panel, "Borrow Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            Book selectedBook = (Book) bookComboBox.getSelectedItem();
            Borrower selectedBorrower = (Borrower) borrowerComboBox.getSelectedItem();
            int year = (int) yearSpinner.getValue();
            int month = (int) monthSpinner.getValue() - 1; // Calendar months are 0-based
            int day = (int) daySpinner.getValue();

            // Validate selections
            if (selectedBook == null || selectedBorrower == null) {
                JOptionPane.showMessageDialog(null, "Please select both a book and a borrower.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!(selectedBook.availableCopies > 0)) {
                JOptionPane.showMessageDialog(null, "Selected book is currently not available.", "Unavailable Book", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set due date
            Calendar returnCalendar = Calendar.getInstance();
            returnCalendar.set(year, month-1, day);
            Date dueDate = (Date) returnCalendar.getTime();

            // Attempt to borrow
            BorrowedBook borrowedBook = new BorrowedBook(selectedBook, selectedBorrower, dueDate);

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
            
            if (borrowedBookInsert != null) {
                JOptionPane.showMessageDialog(null, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to borrow the book. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
