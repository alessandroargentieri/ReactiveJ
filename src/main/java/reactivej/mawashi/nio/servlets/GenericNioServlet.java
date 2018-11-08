package reactivej.mawashi.nio.servlets;

import reactivej.mawashi.nio.utils.RestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * @author alessandroargentieri
 *
 * Dispatcher Non-Blocking servlet which calls the endpoints defined by the programmer through the Action
 */
public class GenericNioServlet extends HttpServlet {

    private final static Logger log = Logger.getLogger(GenericNioServlet.class.getName());

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.startAsync();
        log.info(LocalDateTime.now()+" - " + this.getClass().getSimpleName() + " - start Async context from http request: " + request.getRequestURI());
        try{
            RestHandler.getInstance().getEndpointIfMatches(request.getRequestURI()).act(request, response);
        } catch (Exception e){
            log.info(LocalTime.now() + ": Internal Server Error (code 500) " + e.toString());
            request.setAttribute("internal-server-error", e.toString());
            RestHandler.getInstance().getEndpointIfMatches("/internal-server-error").act(request, response);
        }
    }

}
