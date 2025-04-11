package forms.listeners;

import app.Main;
import forms.tables.ModelProvider;
import models.DBModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteModelListener<T extends DBModel> implements ActionListener {
    private final JTable table;
    private final Class<T> modelType;

    public DeleteModelListener(JTable jTable, Class<T> model){
        table = jTable;
        modelType = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var selectedIndex = table.getSelectedRow();

        if(selectedIndex == -1){
            return;
        }

        var repository = Main.libraryService.getRepository(modelType);
        var model = table.getModel();

        var id = (int) model.getValueAt(selectedIndex, 0);

        if(repository.deleteById(id)){
            table.setModel(ModelProvider.constructGenericTableModel(modelType));
        } else {
            JOptionPane.showMessageDialog(table, "Failed to delete the selected item.");
        }
    }
}
