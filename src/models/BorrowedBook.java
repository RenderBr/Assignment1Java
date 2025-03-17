package models;

import db.annotations.Column;
import db.annotations.ColumnDataType;
import db.annotations.TableName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

@TableName(name = "borrowed_books")
public class BorrowedBook extends DBModel {
    @Column(name = "id", type = ColumnDataType.INTEGER,
            auto_increment = true, primary_key = true)
    public int id;

    @Column(name = "book_id", type = ColumnDataType.INTEGER,
            foreign_key = "books(id)")
    public int bookId;

    @Column(name = "borrower_id", type = ColumnDataType.INTEGER,
            foreign_key = "borrowers(id)")
    public int borrowerId;

    @Column(name = "borrow_date", type = ColumnDataType.DATETIME)
    public Date borrow_date;

    @Column(name = "return_date", type = ColumnDataType.DATETIME)
    public Date return_date;

    public BorrowedBook(ResultSet set) throws SQLException, IllegalAccessException {
        super(set);
    }

    private BorrowedBook(int bookId, int borrowerId, Date borrow_date, Date return_date) {
        this.bookId = bookId;
        this.borrowerId = borrowerId;
        this.borrow_date = borrow_date;
        this.return_date = return_date;
    }

    public BorrowedBook(Book book, Borrower borrower, Date borrow_date){
        this.bookId = book.id;
        this.borrowerId = borrower.id;
        this.borrow_date = borrow_date;
    }

    public BorrowedBook(Book book, Borrower borrower){
        this.bookId = book.id;
        this.borrowerId = borrower.id;
        this.borrow_date = new Date(System.currentTimeMillis());
    }

    public BorrowedBook(Book book, Borrower borrower, Date borrow_date, Date return_date){
        this.bookId = book.id;
        this.borrowerId = borrower.id;
        this.borrow_date = borrow_date;
        this.return_date = return_date;
    }
}
