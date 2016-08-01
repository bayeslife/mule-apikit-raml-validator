package bayeslife;

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.component.JavaComponent;
import org.mule.api.lifecycle.Callable;
import org.mule.api.processor.MessageProcessor;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.component.DefaultJavaComponent;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.construct.Flow;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.object.PrototypeObjectFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ConsoleMain  {

    public static void main(String args[]) throws Exception {

        String ramlPath=args[0];
        String method=args[1];
        String resourcepath=args[2];
        String sampleRequestPath=args[3];


        String sampleRequest = readFile(sampleRequestPath,Charset.defaultCharset());
        //String sampleRequest = "{\"username\":\"gbs\",\"firstName\":\"george\",\"lastName\":\"bernard shaw\",\"emailAddresses\":[\"gbs@ie\"]}";
        //String sampleRequest = "{\"username\":\"gbs\",\"firstName\":\"george\",\"firstName2\":\"george\",\"lastName\":\"bernard shaw\",\"emailAddresses\":[\"gbs@ie\"]}";
        //String sampleRequest = "{\"username2\":\"gbs\"}";

        String generatedFlow = generateFlow(method,resourcepath);
        String generatedApiFlow = generateApiKitFlow("/tmp/api.yaml");

        String allFlows = generatedApiFlow+","+generatedFlow;

        DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        SpringXmlConfigurationBuilder configBuilder = new SpringXmlConfigurationBuilder(allFlows);
        MuleContext muleContext = muleContextFactory.createMuleContext(configBuilder);

        muleContext.start();

        Flow f = (Flow)muleContext.getRegistry().lookupFlowConstruct("entry");
        DefaultMuleMessage m = new DefaultMuleMessage(sampleRequest, muleContext);
        MuleEvent me = new DefaultMuleEvent(m, MessageExchangePattern.REQUEST_RESPONSE,null, f);

        m.setInboundProperty("host","host:80");
        m.setInboundProperty("http.request.path",resourcepath);
        m.setInboundProperty("http.listener.path","");
        m.setInboundProperty("http.method",method.toUpperCase());
        m.setInboundProperty("accept","application/json");
        m.setInboundProperty("content-type","application/json");

        try {
            MuleEvent res = f.process(me);
        }catch(Exception e){
            //failed and  need to get helpful error message
            System.exit(1);
            throw e;
        }

        muleContext.stop();
        System.exit(0);
        //success
    }

    static private String generateFlow(String method,String resourcepath) throws Exception {
        String flowxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<mule xmlns=\"http://www.mulesoft.org/schema/mule/core\"\n"+
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
                "xmlns:http=\"http://www.mulesoft.org/schema/mule/http\"\n"+
                "xmlns:json=\"http://www.mulesoft.org/schema/mule/json\"\n"+
                "xmlns:apikit=\"http://www.mulesoft.org/schema/mule/apikit\"\n"+
                "xmlns:spring=\"http://www.springframework.org/schema/beans\"\n"+
                "xsi:schemaLocation=\"http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd\n"+
                "http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd\n"+
                "http://www.mulesoft.org/schema/mule/json http://www.mulesoft.org/schema/mule/json/current/mule-json.xsd\n"+
                "http://www.mulesoft.org/schema/mule/apikit http://www.mulesoft.org/schema/mule/apikit/current/mule-apikit.xsd\n"+
                "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd\">\n"+
                "<flow name=\""+method+":"+resourcepath+"\">\n"+
                "<component class=\"bayeslife.MyComponent\"/>\n"+
                "<json:json-to-object-transformer returnClass=\"bayeslife.User\"/>"+
                "<component class=\"bayeslife.MyComponent\"/>\n"+
                "</flow>\n"+
                "</mule>";

        File f = File.createTempFile("test",".xml");
        FileWriter fw = new FileWriter(f);
        fw.write(flowxml);
        fw.flush();
        return f.getAbsolutePath();
    }

    static private String generateApiKitFlow(String raml) throws Exception {
        String flowxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<mule xmlns=\"http://www.mulesoft.org/schema/mule/core\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xmlns:vm=\"http://www.mulesoft.org/schema/mule/vm\"\n" +
                "xmlns:apikit=\"http://www.mulesoft.org/schema/mule/apikit\"\n" +
                "xmlns:spring=\"http://www.springframework.org/schema/beans\"\n" +
                "xsi:schemaLocation=\"http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd\n" +
                "http://www.mulesoft.org/schema/mule/vm http://www.mulesoft.org/schema/mule/vm/current/mule-vm.xsd\n" +
                "http://www.mulesoft.org/schema/mule/apikit http://www.mulesoft.org/schema/mule/apikit/current/mule-apikit.xsd\n" +
                "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd\">\n" +
                "<apikit:config name=\"salesApi\" raml=\"" + raml + "\" consoleEnabled=\"false\" consolePath=\"console\"/>\n" +
                "<flow name=\"entry\">\n" +
                "<vm:inbound-endpoint address=\"vm://test\"></vm:inbound-endpoint>\n" +
                "<apikit:router/>\n" +
                "</flow>\n" +
                "</mule>";
        File f = File.createTempFile("test-apikit", ".xml");
        FileWriter fw = new FileWriter(f);
        fw.write(flowxml);
        fw.flush();
        return f.getAbsolutePath();
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        Resource resource = new ClassPathResource(path);

        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
