/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package models;

import db.annotations.Column;
import db.annotations.ColumnDataType;
import db.annotations.TableName;

import java.sql.ResultSet;
import java.sql.SQLException;

@TableName(name = "books")
public class Book extends DBModel {
    @Column(name = "id", type = ColumnDataType.INTEGER,
            auto_increment = true, primary_key = true)
    public int id;

    @Column(name = "title", type = ColumnDataType.TEXT)
    public String title;

    @Column(name = "author", type = ColumnDataType.TEXT)
    public String author;

    @Column(name = "available_copies", type = ColumnDataType.INTEGER,
            default_value = "0")
    public int availableCopies;

    public Book(){
        super();
    }

    public Book(ResultSet set) throws SQLException, IllegalAccessException {
        super(set);
    }

    public Book(String title, String author, int availableCopies){
        this.title = title;
        this.author = author;
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return String.format("#%s | %s by %s | %s available copies", id, title, author, availableCopies);
    }

}
