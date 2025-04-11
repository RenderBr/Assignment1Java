/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package forms.listeners;

import app.Main;
import db.annotations.Column;
import forms.models.NumericalValidator;
import forms.tables.ModelProvider;
import models.DBModel;
import models.TitleCase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.HashMap;

public class AddModelListener<T extends DBModel> implements ActionListener {
    private final JTable table;
    private final Class<T> modelType;

    public AddModelListener(JTable jTable, Class<T> model) {
        table = jTable;
        modelType = model;
    }

    private boolean shouldAddField(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return false;
        }

        if (field.getAnnotation(Column.class).primary_key()) {
            return false;
        }

        if (field.getAnnotation(Column.class).auto_increment()) {
            return false;
        }

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // get T fields through reflection
        JPanel panel = new JPanel(new GridLayout(0, 1));
        HashMap<String, JTextField> fieldsMap = new HashMap<>();

        var fields = modelType.getDeclaredFields();

        for (var field : fields) {
            if (!shouldAddField(field)) {
                continue;
            }

            String fieldName = field.getAnnotation(Column.class).name();
            JTextField textField = new JTextField();
            if (field.getType() == int.class) {
                textField.addKeyListener(new NumericalValidator());
            }

            fieldsMap.put(fieldName, textField);

            panel.add(new JLabel(TitleCase.convert(fieldName.replace("_", " ")) + ":"));
            panel.add(textField);
            panel.add(Box.createHorizontalStrut(15));
        }

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Add a new " + modelType.getSimpleName().toLowerCase(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                T model = modelType.getDeclaredConstructor().newInstance();

                for (var field : fields) {
                    if (!shouldAddField(field)) {
                        continue;
                    }

                    String fieldName = field.getAnnotation(Column.class).name();
                    JTextField textField = fieldsMap.get(fieldName);
                    var fieldType = field.getType();

                    if (textField != null) {
                        field.setAccessible(true);

                        // Handle different field types
                        if (fieldType == int.class) {
                            field.set(model, Integer.parseInt(textField.getText()));
                        } else if (fieldType == String.class) {
                            field.set(model, textField.getText());
                        } else if (fieldType == double.class) {
                            field.set(model, Double.parseDouble(textField.getText()));
                        } else if (fieldType == float.class) {
                            field.set(model, Float.parseFloat(textField.getText()));
                        } else if (fieldType == long.class) {
                            field.set(model, Long.parseLong(textField.getText()));
                        } else if (fieldType == boolean.class) {
                            field.set(model, Boolean.parseBoolean(textField.getText()));
                        } else if (fieldType == Date.class) {
                            field.set(model, Date.valueOf(textField.getText()));
                        }
                    }
                }

                var repository = Main.libraryService.getRepository(modelType);
                repository.insert(model);
                table.setModel(ModelProvider.constructGenericTableModel(modelType));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
