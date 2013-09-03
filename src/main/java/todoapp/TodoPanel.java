package todoapp;

import java.util.Observable;
import java.util.Observer;
import nextapp.echo.app.Button;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Row;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import static todoapp.Visibility.*;

public class TodoPanel extends ContentPane  {

    private final Grid todoGrid = new Grid();
    private final TextField addTodoField = new TextField();
    private final Label itemsLeft = new Label();
    private final CheckBox checkAll = new CheckBox();
    private final Button buttonAll = new Button("All");
    private final Button buttonActive = new Button("Active");
    private final Button buttonCompleted = new Button("Completed");
    private final Button buttonClearCompleted = new Button();
    private final Grid controlsGrid = new Grid();
    private final Column container = new Column();

    private final TodoActions listener = new TodoActions();
    private final TodoList model;

    private Visibility visibility = ALL;

    public TodoPanel(TodoList model) {
        this.model = model;
        this.model.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                updateUI();
            }
        });

        Grid pageGrid = new Grid();
        pageGrid.setSize(3);
        pageGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);

        Grid contentGrid = new Grid();
        contentGrid.setSize(1);
        contentGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);
        contentGrid.setWidth(new Extent(600, Extent.PX));

        Label title = new Label("todos");
        title.setStyleName("Title");
        contentGrid.add(title);

        Button header = new Button();
        header.setStyleName("Header");
        contentGrid.add(header);

        Row addTodoRow = new Row();
        addTodoRow.add(checkAll);
        addTodoRow.setStyleName("AddTodo");
        addTodoField.setStyleName("AddTodoText");

        checkAll.setStyleName("TodoItemCheckBox");

        addTodoRow.add(addTodoField);

        container.add(addTodoRow);
        container.add(todoGrid);
        todoGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);
        todoGrid.setSize(1);
        todoGrid.setWidth(new Extent(100, Extent.PERCENT));


        controlsGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);
        controlsGrid.setSize(3);
        controlsGrid.setColumnWidth(0, new Extent(33, Extent.PERCENT));
        controlsGrid.setColumnWidth(1, new Extent(33, Extent.PERCENT));
        controlsGrid.setColumnWidth(2, new Extent(34, Extent.PERCENT));
        controlsGrid.setBackground(new Color(240, 240, 240));
        controlsGrid.setWidth(new Extent(100, Extent.PERCENT));

        controlsGrid.setInsets(new Insets(10, 2, 2, 2));
        controlsGrid.add(itemsLeft);
        Row controlVis = new Row();
        buttonAll.setInsets(new Insets(5, 5, 5, 5));
        buttonActive.setInsets(new Insets(5, 5, 5, 5));
        buttonCompleted.setInsets(new Insets(5, 5, 5, 5));
        controlVis.add(buttonAll);
        controlVis.add(buttonActive);
        controlVis.add(buttonCompleted);
        controlsGrid.add(controlVis);
        controlsGrid.add(buttonClearCompleted);


        itemsLeft.setStyleName("ItemsLeft");
        buttonAll.setStyleName("Selected");
        buttonActive.setStyleName("Default");
        buttonCompleted.setStyleName("Default");
        buttonClearCompleted.setStyleName("Default");

        buttonAll.addActionListener(listener);
        buttonActive.addActionListener(listener);
        buttonCompleted.addActionListener(listener);
        buttonClearCompleted.addActionListener(listener);
        checkAll.addActionListener(listener);
        addTodoField.addActionListener(listener);

        contentGrid.add(container);

        pageGrid.setWidth(new Extent(100, Extent.PERCENT));
        pageGrid.add(new Label());
        pageGrid.add(contentGrid);
        pageGrid.add(new Label());
        pageGrid.setColumnWidth(0, new Extent(33, Extent.PERCENT));
        pageGrid.setColumnWidth(1, new Extent(33, Extent.PERCENT));
        pageGrid.setColumnWidth(2, new Extent(34, Extent.PERCENT));

        add(pageGrid);
        setBackgroundImage(new FillImage(new ResourceImageReference("/bg.png")));
    }

    private void updateUI() {
        // Update Button Styles
        buttonAll.setStyleName(visibility == ALL ? "Selected" : "Default");
        buttonCompleted.setStyleName(visibility == COMPLETED ? "Selected" : "Default");
        buttonActive.setStyleName(visibility == ACTIVE ? "Selected" : "Default");


        // Rebuild list
        todoGrid.removeAll();
        int completionCount = 0;
        for (TodoItem item : model.getItems()) {
            boolean completed = item.isCompleted();
            completionCount += completed ? 1 : 0;
            TodoRow todoRow = new TodoRow(item);

            if (visibility == ALL || (visibility == COMPLETED && completed) || (visibility == ACTIVE && !completed)) {
                todoGrid.add(todoRow);
            }

        }

        todoGrid.add(controlsGrid);
        controlsGrid.setVisible(!model.getItems().isEmpty());

        if (completionCount > 0) {
            buttonClearCompleted.setText("Clear completed (" + completionCount + ")");
        } else {
            buttonClearCompleted.setText("");
        }
        int toBeDone = model.getItems().size() - completionCount;
        itemsLeft.setText(toBeDone + " item" + (toBeDone == 1 ? "" : "s") + " left");
    }


    private class TodoActions implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Object eventSource = actionEvent.getSource();
            if (eventSource == buttonAll) {
                visibility = ALL;
            } else if (eventSource == buttonCompleted) {
                visibility = COMPLETED;
            } else if (eventSource == buttonActive) {
                visibility = ACTIVE;
            } else if (eventSource == buttonClearCompleted) {
                model.clearAll();
                checkAll.setSelected(false);
            } else if (eventSource == checkAll) {
                model.markAll(checkAll.isSelected());
            } else if (eventSource == addTodoField) {
                String newTodo = addTodoField.getText().trim();
                addTodoField.setText("");
                checkAll.setSelected(false);
                if (!"".equals(newTodo)) {
                    model.add(newTodo);
                    updateUI();
                }
            }

            updateUI();
        }

    }

}
