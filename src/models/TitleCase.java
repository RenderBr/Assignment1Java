/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package models;

public class TitleCase
{
    public static String convert(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder titleCase = new StringBuilder();
        String[] words = input.split(" ");

        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)));
                titleCase.append(word.substring(1).toLowerCase());
                titleCase.append(" ");
            }
        }

        return titleCase.toString().trim();
    }
}
