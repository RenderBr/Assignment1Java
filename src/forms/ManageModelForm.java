/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package forms;

import forms.listeners.AddModelListener;
import forms.listeners.DeleteModelListener;
import forms.tables.DBTableModel;
import models.DBModel;

import javax.swing.*;
import java.awt.*;

public class ManageModelForm<T extends DBModel> extends JFrame {

    public ManageModelForm(Class<T> model) {

        setTitle("Manage " + model.getSimpleName().toLowerCase() + "s");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        JLabel manageModelTitle = new JLabel(model.getSimpleName() + "s");
        manageModelTitle.setFont(new Font("Arial", Font.BOLD, 18));
        manageModelTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JButton addModelButton = new JButton("Add " + model.getSimpleName());
        JButton deleteModelButton = new JButton("Delete " + model.getSimpleName());

        JTable objTable = new JTable(new DBTableModel<>(model));
        JScrollPane scrollPane = new JScrollPane(objTable);

        // Create layout
        JPanel manageModelFrame = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(manageModelTitle, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addModelButton);
        buttonPanel.add(deleteModelButton);

        manageModelFrame.add(topPanel, BorderLayout.NORTH);
        manageModelFrame.add(scrollPane, BorderLayout.CENTER);
        manageModelFrame.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(manageModelFrame);

        // Add listeners
        addModelButton.addActionListener(new AddModelListener<>(objTable, model));
        deleteModelButton.addActionListener(new DeleteModelListener<>(objTable, model));

        setVisible(true);
    }
}
