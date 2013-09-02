package todoapp;

import java.util.Observable;
import nextapp.echo.app.Button;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Component;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

public class TodoItem extends Observable implements ActionListener {

    private CheckBox checkBox = new CheckBox();
    private TextField textField = new TextField();
    private Row row = new Row();
    private Button deleteButton = new Button("✖");
    private boolean completed;

    public TodoItem(String text) {
        // assign style names
        textField.setStyleName("TodoTextField");
        deleteButton.setStyleName("DeleteButton");
        checkBox.setStyleName("TodoItemCheckBox");
        row.setStyleName("TodoItem");

        deleteButton.addActionListener(this);
        checkBox.addActionListener(this);

        textField.setText(text);

        row.add(checkBox);
        row.add(this.textField);
        row.add(deleteButton);
    }

    public Component getUI() {
        return this.row;
    }

    public boolean isCompleted() {
        return this.checkBox.isSelected();
    }

    public void setCompleted(boolean completed) {
        this.textField.setStyleName(completed ? "TodoTextFieldCompleted" : "TodoTextField");
        this.checkBox.setSelected(completed);
        setChanged();
        notifyObservers();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.checkBox) {
            setCompleted(this.checkBox.isSelected());
        } else if (actionEvent.getSource() == this.deleteButton) {
            setChanged();
            notifyObservers("deleted");
        }
    }
}
