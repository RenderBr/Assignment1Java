package forms.tables;

import models.DBModel;

import javax.swing.table.AbstractTableModel;

public class ModelProvider {

    public static <T extends DBModel> AbstractTableModel constructGenericTableModel(Class<T> modelClass) {
        return new DBTableModel<>(modelClass);
    }
}
