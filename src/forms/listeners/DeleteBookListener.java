package forms.listeners;

import app.Main;
import forms.tables.BookTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteBookListener  implements ActionListener {
    private final JTable table;

    public DeleteBookListener(JTable jTable){
        table = jTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var selectedIndex = table.getSelectedRow();

        if(selectedIndex == -1){
            return;
        }

        BookTableModel tableModel = (BookTableModel)table.getModel();
        int selectedBookId = (int) tableModel.getValueAt(selectedIndex, 0);

        if(Main.libraryService.bookRepository.deleteById(selectedBookId)){
            tableModel.removeRow(selectedIndex);

        }else{
            JOptionPane.showMessageDialog(null, "Book could not be removed!");
        }
    }
}
