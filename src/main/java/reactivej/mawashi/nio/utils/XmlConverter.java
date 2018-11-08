package reactivej.mawashi.nio.utils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author alessandroargentieri
 *
 * This class encapsulates the logic of conversion from and to Xml
 * using JAXB library.
 * @param <T>
 */
public class XmlConverter<T> {

    private static XmlConverter instance = null;
    public static XmlConverter getInstance(){
        if(instance == null){
            instance = new XmlConverter();
        }
        return instance;
    }

    public Object getDataFromBodyRequest(final HttpServletRequest request, final Class clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
            sb.append(s);
        }
        return JAXB.unmarshal(new StringReader(sb.toString()), clazz);
    }

    public String getXmlOf(final Object object){
        StringWriter sw = new StringWriter();
        JAXB.marshal(object, sw);
        return sw.toString();
    }


}
