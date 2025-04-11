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

@TableName(name = "borrowers")
public class Borrower extends DBModel {
    @Column(name = "id", type = ColumnDataType.INTEGER,
            auto_increment = true, primary_key = true)
    public int id;

    @Column(name = "name", type = ColumnDataType.TEXT)
    public String name;

    @Column(name = "email", type = ColumnDataType.TEXT)
    public String email;

    public Borrower(ResultSet set) throws SQLException, IllegalAccessException {
        super(set);
    }

    public Borrower(String name, String email) {
        this.name = name;
        this.email = email;
    }


    public Borrower(){
        super();
    }

    @Override
    public String toString() {
        return String.format("#%s | %s | Email: %s", id, name, email);
    }
}
