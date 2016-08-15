package bayeslife;

import junit.framework.TestCase;
import org.junit.Test;

/**

 */
public class ConsoleMainTest extends TestCase {

    @Test
    public void test1() throws Exception {

        CommandLineInterface.main(new String[]{"api.yaml","put","/currentuser","positive-sample.json"});

    }

    @Test
    public void test2() throws Exception {

        CommandLineInterface.main(new String[]{"raml1/api/api.raml","put","/currentuser","positive-sample.json"});

    }


}