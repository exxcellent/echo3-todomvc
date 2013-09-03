package todoapp;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Window;
import nextapp.echo.app.serial.StyleSheetLoader;

public class TodoApp extends ApplicationInstance {

    private final TodoList model;
    private final TodoPanel mainPanel;

    public TodoApp() {
        model = new TodoList();
        mainPanel = new TodoPanel(model);
    }

    @Override
    public Window init() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            setStyleSheet(StyleSheetLoader.load("/Default.stylesheet.xml", classLoader));

            Window window = new Window();
            window.setContent(mainPanel);

            return window;
        } catch (Exception e) {
            throw new RuntimeException("Error on application startup", e);
        }
    }

}
