/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package bayeslife;

import com.jayway.restassured.RestAssured;
import org.junit.Rule;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ValidateAgainstSchemaTestCase extends FunctionalTestCase
{
    @Rule
    public DynamicPort serverPort = new DynamicPort("serverPort");


    @Override
    public int getTestTimeoutSecs()
    {
        return 6000;
    }

    @Override
    protected void doSetUp() throws Exception
    {
        RestAssured.port = serverPort.getNumber();
        super.doSetUp();
    }

    @Override
    public String getConfigResources()
    {
        return "http-listener-validation-flow.xml,simpleResource-flow.xml";
    }

    @Test
    public void putValidJson() throws Exception {

        given().body("{\"simple\":\"100.00\"}")
                .contentType("application/json")
                .expect()
                .statusCode(200).body(is(""))
                .when().post("/simpleResource/1234");

    }

    @Test
    public void putInValidCurrency() throws Exception {

        given().body("{\"simple\":\"100.000\"}")
                .contentType("application/json")
                .expect()
                .statusCode(500)
                .when().post("/simpleResource");

    }

}
