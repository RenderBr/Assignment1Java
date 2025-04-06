package forms.listeners;

import forms.ManageBooksForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageBooksListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        var manageBooksForm = new ManageBooksForm();
    }
}
