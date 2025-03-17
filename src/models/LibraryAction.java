package models;

public class LibraryAction
{
    public String desc;
    public Runnable action;

    public LibraryAction(String desc, Runnable action){
        this.desc = desc;
        this.action = action;
    }
}
