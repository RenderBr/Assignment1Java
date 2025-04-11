package app;

import db.Database;
import db.DatabaseRepository;
import models.*;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;

public class LibraryService {
    public DatabaseRepository<Book> bookRepository;
    public DatabaseRepository<Borrower> borrowerRepository;
    public DatabaseRepository<BorrowedBook> borrowedBooksRepository;
    InputStream input;
    PrintStream output;
    boolean running = false;
    Scanner scanner;

    HashMap<Integer, LibraryAction> libraryActions;

    public LibraryService(InputStream input, PrintStream output) {
        bookRepository = Database.getInstance().initializeTable(Book.class);
        borrowerRepository = Database.getInstance().initializeTable(Borrower.class);
        borrowedBooksRepository = Database.getInstance().initializeTable(BorrowedBook.class);

        this.input = input;
        this.output = output;

        libraryActions = new HashMap<>() {{
            put(1, new LibraryAction("Add a new book", () -> addBook()));
            put(2, new LibraryAction("List all books", () -> listObjects(bookRepository)));
            put(3, new LibraryAction("Update book details", () -> updateBookDetails()));
            put(4, new LibraryAction("Delete a book", () -> deleteBook()));
            put(5, new LibraryAction("Add a new borrower", () -> addBorrower()));
            put(6, new LibraryAction("List all borrowers", () -> listObjects(borrowerRepository)));
            put(7, new LibraryAction("Borrow a book", () -> borrowBook()));
            put(8, new LibraryAction("Return a book", () -> returnBook()));
            put(9, new LibraryAction("View all borrowed books", () -> listBorrowedBooks()));
            put(0, new LibraryAction("Exit", () -> exitLibrary()));
        }};
    }

    public <T extends DBModel> DatabaseRepository<T> getRepository(Class<T> modelClass) {
        if (modelClass == Book.class) {
            return (DatabaseRepository<T>) bookRepository;
        } else if (modelClass == Borrower.class) {
            return (DatabaseRepository<T>) borrowerRepository;
        } else if (modelClass == BorrowedBook.class) {
            return (DatabaseRepository<T>) borrowedBooksRepository;
        }

        throw new RuntimeException("Invalid model class: " + modelClass.getSimpleName());
    }

    public void beginLibraryConsoleService() {
        running = true;
        scanner = new Scanner(input);

        while (running) {
            outputWelcomeMessage();
            int choice;

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                output.println("You must enter a valid integer!");
                scanner.nextLine();
                continue;
            }

            var selection = libraryActions.get(choice);

            if (choice < 0 || choice > 9 || selection == null) {
                output.println("Invalid option.");
                continue;
            }

            selection.action.run();
        }
    }

    private void borrowBook() {
        var book = findBookFromInput();
        if (book == null) {
            return;
        }

        var borrower = findBorrowerFromInput();
        if (borrower == null) {
            return;
        }

        var borrowedBook = new BorrowedBook(book, borrower);

        var existing = borrowedBooksRepository.filter(bb -> bb.borrowerId == borrower.id && bb.bookId == book.id && bb.return_date == null);

        if (!existing.isEmpty()) {
            output.printf("This book has already been borrowed by this user, with a BorrowedBook ID of: %s", existing.getFirst().id);
            return;
        }

        borrowedBooksRepository.insert(borrowedBook);

        book.availableCopies--;
        bookRepository.update(book);
        output.printf("%s has borrowed the book '%s'. There are now %s copies left for %s.\n", borrower.name, book.title,
                book.availableCopies, book.title);
    }

    private void returnBook() {
        var book = findBookFromInput();
        if (book == null) {
            return;
        }

        var borrower = findBorrowerFromInput();
        if (borrower == null) {
            return;
        }

        var existing = borrowedBooksRepository.filter(bb -> bb.borrowerId == borrower.id && bb.bookId == book.id && bb.return_date == null);

        if (existing.isEmpty()) {
            output.printf("The book: %s has not been borrowed by the user: %s\n", book.title, borrower.name);
            return;
        }

        // do foreach in case for some reason there are multiple entries for this book and user unreturned
        existing.forEach(bb -> {
            bb.return_date = new Date(System.currentTimeMillis());
            borrowedBooksRepository.update(bb);

            book.availableCopies++;
            bookRepository.update(book);
        });

        output.printf("%s has returned the book '%s'. There are now %s copies left for %s.\n", borrower.name, book.title,
                book.availableCopies, book.title);
    }

    private void listBorrowedBooks() {
        output.println("What kind of search would you like to do? Type one of the following: user, book, none");
        var searchTypeInput = scanner.nextLine().strip();
        Predicate<BorrowedBook> filter;
        boolean all = false;

        switch (searchTypeInput) {
            case "u":
            case "user":
            case "borrower": {
                var user = findBorrowerFromInput();

                if (user == null) {
                    output.println("Invalid user. Please re-run this command.");
                    return;
                }

                filter = (borrowedBook -> borrowedBook.borrowerId == user.id && borrowedBook.return_date == null);
                break;
            }
            case "b":
            case "book": {
                var book = findBookFromInput();

                if (book == null) {
                    output.println("Invalid book. Please re-run this command.");
                    return;
                }

                filter = (borrowedBook -> borrowedBook.bookId == book.id && borrowedBook.return_date == null);
                break;
            }
            default: // all search
            {
                filter = (borrowedBook -> borrowedBook.return_date == null);
                all = true;
                break;
            }
        }

        var borrowedBooks = borrowedBooksRepository.filter(filter);

        if (borrowedBooks.isEmpty() && all) {
            output.println("There are no borrowed books that need returning.");
            return;
        } else if (borrowedBooks.isEmpty()) {
            output.println("We could not find any borrowed books with this filter.");
            return;
        }

        output.printf("There are %s currently borrowed books matching this query:\n", borrowedBooks.size());
        borrowedBooks.forEach(borrowedBook ->{
            output.println(borrowedBook);
        });
    }

    private Borrower findBorrowerFromInput() {
        output.println("Enter borrower ID, email, or name");
        var borrowerSearchInput = scanner.nextLine().strip();

        var borrowerSearch = borrowerRepository.filter(b -> b.name.equalsIgnoreCase(borrowerSearchInput)
                || b.email.equalsIgnoreCase(borrowerSearchInput));

        if (borrowerSearch.isEmpty()) {
            try {
                int borrowerId = Integer.parseInt(borrowerSearchInput);
                borrowerSearch = borrowerRepository.filter(b -> b.id == borrowerId);

                if (borrowerSearch.isEmpty()) {
                    throw new Exception("No results for search.");
                }
            } catch (Exception ex) {
                output.println("No borrower could be found with the input: " + borrowerSearchInput);
                return null;
            }
        }

        return borrowerSearch.getFirst();
    }

    private Book findBookFromInput() {
        output.println("Enter book ID or title: ");
        var bookSearchInput = scanner.nextLine().strip();

        var bookSearch = bookRepository.filter(b -> b.title.equalsIgnoreCase(bookSearchInput));

        if (bookSearch.isEmpty()) {
            try {
                int bookId = Integer.parseInt(bookSearchInput);
                bookSearch = bookRepository.filter(b -> b.id == bookId);

                if (bookSearch.isEmpty()) {
                    throw new Exception("No results for search.");
                }

            } catch (Exception nfe) {
                output.println("No book could be found with the input: " + bookSearchInput);
                return null;
            }
        }

        return bookSearch.getFirst();
    }

    private void deleteBook() {
        var book = findBookFromInput();

        if (book == null) {
            return;
        }

        output.printf("Are you sure you want to delete: '%s'\n", book.title);
        output.print("Enter Y/N: ");

        var choice = scanner.nextLine().strip().toLowerCase();

        if (choice.equals("y")) {
            bookRepository.deleteById(book.id);
            output.printf("The book '%s' has been deleted successfully!\n", book.title);
        } else {
            output.println("Cancelling, book will not be deleted.");
        }

    }

    private void updateBookDetails() {
        try {
            var book = findBookFromInput();

            if (book == null) {
                return;
            }

            output.printf("Modify Title (%s)\n", book.title);
            output.print("New Title (press enter to keep): ");

            var newTitle = scanner.nextLine();
            if (!newTitle.isBlank() || !Objects.equals(newTitle, book.title)) {
                book.title = newTitle;
                bookRepository.update(book);
            }

            output.printf("Change Author (%s)\n", book.author);
            output.print("New Author (press enter to keep): ");
            var newAuthor = scanner.nextLine();
            if (!newTitle.isBlank() || !Objects.equals(newAuthor, book.author)) {
                book.author = newAuthor;
                bookRepository.update(book);
            }

            output.printf("Update Amount Available (%s)\n", book.availableCopies);
            output.print("New Amount (press enter to keep): ");

            int newAmount = -1;

            while (newAmount < 0) {
                try {
                    var newAmountInput = scanner.nextLine().strip();
                    if (newAmountInput.isBlank()) {
                        newAmount = book.availableCopies;
                        break;
                    }

                    newAmount = Integer.parseInt(newAmountInput);
                } catch (Exception ex) {
                    output.print("Invalid number! Must be a positive integer, or zero.");
                    scanner.nextLine();
                    continue;
                }
            }

            if (newAmount != book.availableCopies) {
                book.availableCopies = newAmount;
                bookRepository.update(book);
            }
        } catch (Exception ex) {
            ex.printStackTrace(output);
        }
    }

    private void addBorrower() {
        try {
            output.println("Borrower Name: ");
            var borrowerName = scanner.nextLine().strip();

            while (borrowerName.isBlank()) {
                output.println("Borrower name can not be empty!");
                borrowerName = scanner.nextLine().strip();
            }

            output.println("Borrower Email: ");
            var borrowerEmail = scanner.nextLine();

            while (borrowerEmail.isBlank()) {
                output.println("Borrower email can not be empty!");
                borrowerEmail = scanner.nextLine().strip();
            }

            Borrower borrower = new Borrower(borrowerName, borrowerEmail);
            borrowerRepository.insert(borrower);
            output.println("Borrower has been created!");
        } catch (Exception ex) {
            ex.printStackTrace(output);
            output.println("Something went wrong when adding a borrower.");
        }
    }

    private void addBook() {
        try {
            output.println("Book Name: ");
            var bookName = scanner.nextLine();

            output.println("Book Author: ");
            var bookAuthor = scanner.nextLine();

            output.println("How many copies available? (positive integer)");
            var bookCopies = scanner.nextInt();

            bookRepository.insert(new Book(bookName, bookAuthor, bookCopies));
        } catch (Exception e) {
            e.printStackTrace(output);
            output.println("Something went wrong when adding the book.");
        }
    }

    private <T extends DBModel> void listObjects(DatabaseRepository<T> repository) {
        var objects = repository.getAll();

        output.printf("There are %s %s in the database:\n", objects.size(), repository.getTableName());
        for (var obj : objects) {
            output.println(obj);
        }
    }

    private void exitLibrary() {
        output.println("Exiting library...");
        running = false;
    }

    private void outputWelcomeMessage() {
        output.println("Welcome to the Library Management System!");

        for (var key : libraryActions.keySet()) {
            var action = libraryActions.get(key);
            output.printf("%s. %s\n", key.toString(), action.desc);
        }

        output.print("Enter your choice: ");
    }
}
