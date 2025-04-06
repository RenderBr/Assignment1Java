package forms.tables;

import db.annotations.Column;
import models.Book;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;

public class BookTableModel extends AbstractTableModel {
    private final String[] columnNames = {"id", "title", "author", "available_copies"};
    private final ArrayList<Book> books;

    public BookTableModel(ArrayList<Book> book) {
        this.books = book;
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);

        var columnName = columnNames[columnIndex];

        for (var prop : Book.class.getDeclaredFields()) {
            if (!prop.isAnnotationPresent(Column.class)) {
                continue;
            }

            var annotation = prop.getAnnotation(Column.class);

            if (!annotation.name().equals(columnName)) {
                continue;
            }

            try {
                return prop.get(book);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Allow editing
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);

        var columnName = columnNames[columnIndex];

        for (var prop : Book.class.getDeclaredFields()) {
            if (!prop.isAnnotationPresent(Column.class)) {
                continue;
            }

            var annotation = prop.getAnnotation(Column.class);

            if (!annotation.name().equals(columnName)) {
                continue;
            }

            try {
                prop.set(book, value);
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
