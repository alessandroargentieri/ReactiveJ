# ReactiveJ

ReactiveJ is a very lightweight library to build Java reactive microservices
It's built on top of Java EE 7 spec and Servlet 3.1 spec, in order to guarantee a reactive and non-blocking IO.
It uses:

  - Jetty 9 as embedded non-blocking web-server
  - Gson to parse Json messages to Java objects and vice-versa
  - JAXB to parse Xml messages to Java objects and vice versa

# How to use it

In order to use ReactiveJ and create your first Non-Blocking (asynchronous and reactive) Java microservice you can:

  - Set the dependency in your pom.xml file (if you are using Maven):
  
  ```xml
  <dependency>
    <groupId>com.github.alessandroargentieri</groupId>
    <artifactId>ReactiveJ</artifactId>
    <version>1.0.0</version>
  </dependency>
  ```
  
  - Or alternatively set the ReactiveJ dependency in your .gradle file if you're using Gradle:
  ```json
  compile 'com.github.alessandroargentieri:ReactiveJ:1.0.0'
  ```

  - start using the imported dependencies designing your Endpoints extending the **Endpoints** abstract class:
  ```java
  package com.mypackage.example;
  
  public class ToDoEndpoints extends Endpoints {
    public ToDoEndpoints(){}
  }
  ```
  - design your endpoints implementing the **Action** functional interface:
  ```java
  package com.mypackage.example;
  
  public class ToDoEndpoints extends Endpoints {
    
    @Api(path = "/create/todo", method = "POST", consumes = "application/json", produces = "application/json", description = "")
    private final Action createToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    @Api(path = "/delete/todo", method = "GET", produces = "text/plain", description = "")
    private final Action deleteToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    @Api(path = "/get/todo/{id}", method = "GET", produces = "application/xml", description = "")
    private final Action getToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    @Api(path = "/get/todo", method = "GET", produces = "application/json", description = "")
    private final Action getAllToDos = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    public ToDoEndpoints(){
       setEndpoint("/create/todo", createToDo);
       setEndpoint("/delete/todo", deleteToDo);
       setEndpoint("/get/todo/{id}", getToDo);
       setEndpoint("/get/todo", getAllToDos);
    }
  }
  ```
  - create your MainApplication class (with the main method) and start the library:
  ```java
  package com.mypackage.example;
  
  import com.mawashi.nio.ReactiveJ;
  
  public class MainApplication {

    public static void main(String[] args) throws Exception {
        new ReactiveJ().port(8081)                      //custom is 8080
                       .endpoints(new ToDoEndpoints())
                       .start();
    }
  }
  ```
  
  - inject (or instantiate) your services and implement the logic:
  ```java
  package com.mypackage.example;
  
  import com.mypackage.example.entities.ToDo;
  import com.mypackage.example.service.ToDoService;
  
  public class ToDoEndpoints extends Endpoints {
  
    ToDoService myService = new ToDoServiceImpl();
    
    @Api(path = "/create/todo", method = "POST", consumes = "application/json", produces = "application/json", description = "")
    private final Action createToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      ToDo input = (ToDo) getDataFromJsonBodyRequest(request, ToDo.class);
      ToDo output = myService.create(input);
      toJsonResponse(request, response, output);
    };
    
    @Api(path = "/delete/todo/{id}", method = "GET", produces = "text/plain", description = "")
    private final Action deleteToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      Map pathVariablesMap = getPathVariables(request);
      String id = pathVariablesMap.get("id");
      myService.delete(id);
      toTextResponse(request, response, "The " + id + " todo has been deleted. ");
    };
    
    @Api(path = "/get/todo/{id}", method = "GET", produces = "application/xml", description = "")
    private final Action getToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    @Api(path = "/get/todo", method = "GET", produces = "application/json", description = "")
    private final Action getAllToDos = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    public ToDoEndpoints(){
       setEndpoint("/create/todo", createToDo);
       setEndpoint("/delete/todo", deleteToDo);
       setEndpoint("/get/todo/{id}", getToDo);
       setEndpoint("/get/todo", getAllToDos);
    }
  }
  ````
    
    
