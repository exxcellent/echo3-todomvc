package todoapp;

import nextapp.echo.app.*;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import java.util.Observable;

public class TodoItem extends Observable implements ActionListener {
    private CheckBox checkBox = new CheckBox();
    private TextField text = new TextField();
    private Row row = new Row();
    private Button btnDelete = new Button("✖");

    private boolean completed;

    public TodoItem(String text) {
        this.text.setStyleName("Default");
        btnDelete.setStyleName("DeleteButton");
        checkBox.setStyleName("CheckBox");
        row.setStyleName("TodoItem");
        row.add(checkBox);
        checkBox.setInsets(new Insets(0, 10, 20, 10));
        btnDelete.addActionListener(this);
        checkBox.addActionListener(this);
        this.text.setText(text);
        this.text.setWidth(new Extent(535, Extent.PX));
        this.text.setInsets(new Insets(20, 0, 0, 0));
        row.add(this.text);
        row.add(btnDelete);
    }

    public Component getGui() {
        return this.row;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            this.text.setStyleName("Completed");
        } else {
            this.text.setStyleName("Default");
        }
        this.checkBox.setSelected(completed);
        setChanged();
        notifyObservers();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.checkBox) {
            setCompleted(this.checkBox.isSelected());
        } else if (actionEvent.getSource() == this.btnDelete) {
            setChanged();
            notifyObservers("deleted");
        }
    }
}
