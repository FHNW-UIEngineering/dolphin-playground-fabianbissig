package myapp.servlet;

import myapp.service.impl.SomeRemoteService;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

import myapp.controller.Reception;

public class MyAppServlet extends DolphinServlet {

	@Override
	protected void registerApplicationActions(DefaultServerDolphin serverDolphin) {
        SomeRemoteService myService = new SomeRemoteService();

        serverDolphin.register(new Reception(myService));
	}
}
