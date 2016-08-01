package bayeslife;

import junit.framework.TestCase;
import org.junit.Test;

/**

 */
public class ConsoleMainTest extends TestCase {

    @Test
    public void test1() throws Exception {

        ConsoleMain.main(new String[]{"api.yaml","put","/currentuser","positive-sample.json"});


    }


}