/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package models;

import app.Main;

public class BorrowedBookDisplay {
    public BorrowedBook borrowedBook;

    private BorrowedBookDisplay(BorrowedBook borrowedBook) {
        this.borrowedBook = borrowedBook;
    }

    @Override
    public String toString() {
        var book = Main.libraryService.bookRepository.findById(borrowedBook.bookId);
        var borrower = Main.libraryService.borrowerRepository.findById(borrowedBook.borrowerId);

        if (book == null || borrower == null) {
            return "BorrowedBookDisplay{borrowedBook=invalid_data}";
        }
        return String.format("#%s | Book: %s | Borrower: %s (%s) | Borrowed: %s", borrowedBook.id, book.title,
                borrower.name, borrower.email, borrowedBook.borrow_date.toString());
    }

    public static BorrowedBookDisplay from(BorrowedBook borrowedBook) {
        return new BorrowedBookDisplay(borrowedBook);
    }
}
