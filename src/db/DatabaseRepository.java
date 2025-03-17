package db;

import db.annotations.Column;
import db.annotations.TableName;
import db.inserts.InsertProperties;
import models.Book;
import models.DBModel;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.function.Predicate;

public class DatabaseRepository<T extends DBModel> {
    protected Connection connection;
    private final Class<T> modelClass;
    private final String tableName;

    public DatabaseRepository(Class<T> modelClass)
    {
        if (!modelClass.isAnnotationPresent(TableName.class)) {
            throw new RuntimeException(String.format("%s was not given a value for class: %s", TableName.class.getSimpleName(), modelClass.getSimpleName()));
        }

        this.connection = Database.getInstance().getConnection();
        this.modelClass = modelClass;
        this.tableName = modelClass.getAnnotation(TableName.class).name();
    }

    public String getTableName() {
        return tableName;
    }

    public T insert(T objectToInsert) {
        T insertedObject = null;

        var query = objectToInsert.getInsertionQuery();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            var fields = modelClass.getDeclaredFields();
            int columnIndex = 1;

            for (Field field : fields) {
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }

                Object fieldValue = field.get(objectToInsert);
                var column = field.getAnnotation(Column.class);

                if (column.auto_increment()) {
                    continue;
                }

                insertPlaceholder(column, columnIndex, fieldValue, stmt);
                columnIndex++;
            }

            stmt.executeUpdate();

            try (ResultSet insertedBookIds = stmt.getGeneratedKeys()) {
                if (insertedBookIds.next()) {
                    int id = insertedBookIds.getInt(1);

                    insertedObject = findById(id);
                }
            }
        } catch (SQLException | IllegalAccessException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        return insertedObject;
    }

    private void insertPlaceholder(Column column, int columnIndex,
                                   Object fieldValue, PreparedStatement stmt) throws SQLException
    {
        switch (column.type()) {
            case INTEGER -> {
                stmt.setInt(columnIndex, ((int) fieldValue));
            }
            case TEXT -> {
                stmt.setString(columnIndex, ((String) fieldValue));
            }
            case DATETIME -> {
                stmt.setDate(columnIndex, (Date) fieldValue);
            }
        }
    }

    public boolean update(T object) {
        boolean updated = false;

        try (PreparedStatement stmt = connection.prepareStatement(object.getUpdateQuery())) {

            var fields = modelClass.getDeclaredFields();
            int columnIndex = 1;
            Field identifier = null;

            for (Field field : fields) {
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }

                Object fieldValue = field.get(object);
                var column = field.getAnnotation(Column.class);

                if (column.primary_key()) {
                    identifier = field;
                }

                if (column.auto_increment()) {
                    continue;
                }

                insertPlaceholder(column, columnIndex, fieldValue, stmt);
                columnIndex++;
            }

            if (identifier != null) {
                stmt.setInt(columnIndex, identifier.getInt(object));
            }

            updated = stmt.executeUpdate() > 0;
        } catch (SQLException | IllegalAccessException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        return updated;
    }

    public boolean deleteById(int id) {
        boolean deleted = false;

        String query = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            deleted = stmt.executeUpdate() > 0;

        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        return deleted;
    }

    public ArrayList<T> filter(Predicate<T> predicate) {
        ArrayList<T> filteredList = new ArrayList<>();

        for (T item : getAll()) {
            if (predicate.test(item)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    public ArrayList<T> getAll() {
        ArrayList<T> objects = new ArrayList<>();

        String query = "SELECT * FROM " + tableName;
        try (ResultSet resultSet = connection.createStatement().executeQuery(query)) {

            while (resultSet.next()) {
                objects.add(modelClass.getConstructor(ResultSet.class).newInstance(resultSet));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        return objects;

    }

    public T findById(int id) {
        T object = null;

        String query = "SELECT * FROM " + tableName + " WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    object = modelClass.getConstructor(ResultSet.class).newInstance(resultSet);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        return object;
    }
}
