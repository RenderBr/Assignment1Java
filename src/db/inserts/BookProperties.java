package db.inserts;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookProperties implements InsertProperties {
    public String title;
    public String author;
    public int availableCopies;

    public BookProperties(String title, String author, int availableCopies){
        this.title = title;
        this.author = author;
        this.availableCopies = availableCopies;
    }

    @Override
    public void replaceQueryPlaceholders(PreparedStatement statement) throws SQLException {
        statement.setString(1, title);
        statement.setString(2, author);
        statement.setInt(3, availableCopies);
    }

    @Override
    public String getQuery() {
        return "";
    }
}
