package reactivej.mawashi.nio.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * @author alessandroargentieri
 *
 * this class encapsulate the explicit and classic use of org.eclipse.jetty.server.Server libraries
 */
public class Jetty {

    public enum Dispatch {ALL, ASYNC, DEFAULT, ERROR, FORWARD, INCLUDE, REQUEST};

    private int port = 8080;
    private ServletHandler servletHandler= new ServletHandler();

    public Jetty port(int port){
        this.port = port;
        return this;
    }

    public Jetty servlet(Class servletClass, String path){
        servletHandler.addServletWithMapping(servletClass, path);
        return this;
    }

    public Jetty filter(Class filterClass, String path, Dispatch dispatch){
        int dispatchType = 0;
        switch (dispatch) {
            case ALL:
                dispatchType = FilterMapping.ALL;
                break;
            case ASYNC:
                dispatchType = FilterMapping.ASYNC;
                break;
            case DEFAULT:
                dispatchType = FilterMapping.DEFAULT;
                break;
            case ERROR:
                dispatchType = FilterMapping.ERROR;
                break;
            case FORWARD:
                dispatchType = FilterMapping.FORWARD;
                break;
            case INCLUDE:
                dispatchType = FilterMapping.INCLUDE;
                break;
            case REQUEST:
                dispatchType = FilterMapping.REQUEST;
                break;
        }
        servletHandler.addFilterWithMapping(filterClass, path, dispatchType);
        return this;
    }

    public void start() throws Exception {
        Server server = new Server(port);
        server.setHandler(servletHandler);
        server.start();
        server.join();
    }
}
