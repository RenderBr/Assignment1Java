/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package db;

import db.annotations.Column;
import db.annotations.TableName;
import models.DBModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    private static Database instance;
    private Connection connection;
    private final String databaseFile = "./resources/database.sqlite";
    private final String connectionString = "jdbc:sqlite:" + databaseFile;


    private Database()
    {
        try {
            // create db file if not exists
            File f = new File(databaseFile);
            if(f.createNewFile()){
                System.out.println("Database file has been created automatically.");
            }

            connection = DriverManager.getConnection(connectionString);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Database getInstance()
    {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public <T extends DBModel> DatabaseRepository<T> initializeTable(Class<T> modelClass) {
        if (!modelClass.isAnnotationPresent(TableName.class)) {
            throw new RuntimeException(String.format("%s was not given a value for class: %s", TableName.class.getSimpleName(), modelClass.getSimpleName()));
        }

        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + modelClass.getAnnotation(TableName.class).name() + " (");

        Field[] fields = modelClass.getFields();
        ArrayList<String> foreignKeys = new ArrayList<>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            var columnAnnotation = field.getAnnotation(Column.class);
            var columnText = DBModel.getColumnQueryText(columnAnnotation);
            queryBuilder.append(columnText);

            if (!columnAnnotation.foreign_key().isBlank()) {
                foreignKeys.add(String.format("FOREIGN KEY (%s) REFERENCES %s", columnAnnotation.name(), columnAnnotation.foreign_key()));
            }

            if (fields[fields.length - 1] == field) {
                break;
            }

            queryBuilder.append(",\n");
        }

        if((long) foreignKeys.size() > 0){
            queryBuilder.append(",\n");

            for(int i = 0; i < foreignKeys.size(); i++){
                queryBuilder.append(foreignKeys.get(i));
                if(!(i == foreignKeys.size()-1)){
                    queryBuilder.append(",\n");
                }
            }
        }

        queryBuilder.append("\n);");

        try {
            connection.createStatement().executeUpdate(queryBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new DatabaseRepository<>(modelClass);
    }
}
