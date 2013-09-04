package todoapp;

import java.util.Observable;
import java.util.Observer;
import nextapp.echo.app.Alignment;
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
import nextapp.echo.app.layout.GridLayoutData;
import static todoapp.Visibility.*;

public class TodoPanel extends ContentPane implements Observer  {

    private final Grid todoGrid = new Grid(1);
    private final TextField addTodoField = new TextField();
    private final Label itemsLeft = new Label();
    private final CheckBox checkAll = new CheckBox();
    private final Button buttonAll = new Button("All");
    private final Button buttonActive = new Button("Active");
    private final Button buttonCompleted = new Button("Completed");
    private final Button buttonClear = new Button("Clear (x) completed");
    private final Grid controlsGrid = new Grid(3);

    private final TodoList model;

    private Visibility visibility = ALL;

    public TodoPanel(TodoList model) {
        // Save model
        this.model = model;
        this.model.addObserver(this);

        // Assign event listener
        ActionListener listener = new TodoActions();
        buttonAll.addActionListener(listener);
        buttonActive.addActionListener(listener);
        buttonCompleted.addActionListener(listener);
        buttonClear.addActionListener(listener);
        checkAll.addActionListener(listener);
        addTodoField.addActionListener(listener);

        // Assign styles
        addTodoField.setStyleName("AddTodoText");
        checkAll.setStyleName("TodoItemCheckBox");
        itemsLeft.setStyleName("ItemsLeft");
        buttonAll.setStyleName("Selected");
        buttonActive.setStyleName("Default");
        buttonCompleted.setStyleName("Default");
        buttonClear.setStyleName("Default");
        todoGrid.setStyleName("FullWidth");

        // set background image
        this.setBackgroundImage(new FillImage(new ResourceImageReference("/bg.png")));

        // Add layout grid and define background
        this.add(buildLayout());
    }

    private Grid buildLayout() {
        // Build header et al.
        Label title = new Label("todos");
        title.setStyleName("Title");

        Button header = new Button();
        header.setStyleName("Header");

        Row newTodoRow = new Row();
        newTodoRow.setStyleName("AddTodo");
        newTodoRow.add(checkAll);
        newTodoRow.add(addTodoField);

        Column container = new Column();
        container.add(newTodoRow);
        container.add(todoGrid);

        GridLayoutData gridCenter = new GridLayoutData();
        gridCenter.setAlignment(Alignment.ALIGN_CENTER);

        Row filterControls = new Row();
        filterControls.setCellSpacing(new Extent(10));
        filterControls.add(buttonAll);
        filterControls.add(buttonActive);
        filterControls.add(buttonCompleted);
        filterControls.setLayoutData(gridCenter);

        controlsGrid.setColumnWidth(0, new Extent(30, Extent.PERCENT));
        controlsGrid.setColumnWidth(1, new Extent(40, Extent.PERCENT));
        controlsGrid.setColumnWidth(2, new Extent(30, Extent.PERCENT));
        controlsGrid.setBackground(new Color(240, 240, 240));
        controlsGrid.setWidth(new Extent(100, Extent.PERCENT));
        controlsGrid.setInsets(new Insets(10, 2, 10, 2));

        controlsGrid.add(itemsLeft);
        controlsGrid.add(filterControls);
        controlsGrid.add(buttonClear);

        Grid contentGrid = new Grid(1);
        contentGrid.setWidth(new Extent(600, Extent.PX));
        contentGrid.add(title);
        contentGrid.add(header);
        contentGrid.add(container);

        // Make a 100% sized grid and add the content centered in it
        Grid pageGrid = new Grid();
        pageGrid.setWidth(new Extent(100, Extent.PERCENT));

        contentGrid.setLayoutData(gridCenter);

        pageGrid.add(contentGrid);
        return pageGrid;
    }

    private void updateUI() {
        // Update Button Styles
        buttonAll.setStyleName(visibility == ALL ? "Selected" : "Default");
        buttonCompleted.setStyleName(visibility == COMPLETED ? "Selected" : "Default");
        buttonActive.setStyleName(visibility == ACTIVE ? "Selected" : "Default");

        controlsGrid.setVisible(!model.getItems().isEmpty());

        // Rebuild list
        todoGrid.removeAll();
        int completionCount = 0;
        for (TodoItem item : model.getItems()) {
            completionCount += item.isCompleted() ? 1 : 0;
            TodoRow todoRow = new TodoRow(item);

            if (visibility == ALL
                    || (visibility == COMPLETED && item.isCompleted())
                    || (visibility == ACTIVE && !item.isCompleted())) {
                todoGrid.add(todoRow);
            }

        }
        todoGrid.add(controlsGrid);

        //  Update texts
        int toBeDone = model.getItems().size() - completionCount;
        buttonClear.setText(completionCount > 0 ? "Clear completed (" + completionCount + ")" : "");
        itemsLeft.setText(toBeDone + " item" + (toBeDone == 1 ? "" : "s") + " left");
    }

    @Override
    public void update(Observable o, Object arg) {
        updateUI();
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
            } else if (eventSource == buttonClear) {
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
