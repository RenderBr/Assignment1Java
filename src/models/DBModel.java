package models;

import db.annotations.Column;
import db.annotations.ColumnDataType;
import db.annotations.TableName;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DBModel {
    public DBModel(ResultSet set) throws SQLException, IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            String columnName = column.name();

            field.setAccessible(true);
            Object value = set.getObject(columnName);

            if(value == null){
                continue;
            }

            if (column.type() == ColumnDataType.DATETIME) {
                field.set(this, new Date((Long) value));
            } else {
                field.set(this, value);
            }
        }
    }

    public DBModel() {
    }

    public String getUpdateQuery(){
        //UPDATE %s SET title = ?, author = ?, available_copies = ? WHERE id = ?
        StringBuilder query = new StringBuilder();

        if(!this.getClass().isAnnotationPresent(TableName.class)){
            throw new RuntimeException(String.format("DB Model does not contain a %s", TableName.class.getSimpleName()));
        }

        query.append("UPDATE ").append(this.getClass().getAnnotation(TableName.class).name())
                .append(" SET ");

        int fieldCount = 0;
        var fields = getClass().getDeclaredFields();

        for(Field field : fields){
            if(!field.isAnnotationPresent(Column.class)){
                continue;
            }

            var annotation = field.getAnnotation(Column.class);
            if(annotation.auto_increment()){
                continue;
            }
            query.append(annotation.name()).append(" = ?");
            fieldCount++;
            if (fields[fields.length - 1] == field) {
                break;
            }
            query.append(",");

        }

        query.append("WHERE ID = ?");

        return query.toString();
    }

    public String getInsertionQuery() {
        // INSERT INTO books (title, author, available_copies) VALUES (?, ?, ?)
        StringBuilder query = new StringBuilder();

        if (!this.getClass().isAnnotationPresent(TableName.class)) {
            throw new RuntimeException(String.format("DB Model does not contain a %s", TableName.class.getSimpleName()));
        }

        query.append("INSERT INTO ").append(this.getClass().getAnnotation(TableName.class).name()).append(" (");

        int placeholderCount = 0;
        var fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            var annotation = field.getAnnotation(Column.class);
            if (annotation.auto_increment()) {
                continue;
            }

            query.append(annotation.name());

            placeholderCount++;
            if (fields[fields.length - 1] == field) {
                break;
            }

            query.append(",");
        }
        query.append(") VALUES (");

        String placeholders = "?,".repeat(placeholderCount - 1);

        query.append(placeholders).append("?);");

        return query.toString();

    }

    public static String getColumnQueryText(Column column) {
        StringBuilder columnQueryText = new StringBuilder();

        columnQueryText.append(String.format("%s %s", column.name(), column.type().name()));

        if (column.primary_key()) {
            columnQueryText.append(" PRIMARY KEY");
        }

        if (column.auto_increment()) {
            if (column.primary_key()) {
                columnQueryText.append(" AUTOINCREMENT");
            } else {
                throw new RuntimeException("Auto increment can only be applied to a primary key!");
            }
        }

        if (column.not_null()) {
            columnQueryText.append(" not null");
        }

        if (!column.default_value().isBlank()) {
            columnQueryText.append(" DEFAULT ").append(column.default_value());
        }

        return columnQueryText.toString();
    }
}
