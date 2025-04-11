/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package app;

import forms.MainMenuForm;

public class Main {
    public static LibraryService libraryService;
    
    public static void main(String[] args) {
        libraryService = new LibraryService(System.in, System.out);
        MainMenuForm form = new MainMenuForm();
    }
}