package todoapp;

import java.util.Observable;

public class TodoItem extends Observable {

    private boolean completed;
    private String text;

    public TodoItem(String text) {
        this.text = text;
        this.completed = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setChanged();
        notifyObservers();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        setChanged();
        notifyObservers();
    }

    public void delete() {
        setChanged();
        notifyObservers("deleted");
    }
}
