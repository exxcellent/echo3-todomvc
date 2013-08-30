package todoapp;

import nextapp.echo.app.*;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

import java.util.*;

public class TodoApp extends ApplicationInstance implements ActionListener, Observer {
    private Grid todoGrid = new Grid();
    private TextField addTodoText = new TextField();
    private Label itemsLeft = new Label();
    private CheckBox checkAll = new CheckBox();
    private Button btnAll = new Button("All");
    private Button btnActive = new Button("Active");
    private Button btnCompleted = new Button("Completed");
    private Button btnClearCompleted = new Button();
    private Grid controlsGrid = new Grid();
    private Column container = new Column();

    List<TodoItem> items = new ArrayList<TodoItem>();

    public static final StyleSheet DEFAULT_STYLE_SHEET;
    public static final ImageReference BACKGROUND_IMAGE;

    static {
        try {
            DEFAULT_STYLE_SHEET = StyleSheetLoader.load(
                    "/Default.stylesheet.xml",
                    Thread.currentThread().getContextClassLoader());

            BACKGROUND_IMAGE = new ResourceImageReference("/bg.png");

        } catch (SerialException ex) {
            throw new RuntimeException(ex);
        }
    }

    enum VISIBILITY {
        ALL, ACTIVE, COMPLETED;
    }

    VISIBILITY visibility = VISIBILITY.ALL;

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
        if (actionEvent.getSource() == btnAll) {
            visibility = VISIBILITY.ALL;
        } else if (actionEvent.getSource() == btnCompleted) {
            visibility = VISIBILITY.COMPLETED;
        } else if (actionEvent.getSource() == btnActive) {
            visibility = VISIBILITY.ACTIVE;
        } else if (actionEvent.getSource() == btnClearCompleted) {
            Iterator<TodoItem> it = items.iterator();
            while (it.hasNext()) {
                if (it.next().isCompleted()) it.remove();
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
        btnAll.setStyleName("Default");
        btnCompleted.setStyleName("Default");
        btnActive.setStyleName("Default");
        if (visibility == VISIBILITY.ALL) {
            btnAll.setStyleName("Selected");
        } else if (visibility == VISIBILITY.ACTIVE) {
            btnActive.setStyleName("Selected");
        } else {
            btnCompleted.setStyleName("Selected");
        }

        todoGrid.removeAll();
        int completed = 0;
        for (TodoItem item: items) {
            if (item.isCompleted()) completed += 1;

            if (visibility == VISIBILITY.ALL || (visibility == VISIBILITY.COMPLETED && item.isCompleted())) {
                todoGrid.add(item.getGui());
            } else if (visibility == VISIBILITY.ACTIVE && !item.isCompleted()) {
                todoGrid.add(item.getGui());
            }
        }

        if (items.isEmpty()) {
            container.remove(controlsGrid);
        } else {
            container.add(controlsGrid);
        }

        if (completed > 0) {
            btnClearCompleted.setText("Clear completed ("+ completed +")");
        } else {
            btnClearCompleted.setText("");
        }

        int toBeDone = items.size() - completed;
        itemsLeft.setText(toBeDone + " item" + (toBeDone == 1 ? "" : "s") +  " left");
    }


    private ContentPane buildLayout() {
        Grid pageGrid = new Grid();
        pageGrid.setSize(3);
        pageGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);

        Grid contentGrid = new Grid();
        contentGrid.setSize(1);
        contentGrid.setOrientation(Grid.ORIENTATION_HORIZONTAL);
        contentGrid.setWidth(new Extent(600, Extent.PX));

        Button title = new Button("todos");
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
        addTodoText.setWidth(new Extent(550, Extent.PX));

        checkAll.setInsets(new Insets(0, 10, 20, 10));
        checkAll.setStyleName("CheckBox");

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

        controlsGrid.setInsets(new Insets(10,2,2,2));
        controlsGrid.add(itemsLeft);
        Row controlVis = new Row();
        btnAll.setInsets(new Insets(5,5,5,5));
        btnActive.setInsets(new Insets(5,5,5,5));
        btnCompleted.setInsets(new Insets(5,5,5,5));
        controlVis.add(btnAll);
        controlVis.add(btnActive);
        controlVis.add(btnCompleted);
        controlsGrid.add(controlVis);
        controlsGrid.add(btnClearCompleted);


        itemsLeft.setStyleName("ItemsLeft");
        btnAll.setStyleName("Selected");
        btnActive.setStyleName("Default");
        btnCompleted.setStyleName("Default");
        btnClearCompleted.setStyleName("Default");

        btnAll.addActionListener(this);
        btnActive.addActionListener(this);
        btnCompleted.addActionListener(this);
        btnClearCompleted.addActionListener(this);

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
}
