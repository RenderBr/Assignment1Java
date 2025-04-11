/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
 */

package forms;

import forms.listeners.BorrowBookListener;
import forms.listeners.OpenModelManagerListener;
import forms.listeners.ReturnBookListener;
import models.Book;
import models.Borrower;

import javax.swing.*;

public class MainMenuForm extends JFrame {
    private JButton borrowBookButton;
    private JButton manageBorrowersButton;
    private JButton manageBooksButton;
    private JLabel mainMenuTitle;
    private JLabel nameLabel;
    private JPanel mainFrame;
    private JButton returnBookButton;

    public MainMenuForm(){
        setTitle("Library Book Management Service");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainFrame);

        manageBooksButton.addActionListener(new OpenModelManagerListener<>(Book.class));
        manageBorrowersButton.addActionListener(new OpenModelManagerListener<>(Borrower.class));

        borrowBookButton.addActionListener(new BorrowBookListener());
        returnBookButton.addActionListener(new ReturnBookListener());

        JMenuBar menuBar = getjMenuBar();
        setJMenuBar(menuBar);
        setVisible(true);
    }

    private JMenuBar getjMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));

        fileMenu.add(exitMenuItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");

        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Library Book Management Service\nVersion 1.0\nDeveloped by Julian Seitz", "About", JOptionPane.INFORMATION_MESSAGE);
        });

        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

}
