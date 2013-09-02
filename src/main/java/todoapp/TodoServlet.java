package todoapp;


import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.StaticTextService;

public class TodoServlet extends WebContainerServlet {

    /** An Echo Service to inject a custom CSS stylesheet into the app. */
    private static final Service CUSTOM_STYLE_SHEET = StaticTextService.forResource("CustomCSS", "text/css", "CustomFonts.css");

    public TodoServlet() {
        addInitStyleSheet(CUSTOM_STYLE_SHEET);
    }

    @Override
    public ApplicationInstance newApplicationInstance() {
        return new TodoApp();
    }
}
