package com.mawashi.nio.jetty;

import com.mawashi.nio.servlets.GenericNioServlet;
import com.mawashi.nio.utils.Endpoints;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author alessandroargentieri
 *
 * This is the principal class of the library
 * It wraps Jetty embedded server, add servlets, filters and endpoints
 *
 *
 */
public class ReactiveJ {

    private final static Logger log = Logger.getLogger(ReactiveJ.class.getName());

    private int port;
    private List<Endpoints>    endpointsList  = new ArrayList<>();
    private Map<Class, String> servletsMap    = new HashMap<>();
    private List<FilterItem>   filterItemList = new ArrayList<>();

    public ReactiveJ port(int port){
        this.port = port;
        return this;
    }

    public ReactiveJ endpoints(Endpoints endpoints){
        endpointsList.add(endpoints);
        return this;
    }

    /* if you want to add some other custom Servlets */
    public ReactiveJ addServlet(Class servletClass, String path){
        this.servletsMap.put(servletClass, path);
        return this;
    }

    /* if you want to add some other custom Filters */
    public ReactiveJ addFilter(Class filterClass, String path, Jetty.Dispatch dispatch){
        this.filterItemList.add( new FilterItem(filterClass, path, dispatch));
        return this;
    }

    public void start() throws Exception {
      Jetty jetty = new Jetty().port(port).servlet(GenericNioServlet.class, "/*");
        if(!servletsMap.isEmpty()){
            for (Map.Entry<Class, String> entry : servletsMap.entrySet()) {
                jetty.servlet(entry.getKey(), entry.getValue());
                log.info(LocalDateTime.now()+": "+this.getClass().getSimpleName()+" | "+ "Added servlet " + entry.getKey() + "at path " + entry.getValue());
            }
        }
        if(!filterItemList.isEmpty()){
            for(FilterItem filter : filterItemList){
                jetty.filter(filter.getFilterClass(), filter.getPath(), filter.getDispatch());
                log.info(LocalDateTime.now()+": "+this.getClass().getSimpleName()+" | "+ "Added filter " + filter.getFilterClass() + "at path " + filter.getPath());
            }
        }
        jetty.start();
    }

}
