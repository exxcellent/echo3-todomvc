package todoapp;


import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.webcontainer.WebContainerServlet;

public class TodoServlet extends WebContainerServlet {

    @Override
    public ApplicationInstance newApplicationInstance() {
        return new TodoApp();
    }
}
