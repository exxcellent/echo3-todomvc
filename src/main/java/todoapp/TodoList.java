package todoapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TodoList extends Observable implements Observer {

    private final List<TodoItem> items = new ArrayList<>();

    public List<TodoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public TodoItem add(String newTodo) {
        if (!"".equals(newTodo)) {
            TodoItem item = new TodoItem(newTodo);
            item.addObserver(this);
            items.add(item);
            update(this, null);
            return item;
        }
        return null;
    }


    public void remove(TodoItem todo) {
        items.remove(todo);
        update(this, null);
    }

    public void clearAll() {
        Iterator<TodoItem> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().isCompleted()) {
                it.remove();
            }
        }
        update(this, null);
    }

    public void markAll(boolean asDone) {
        for (TodoItem item : items) {
            item.setCompleted(asDone);
        }
        update(this, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        if ("deleted".equals(arg)) {
            remove((TodoItem) o);
        }

        setChanged();
        notifyObservers(arg);
    }
}
