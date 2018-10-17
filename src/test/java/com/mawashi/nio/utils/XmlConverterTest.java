package com.mawashi.nio.utils;

import org.junit.Test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.*;

public class XmlConverterTest {

    XmlConverter xmlConverter = XmlConverter.getInstance();

    @Test
    public void test(){
        CustomObject o = new CustomObject(1, "hello world");
        String xmlString = xmlConverter.getXmlOf(o);
        assertEquals(xmlString, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<customObject>\n" +
                "    <attribute1>1</attribute1>\n" +
                "    <attribute2>hello world</attribute2>\n" +
                "</customObject>\n");
    }

}

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
class CustomObject {
    @XmlElement
    private int attribute1;
    @XmlElement
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