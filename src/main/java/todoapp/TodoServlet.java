package todoapp;


import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;
import nextapp.echo.webcontainer.service.StaticBinaryService;
import nextapp.echo.webcontainer.service.StaticTextService;

public class TodoServlet extends WebContainerServlet {
    private static final Service CUSTOM_STYLE_SHEET = StaticTextService.forResource("CustomCSS", "text/css",
            "CustomFonts.css");

    public TodoServlet() {
        addInitStyleSheet(CUSTOM_STYLE_SHEET);
    }

    @Override
    public ApplicationInstance newApplicationInstance() {
        return new TodoApp();
    }
}
