package bayeslife;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;


/**
 */
public class MyComponent implements Callable {

    public Object onCall(MuleEventContext eventContext) throws Exception {

        System.out.println("----------------------");
        MuleMessage m = eventContext.getMessage();
        //eventContext.getMessage().setInvocationProperty("myProperty", "Hello World!");
        System.out.println("Java Component called with payload: "+eventContext.getMessage().getPayload().getClass().getName());

        System.out.println("Java Component called with payload: "+eventContext.getMessage().getPayloadAsString("utf-8"));

        System.out.println("Java Component called with payload: "+eventContext.getMessage().getOriginalPayload().getClass().getName());

        return eventContext.getMessage();
    }
}
