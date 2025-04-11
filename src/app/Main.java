package app;

import forms.MainMenuForm;

public class Main {
    public static LibraryService libraryService;
    
    public static void main(String[] args) {
        libraryService = new LibraryService(System.in, System.out);
        MainMenuForm form = new MainMenuForm();
    }
}