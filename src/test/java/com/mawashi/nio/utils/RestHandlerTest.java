package com.mawashi.nio.utils;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class RestHandlerTest {

    RestHandler handler = RestHandler.getInstance();
    Action action;

    @Before
    public void setUp(){
        action = new Action() {
            @Override
            public void act(HttpServletRequest request, HttpServletResponse response) throws IOException {
                //doNothing
            }
        };
        handler.setEndpoint("/path/{id}", action);
    }

    @Test
    public void get_endpoint_test(){
        assertEquals(handler.getEndpointIfMatches("/path/23"), action);
    }

    @Test
    public void test(){
        Map<String, String> pathMaps = handler.getPathVariables("/path/23");
        assertEquals(pathMaps.get("id"), "23");
    }

}