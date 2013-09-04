package todoapp;

import nextapp.echo.app.Button;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

/**
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public class TodoRow extends Row implements ActionListener {

    private CheckBox checkBox = new CheckBox();
    private TextField textField = new TextField();
    private Button deleteButton = new Button("✖");

    private final TodoItem model;

    public TodoRow(TodoItem model) {
        this.model = model;

        this.setStyleName("TodoItem");
        deleteButton.setStyleName("DeleteButton");
        checkBox.setStyleName("TodoItemCheckBox");

        deleteButton.addActionListener(this);
        checkBox.addActionListener(this);
        textField.addActionListener(this);

        add(checkBox);
        add(this.textField);
        add(deleteButton);

        onModelUpdate();
    }

    private void onModelUpdate() {
        this.textField.setText(model.getText());
        this.textField.setStyleName(model.isCompleted() ? "TodoTextFieldCompleted" : "TodoTextField");
        this.checkBox.setSelected(model.isCompleted());
    }

    private void onViewUpdate() {
        this.model.setText(this.textField.getText());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.checkBox) {
            model.setCompleted(this.checkBox.isSelected());
        } else if (actionEvent.getSource() == this.deleteButton) {
            model.delete();
        } else if (actionEvent.getSource() == this.textField) {
            onViewUpdate();
        }
    }

}
