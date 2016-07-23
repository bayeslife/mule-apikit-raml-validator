package bayeslife;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.mule.api.MuleContext;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ConsoleTest
{
    static String flows = "flow1.xml,flow2.xml";

    public static void main(String args[]) throws Exception {

        JUnitCore.runClasses(GeneratedSchemaTestCase.class);
    }

    public static String getFlows(){
        return flows;
    }

    public static void runTests(){

            given().body("{\"username\":\"gbs\",\"firstName\":\"george\",\"lastName\":\"bernard shaw\",\"emailAddresses\":[\"gbs@ie\"]}")
                    .contentType("application/json")
                    .expect()
                    .statusCode(204).body(is(""))
                    .when().put("/api/currentuser");


            given().body("{\"username\":\"gbs\",\"firstName\":\"george\",\"lastName\":\"bernard shaw\"}")
                    .contentType("application/json")
                    .expect()
                    .statusCode(400).body(is("bad request"))
                    .when().put("/api/currentuser");


            given().body("<user xmlns=\"http://mulesoft.org/schemas/sample\" username=\"gbs\" firstName=\"george\" lastName=\"bernard shaw\">" +
                    "<email-addresses><email-address>gbs@ie</email-address></email-addresses></user>")
                    .contentType("text/xml")
                    .expect()
                    .statusCode(204).body(is(""))
                    .when().put("/api/currentuser");


            given().body("<user xmlns=\"http://mulesoft.org/schemas/sample\" username=\"gbs\" firstName=\"george\" lastName=\"bernard shaw\">" +
                    "<email-addresses></email-addresses></user>")
                    .contentType("text/xml")
                    .expect()
                    .statusCode(400).body(is("bad request"))
                    .when().put("/api/currentuser");

    }
}
