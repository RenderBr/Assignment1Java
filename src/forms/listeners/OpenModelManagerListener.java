/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package forms.listeners;

import forms.ManageModelForm;
import models.DBModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenModelManagerListener<T extends DBModel> implements ActionListener {
    private final Class<T> modelType;

    public OpenModelManagerListener(Class<T> modelType) {
        this.modelType = modelType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var manageBooksForm = new ManageModelForm<>(modelType);
    }
}
