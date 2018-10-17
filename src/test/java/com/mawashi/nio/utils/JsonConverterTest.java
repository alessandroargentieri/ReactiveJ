package com.mawashi.nio.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class JsonConverterTest {

    JsonConverter jsonConverter = JsonConverter.getInstance();

    @Test
    public void test(){
        CustomObject o = new CustomObject(1, "hello world");
        String jsonString = jsonConverter.getJsonOf(o);
        assertEquals(jsonString, "{\"attribute1\":1,\"attribute2\":\"hello world\"}");
    }

    class CustomObject {
        private int attribute1;
        private String attribute2;

        public CustomObject(){}

        public CustomObject(int attribute1, String attribute2) {
            this.attribute1 = attribute1;
            this.attribute2 = attribute2;
        }

        public int getAttribute1() {
            return attribute1;
        }

        public void setAttribute1(int attribute1) {
            this.attribute1 = attribute1;
        }

        public String getAttribute2() {
            return attribute2;
        }

        public void setAttribute2(String attribute2) {
            this.attribute2 = attribute2;
        }
    }
}