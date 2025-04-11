/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package forms.tables;

import app.Main;
import db.DatabaseRepository;
import db.annotations.Column;
import models.Book;
import models.DBModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.Date;
import java.util.ArrayList;

public class DBTableModel<T extends DBModel> extends AbstractTableModel {
    private final String[] columnNames;
    private final ArrayList<T> objects;
    private final DatabaseRepository<T> repository;
    private final Class<T> clazz;

    public DBTableModel(Class<T> clazz) {
        this.clazz = clazz;
        objects = new ArrayList<>();
        repository = Main.libraryService.getRepository(clazz);

        repository.getAll().forEach(obj -> {
            if (obj.getClass() == clazz) {
                objects.add(obj);
            }
        });

        var fields = clazz.getDeclaredFields();

        ArrayList<String> columnNamesList = new ArrayList<>();
        for (var field : fields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            var annotation = field.getAnnotation(Column.class);
            columnNamesList.add(annotation.name());
        }

        columnNames = columnNamesList.toArray(new String[0]);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return objects.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T obj = objects.get(rowIndex);

        var columnName = columnNames[columnIndex];

        for (var prop : clazz.getDeclaredFields()) {
            if (!prop.isAnnotationPresent(Column.class)) {
                continue;
            }

            var annotation = prop.getAnnotation(Column.class);

            if (!annotation.name().equals(columnName)) {
                continue;
            }

            try {
                return prop.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    public void removeRow(int rowIndex) {
        objects.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void addRow(T obj) {
        objects.add(obj);
        fireTableRowsInserted(0, objects.size());
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        T obj = objects.get(rowIndex);

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
                if (prop.getType() == int.class) {
                    try {
                        value = Integer.parseInt((String) value);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid number format for " + columnName);
                        return;
                    }
                }

                if (prop.getType() == String.class) {
                    value = value.toString();
                }

                if(prop.getType() == Date.class){
                    try {
                        value = Date.valueOf((String) value);
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(null, "Invalid date format for " + columnName);
                        return;
                    }
                }

                prop.set(obj, value);

                repository.update(obj);
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
