package todoapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import nextapp.echo.app.*;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

public class TodoApp extends ApplicationInstance implements ActionListener, Observer {

    private Grid todoGrid = new Grid();
    private TextField addTodoText = new TextField();
    private Label itemsLeft = new Label();
    private CheckBox checkAll = new CheckBox();
    private Button buttonAll = new Button("All");
    private Button buttonActive = new Button("Active");
    private Button buttonCompleted = new Button("Completed");
    private Button buttonClearCompleted = new Button();
    private Grid controlsGrid = new Grid();
    private Column container = new Column();
    private List<TodoItem> items = new ArrayList<>();

    private Visibility visibility = Visibility.ALL;

    private static final StyleSheet DEFAULT_STYLE_SHEET;
    private static final ImageReference BACKGROUND_IMAGE = new ResourceImageReference("/bg.png");

    static {
        try {
            DEFAULT_STYLE_SHEET = StyleSheetLoader.load("/Default.stylesheet.xml", Thread.currentThread().getContextClassLoader());
        } catch (SerialException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Window init() {
        this.setStyleSheet(DEFAULT_STYLE_SHEET);

        Window window = new Window();
        window.setContent(buildLayout());

        checkAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (TodoItem item : items) {
                    item.setCompleted(checkAll.isSelected());
                }
            }
        });

        addTodoText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!"".equals(addTodoText.getText().trim())) {
                    addTodo(addTodoText.getText().trim());
                    addTodoText.setText("");
                    checkAll.setSelected(false);
                }
            }
        });

        return window;
    }

    protected void addTodo(String todo) {
        TodoItem item = new TodoItem(todo);
        item.addObserver(this);
        items.add(item);
        updateGUI();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == buttonAll) {
            visibility = Visibility.ALL;
        } else if (actionEvent.getSource() == buttonCompleted) {
            visibility = Visibility.COMPLETED;
        } else if (actionEvent.getSource() == buttonActive) {
            visibility = Visibility.ACTIVE;
        } else if (actionEvent.getSource() == buttonClearCompleted) {
            Iterator<TodoItem> it = items.iterator();
            while (it.hasNext()) {
                if (it.next().isCompleted()) {
                    it.remove();
                }
            }
            checkAll.setSelected(false);
        }
        updateGUI();
    }

    @Override
    public void update(Observable observable, Object o) {
        if ("deleted".equals(o)) {
            items.remove(observable);
        }
        updateGUI();
    }

    private void updateGUI() {
        buttonAll.setStyleName("Default");
        buttonCompleted.setStyleName("Default");
        buttonActive.setStyleName("Default");
        if (visibility == Visibility.ALL) {
            buttonAll.setStyleName("Selected");
        } else if (visibility == Visibility.ACTIVE) {
            buttonActive.setStyleName("Selected");
        } else {
            buttonCompleted.setStyleName("Selected");
        }

        todoGrid.removeAll();
        int completed = 0;
        for (TodoItem item : items) {
            if (item.isCompleted()) {
                completed += 1;
            }

            if (visibility == Visibility.ALL || (visibility == Visibility.COMPLETED && item.isCompleted())) {
                todoGrid.add(item.getUI());
            } else if (visibility == Visibility.ACTIVE && !item.isCompleted()) {
                todoGrid.add(item.getUI());
            }
        }

        if (items.isEmpty()) {
            container.remove(controlsGrid);
        } else {
            container.add(controlsGrid);
        }

        if (completed > 0) {
            buttonClearCompleted.setText("Clear completed (" + completed + ")");
        } else {
            buttonClearCompleted.setText("");
        }

        int toBeDone = items.size() - completed;
        itemsLeft.setText(toBeDone + " item" + (toBeDone == 1 ? "" : "s") + " left");
    }


    private ContentPane buildLayout() {
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

        ContentPane contentPane = new ContentPane();
        Row addTodoRow = new Row();
        addTodoRow.add(checkAll);
        addTodoRow.setStyleName("AddTodo");
        addTodoText.setStyleName("AddTodoText");

        checkAll.setStyleName("TodoItemCheckBox");

        addTodoRow.add(addTodoText);

        container.add(addTodoRow);
        container.add(todoGrid);
        todoGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);
        todoGrid.setSize(1);


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

        buttonAll.addActionListener(this);
        buttonActive.addActionListener(this);
        buttonCompleted.addActionListener(this);
        buttonClearCompleted.addActionListener(this);

        contentGrid.add(container);

        pageGrid.setWidth(new Extent(100, Extent.PERCENT));
        pageGrid.add(new Label());
        pageGrid.add(contentGrid);
        pageGrid.add(new Label());
        pageGrid.setColumnWidth(0, new Extent(33, Extent.PERCENT));
        pageGrid.setColumnWidth(1, new Extent(33, Extent.PERCENT));
        pageGrid.setColumnWidth(2, new Extent(34, Extent.PERCENT));

        contentPane.add(pageGrid);
        contentPane.setBackgroundImage(new FillImage(BACKGROUND_IMAGE));
        return contentPane;
    }

    enum Visibility {
        ALL, ACTIVE, COMPLETED
    }

}
