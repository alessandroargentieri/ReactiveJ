# ReactiveJ

ReactiveJ is a very lightweight library to build Java reactive microservices
It's built on top of Java EE 7 spec and Servlet 3.1 spec, in order to guarantee a reactive and non-blocking IO.
It uses:

  - Jetty 9 as embedded non-blocking web-server
  - Gson to parse Json messages to Java objects and vice-versa
  - JAXB to parse Xml messages to Java objects and vice versa

## Create a skeleton app

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
    
    @Api(path = "/delete/todo/{id}", method = "GET", produces = "text/plain", description = "")
    private final Action deleteToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    @Api(path = "/get/todo", method = "GET", produces = "application/xml", description = "")
    private final Action getToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
    };
    
    @Api(path = "/get/todos", method = "GET", produces = "application/json", description = "")
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
  - create your MainApplication class (with the main method) and start the library, passing the server port and all the Endpoints files:
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
  - you have a fully working skeleton app! If you run the static void main, you'll get the app running on the port you specified and you get all the endpoint actions mapped on the paths you've defined in the ToDoEndpoints constructor.
  
## Useful methods  
  
  Now, in order to implement your logic, you can use some useful methods:
  
  ```sh
  Object getDataFromJsonBodyRequest(HttpServletRequest request, Class objectClass)
  ```
  Returns the input data from a body request and convert the json received in input into the class you want.
  
  ```sh
  Object getDataFromXmlBodyRequest(HttpServletRequest request, Class objectClass)
  ```
  Returns the input data from a body request and convert the xml received in input into the class you want.
  
  ```sh
  Map<String, String> getPathVariables(HttpServletRequest request)
  ```
  Returns the map of the path variables included in the url path.
  
  ```sh
  void toJsonResponse(HttpServletRequest request, HttpServletResponse response, Object output)
  ```
  Pass the output object to the response and convert it into a Json message.
  
  ```sh
  void toXmlResponse(HttpServletRequest request, HttpServletResponse response, Object output)
  ```
  Pass the output object to the response and convert it into a Xml message.
  
  ```sh
  void toTextResponse(HttpServletRequest request, HttpServletResponse response, Object output)
  ```
  Pass the output object to the response and convert it into a Text message.
  
  ## Develop a Blocking app
  
  If you want to use a blocking approach (even if the server and the library are non-blocking) you just have to use the above methods and implement the logic you need.
  For example you can:
  
  - inject (or instantiate) your services and implement the logic:
  ```java
  package com.mypackage.example;
  
  import com.mypackage.example.entities.ToDo;
  import com.mypackage.example.service.ToDoService;
  
  public class ToDoEndpoints extends Endpoints {
  
    //inject or instatiate your service for the business logic
    ToDoService myService = new ToDoServiceImpl();
    
    @Api(path = "/create/todo", method = "POST", consumes = "application/json", produces = "application/json", description = "")
    private final Action createToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      /* let's suppose we have the input as a Json message */
      ToDo input = (ToDo) getDataFromJsonBodyRequest(request, ToDo.class);
      ToDo output = myService.create(input);
      /* let's suppose we want the output as a Json message */
      toJsonResponse(request, response, output);
    };
    
    @Api(path = "/delete/todo/{id}", method = "GET", produces = "text/plain", description = "")
    private final Action deleteToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      /* let's suppose we get the Id from the url path */
      Map pathVariablesMap = getPathVariables(request);
      String id = pathVariablesMap.get("id");
      myService.delete(id);
      /* let's suppose we want the output as a Text message */
      toTextResponse(request, response, "The " + id + " todo has been deleted. ");
    };
    
    @Api(path = "/get/todo", method = "GET", produces = "application/xml", description = "")
    private final Action getToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      /* let's suppose we get the Id from the query string */
      String id = request.getParameter("id");
      ToDo output = myService.getFromId(id);
      /* let's suppose we want the output as a Xml message */
      toXmlResponse(request, response, output);
    };
    
    @Api(path = "/get/todos", method = "GET", produces = "application/json", description = "")
    private final Action getAllToDos = (HttpServletRequest request, HttpServletResponse response) -> {
      //implement logic here
      List<ToDo> output = myService.getAll();
      /* let's suppose we want to set an header to the response */
      response.setHeader("myKey", "myValue");
      /* let's suppose we want the output as a Json message */
      toJsonResponse(request, response, output);
    };
    
    /* in the constructor we map each Action to a specific url path */
    public ToDoEndpoints(){
       setEndpoint("/create/todo", createToDo);
       setEndpoint("/delete/todo", deleteToDo);
       setEndpoint("/get/todo/{id}", getToDo);
       setEndpoint("/get/todo", getAllToDos);
    }
  }
  ````
    
  ## Develop a Reactive (Non-Blocking) app  
  
  If you want all your stack to be reactive and non-blocking you just have to choose and add the reactive library you prefer, to let your business logic to be non-blocking and asynchronous.
  Let's assume we would like to use RxJava2, so you can add the dependency in your pom.xml file if you are using Maven or into your .gradle file if you are using Gradle.
  
  - maven dependency for RxJava2:
  
 ```xml
 <dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.2</version>
 </dependency>
 ```
 
 -gradle dependency for RxJava2:
 ```json
 compile 'io.reactivex.rxjava2:rxjava:2.2.2'
 ```
 Let's assume you want to use Flowable class because it's reactive and implements the backpressure, then we can change the ToDoEndpoints class as follows:
 ```java
  package com.mypackage.example;
  
  import com.mypackage.example.entities.ToDo;
  import com.mypackage.example.service.ToDoService;
  
  public class ToDoEndpoints extends Endpoints {
  
    //inject or instatiate your service for the business logic
    ToDoService myService = new ToDoServiceImpl();
    
    @Api(path = "/create/todo", method = "POST", consumes = "application/json", produces = "application/json", description = "")
    private final Action createToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      /* let's assume we have the input as a Json message
         we want the output as a Json message
         and myService returns a Flowable/Observable object
      */   
      Flowable.just(request)
              .map(req -> (ToDo) getDataFromJsonBodyRequest(req, ToDo.class))
              .flatMap(input -> myService.createToDoReactively(input))
              .subscribe(output -> toJsonResponse(request, response, output));
    };
    
    @Api(path = "/delete/todo/{id}", method = "GET", produces = "text/plain", description = "")
    private final Action deleteToDo = (HttpServletRequest request, HttpServletResponse response) -> {
       /*  let's suppose we get the Id from the url path,
           we want the success or error message by text,
           myService is reactive
       */
       Flowable.just(request)
              .map(req -> getPathVariables(request).get("id"))
              .flatMap(id -> myService.deleteToDoReactively(id))
              .subscribe(() -> toTextResponse(request, response, "ToDo deleted successfully"),
                          t -> toTextResponse(request, response, "Something went wrong: " + t);
              );
     };
    
    @Api(path = "/get/todo", method = "GET", produces = "application/xml", description = "")
    private final Action getToDo = (HttpServletRequest request, HttpServletResponse response) -> {
      /*   let's suppose we get the Id from a query string,
           we want the output as a Xml message,
           myService is reactive
       */
      Flowable.just(request)
              .flatMap( req -> myService.getToDoReactively(req.getParameter("id")))
              .subscribe(output -> toXmlResponse(request, response, output));
    };
    
    @Api(path = "/get/todos", method = "GET", produces = "application/json", description = "")
    private final Action getAllToDos = (HttpServletRequest request, HttpServletResponse response) -> {
      /*   let's suppose we want the output as a Json message,
           myService is reactive
       */
      Flowable.just(request)
              .flatMap(req -> myService.getAllToDosReactively())
              .subscribe(outputList -> {  response.setHeader("myKey", "myValue");
                                          toJsonResponse(request, response, outputList);             
                                       },
                         throwable ->  toJsonResponse(request, response, throwable);              
              );
    };
    
    /* in the constructor we map each Action to a specific url path */
    public ToDoEndpoints(){
       setEndpoint("/create/todo", createToDo);
       setEndpoint("/delete/todo", deleteToDo);
       setEndpoint("/get/todo/{id}", getToDo);
       setEndpoint("/get/todo", getAllToDos);
    }
  }
  ````
 
 Now we have a full reactive web application, because either the server, either the logic is non-blocking.
 
