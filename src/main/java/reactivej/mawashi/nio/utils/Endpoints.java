package reactivej.mawashi.nio.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author alessandroargentieri
 *
 * This abstract class must be extended by the programmer in order to define Endpoints
 * The programmer must define various Action and then must add each endpoint in the constructor with Endpoints#setEndpoint
 * Each extended class must be set into the ReactiveJ#endpoints
 */
public abstract class Endpoints {

    protected RestHandler   handler       = RestHandler.getInstance();
    protected JsonConverter jsonConverter = JsonConverter.getInstance();
    protected XmlConverter  xmlConverter  = XmlConverter.getInstance();

    protected synchronized void setEndpoint(final String path, final Action action){
        handler.setEndpoint(path, action);
    }

    protected Object getDataFromJsonBodyRequest(final HttpServletRequest request, final Class clazz) throws IOException {
        return jsonConverter.getDataFromBodyRequest(request, clazz);
    }

    protected Object getDataFromXmlBodyRequest(final HttpServletRequest request, final Class clazz) throws IOException {
        return xmlConverter.getDataFromBodyRequest(request, clazz);
    }

    protected Map<String, String> getPathVariables(final HttpServletRequest request){
        return handler.getPathVariables(request.getRequestURI());
    }

    protected void toJsonResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp) throws IOException {
        handler.toJsonResponse(request, response, resp);
    }

    protected void toXmlResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp) throws IOException {
        handler.toXmlResponse(request, response, resp);
    }

    protected void toTextResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp) throws IOException {
        handler.toTextResponse(request, response, resp);
    }

    protected void toResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp, final String mimetype) throws IOException {
        handler.toResponse(request, response, resp, mimetype);
    }
}
