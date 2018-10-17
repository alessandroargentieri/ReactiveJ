package com.mawashi.nio.utils;

import com.mawashi.nio.annotations.Api;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author alessandroargentieri
 *
 * Singleton class which contains all methods and data regarding Endpoints, Paths,
 * Non-Blocking Output conversion and representation
 */
public class RestHandler {

    private final static Logger log = Logger.getLogger(RestHandler.class.getName());

    /* Singleton Design pattern implementation */
    private static RestHandler instance = null;
    public static RestHandler getInstance(){
        if(instance == null){
            log.info(LocalTime.now() + ": creation of RestHandler");
            instance = new RestHandler();
        }
        return instance;
    }

    private final List<Endpoint> endpointList = new ArrayList<>();

    @Api(path = "/not-found", method = "", consumes = "", produces = "", description = "Error action")
    private final Action notFoundError = (HttpServletRequest request, HttpServletResponse response) -> {
        toJsonResponse(request, response, new Error(404, request.getRequestURI(), "Page not found"));
    };
    @Api(path = "/internal-server-error", method = "", consumes = "", produces = "", description = "Internal server error")
    private final Action internalServerError = (HttpServletRequest request, HttpServletResponse response) -> {
        toJsonResponse(request, response, new Error(500, request.getRequestURI(), request.getAttribute("internal-server-error").toString()));
    };
    @Api(path = "/status", method = "", consumes = "", produces = "", description = "Check health")
    private final Action checkHealth = (HttpServletRequest request, HttpServletResponse response) -> {
        toJsonResponse(request, response, "{ \"status\": \"ok\"}");
    };

    private RestHandler(){
        endpointList.add(new Endpoint("/not-found", notFoundError));
        endpointList.add(new Endpoint("/internal-server-error", internalServerError));
        endpointList.add(new Endpoint("/status", checkHealth));
    }


    public final synchronized RestHandler setEndpoint(final String path, final Action action){
        final Endpoint endpoint = new Endpoint(path, action);
        endpointList.add(endpoint);
        log.info(LocalTime.now() + ": added action to path " + path);
        return this;
    }

    public final Action getEndpointIfMatches(final String path){
        List<Endpoint> endpoints = endpointList.stream().filter(end -> urlMatch(path, end.getPath())).limit(1).collect(Collectors.toList());
        return (endpoints.size()==1) ? endpoints.get(0).getAction(): getEndpointIfMatches("/not-found") ;
    }

    private boolean urlMatch(String requestUrl, String endpointUrl){

        if(!requestUrl.startsWith("/")) requestUrl = "/"+requestUrl;
        if(!endpointUrl.startsWith("/")) endpointUrl = "/"+endpointUrl;

        if(requestUrl.endsWith("/")) requestUrl = requestUrl.substring(0, requestUrl.length()-1);
        if(endpointUrl.endsWith("/")) endpointUrl = endpointUrl.substring(0, endpointUrl.length()-1);

        String[] split1 = requestUrl.split("/");
        String[] split2 = endpointUrl.split("/");
        if(split1.length != split2.length) return false;

        for(int i=0; i<split1.length; i++){
            String chunck1 = split1[i];
            String chunck2 = split2[i];

            if(!chunck1.equals(chunck2)){
                if(!chunck2.startsWith("{") && !chunck2.endsWith("}")) return false;
            }
        }
        return true;
    }

    public Map<String, String> getPathVariables(final String path){
        List<Map<String,String>> pathVariablesListMap = endpointList.stream()
                    .filter(end -> urlMatch(path, end.getPath()))
                    .limit(1)
                    .map(end -> end.getPath())
                    .map(p -> extractPathVariables(p, path)).collect(Collectors.toList());
        return (!pathVariablesListMap.isEmpty()) ? pathVariablesListMap.get(0) : new HashMap<String, String>();
    }

    private Map<String, String> extractPathVariables(String endpointPath, String requestPath){

        Map<String, String> pathVariables = new HashMap<>();

        if(!requestPath.startsWith("/")) requestPath = "/"+requestPath;
        if(!endpointPath.startsWith("/")) endpointPath = "/"+endpointPath;

        if(requestPath.endsWith("/")) requestPath = requestPath.substring(0, requestPath.length()-1);
        if(endpointPath.endsWith("/")) endpointPath = endpointPath.substring(0, endpointPath.length()-1);

        String[] split1 = requestPath.split("/");
        String[] split2 = endpointPath.split("/");

        for(int i=0; i<split1.length; i++){
            String chunck1 = split1[i];
            String chunck2 = split2[i];

            if(!chunck1.equals(chunck2)){
                if(chunck2.startsWith("{") && chunck2.endsWith("}")) {
                    String paramName = chunck2.replaceAll("\\{","")
                           .replaceAll("\\}","")
                           .replaceAll(" ", "");
                    pathVariables.put(paramName, chunck1);
                }
            }
        }
        return pathVariables;
    }


    public void toJsonResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp) throws IOException {
        response.setContentType("application/json");
        nioResponse(request, response, JsonConverter.getInstance().getJsonOf(resp));
    }
    public void toXmlResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp) throws IOException {
        response.setContentType("application/xml");
        nioResponse(request, response, XmlConverter.getInstance().getXmlOf(resp));
    }
    public void toTextResponse(final HttpServletRequest request, final HttpServletResponse response, final Object resp) throws IOException {
        response.setContentType("text/plain");
        nioResponse(request, response, resp.toString());
    }

    private void nioResponse(final HttpServletRequest request, final HttpServletResponse response, final String resp) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        final ByteBuffer finalContent = ByteBuffer.wrap(resp.getBytes());
        final AsyncContext async = request.getAsyncContext();
        final ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {

            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!finalContent.hasRemaining()) {
                        response.setStatus(200);
                        async.complete();
                        log.info(LocalDateTime.now()+" - " + this.getClass().getSimpleName() + " - close Async context from http request: " + request.getRequestURI());
                        return;
                    }
                    out.write(finalContent.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.info(LocalDateTime.now().toString()+" | "+this.getClass().getSimpleName()+":"+t.toString());
                async.complete();
            }
        });
    }

}
