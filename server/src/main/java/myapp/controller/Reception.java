package myapp.controller;

import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.service.SomeService;


public class Reception extends DolphinServerAction {
    private SomeService myService;

    public Reception(SomeService myService) {
        this.myService = myService;
    }

    public void registerIn(ActionRegistry registry) {
        getServerDolphin().register(new CantonController(myService));


        getServerDolphin().register(new ApplicationStateController());
    }
}
