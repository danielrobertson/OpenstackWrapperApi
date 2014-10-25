package assign.resources;

import assign.services.EavesdropRestService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class EavesdropApplication extends Application {
    private Set<Object> singletons = new HashSet<Object>();

    public EavesdropApplication() {
        singletons.add(new EavesdropRestService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
