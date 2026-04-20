# Smart Campus Sensor & Room Management API
module **5COSC022W** – Client-Server Architectures <br>
Coursework - REST API Design and Implementation using JAX-RS

---

## 1. API Overview
This project implements a RESTful API for managing smart campus infrastructure, specifically rooms, sensors, and sensor readings.

The API allows administrators and systems to:

* Register and manage rooms across the campus
* Register sensors and associate them with rooms
* Record historical sensor readings
* Retrieve filtered sensor information
* Maintain system integrity through validation and error handling

The system follows the REST architectural style, where resources are accessed through standard HTTP methods such as:

* GET – retrieve resources
* POST – create resources
* DELETE – remove resources

The API is implemented using JAX-RS (Jersey) and deployed on Apache Tomcat.

<br>

### API Routes

Base API URL - `http://localhost:8080/SmartCampusAPI/api/v1`

<br>

| Resource | Base Path |
|---|---|
| Discovery Endpoint | `GET /api/v1` |
| Get all rooms | `GET /rooms` |
| Create room | `POST /rooms` |
| Get Room by ID | `GET /rooms/{roomId}` |
| Delete room | `DELETE /rooms/{roomId}` |
| Get All Sensors | `GET /sensors` |
| Filter Sensors by Type | `GET /sensors?type=CO2` |
| Create Sensor | `POST /sensors` |
| Get Sensor Readings | `GET /sensors/{sensorId}/readings` |
| Add Sensor Readings | `POST /sensors/{sensorId}/readings` |

<br>

### Exception Mapping
| Exception | HTTP Code | Scenario |
|---|---|---|
| DataNotFoundException |404 | Resource not found | 
| RoomNotEmptyException | 409 | Cannot delete room with sensors | 
| LinkedResourceNotFoundException | 422 | Invalid room reference | 
| SensorUnavailableException | 403 | Sensor under maintenance |
| GlobalExceptionMapper | 500 | Unexpected server error |

<br>

### Project Structure
```
SmartCampusAPI/
├── pom.xml
└── src/main/java/com/smartcampusapi/
    ├── config/
    │   └── SmartCampusApplication.java
    │                        
    ├── exceptions/
    │   ├── DataNotFoundException.java
    │   ├── DataNotFoundExceptionMapper.java
    │   ├── GlobalExceptionMapper.java     
    │   ├── LinkedResourceNotFoundException.java
    │   ├── LinkedResourceNotFoundExceptionMapper.java
    │   ├── RoomNotEmptyException.java
    │   ├── RoomNotEmptyExceptionMapper.java  
    │   ├── SensorUnavailableException.java
    │   └── SensorUnavailableExceptionMapper.java
    │
    ├── filter/
    │   └── LoggingFilter.java
    │
    ├── model/
    │   ├── ErrorMessage.java
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    │
    ├── resource/
    │   ├── DiscoveryResource.java     
    │   ├── SensorReadingResource.java
    │   ├── SensorResource.java         
    │   └── SensorRoomResource.java
    │           
    └── store/
        └── DataStore.java          
```
<hr>

<br>

## 2. How to Build & Run the Project
1. Clone the repository using:
   ```git clone https://github.com/Thinal-Fernando/SmartCampusAPI.git```
2. Open in Apache NetBeans
   * File -> Open Project
   * Navigate to the cloned folder -> Click Open
3. Ensure Apache Tomcat server is configured and running
4. Build the Project
   * Right-click the project
   * select -> Clean and Build
   * Then select -> Run <br>

The API will start at:

`http://localhost:8080/SmartCampusAPI/api/v1`  

<hr>

<br>

## 3. Sample cURL Commands

1. Discovery Route
```
curl http://localhost:8080/SmartCampusAPI/api/v1
```

2. Get All Rooms
```
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

3. Create A Room
```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"SCI-101\",\"name\":\"Science Lab\",\"capacity\":50}"
```

4. Create a sensor
```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"CO2-004\",\"type\":\"CO2\",\"roomId\":\"LAB-101\",\"status\":\"ACTIVE\"}"
```

5. Filter Sensors by type
```
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2
```

6. Post a Sensor Reading
```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings -H "Content-Type: application/json" -d "{\"id\":\"R-001\",\"timestamp\":1712745000000,\"value\":450}"
```

7. Get all readings for a sensor
```
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings
```

8. Trigger 409 – delete a room that still has sensors
```
curl -s -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

9. Trigger 422 – create sensor with non-existent roomId
```
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"FAKE-001\",\"type\":\"Temperature\",\"roomId\":\"DOES-NOT-EXIST\"}"
```

10. Trigger 403 – post reading to a MAINTENANCE sensor
```
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/OCC-001/readings -H "Content-Type: application/json" -d "{\"id\":\"R-002\",\"timestamp\":1712745000000,\"value\":5.0}"
```

<hr>

<br>

## 4. Report - Answers to Coursework Questions

### Part 1: Service Architecture & Setup
**Q1.1  Project & Application Configuration**

In JAX-RS, resource classes are typically instantiated per request by default. This means that every incoming HTTP request creates a new instance of the resource class that handles that request. The JAX-RS runtime invokes the appropriate method on that instance and then discards it after the request is completed.
This lifecycle behaviour improves thread safety because each request is handled by a separate object instance, preventing shared mutable state inside the resource class. Unlike a singleton resource, which would share the same instance across multiple threads and require explicit synchronization, the per-request model avoids this issue overall. However, it also means that in-memory data structures used to store application data (such as HashMaps or ArrayLists) cannot be defined as instance variables inside the resource class because they would be recreated with every request, causing data loss.
To avoid this issue, shared data structures should be stored in static classes or centralized data stores, such as a dedicated DataStore class. This ensures that the data persists across multiple requests and remains accessible to all resource instances.
Since web servers often handle many requests simultaneously, shared data structures must also support safe concurrent access. Thread-safe collections such as ConcurrentHashMap are commonly used because they allow multiple threads to perform read and write operations without causing race conditions or data corruption. 
This design ensures that application data persists across requests while resource classes remain stateless, which aligns with the REST architectural constraint that each request should contain all the information needed for processing without relying on stored server-side session state.

<br>

**Q1.2 The "Discovery" Endpoint**

Hypermedia as the Engine of Application State (HATEOAS) is a core REST principle where responses include links that guide the client to related resources and actions. Instead of relying on external documentation, the API itself provides navigational information within responses.
For example, the discovery endpoint returns metadata along with links to primary resources such as rooms and sensors. This allows clients to dynamically explore the API by following links instead of hardcoding endpoint paths.
This approach provides several advantages:
First, it improves API discoverability. A client can retrieve the discovery endpoint and learn how to navigate the API without consulting external documentation.
Second, it increases flexibility and maintainability. If the API structure changes, clients can follow updated links provided by the server rather than relying on outdated hardcoded URLs. This reduces tight coupling between client and server, as clients do not depend on hardcoded URLs.
Third, it improves developer experience. Client developers can build applications that dynamically adapt to the API structure, reducing coupling between the client and server implementations.
Compared to static documentation, which can become outdated when API endpoints change, HATEOAS ensures that clients always receive up-to-date navigation links directly from the server. This makes the API more self-descriptive and easier to evolve over time.

<br> 

### Part 2: Room Management
**Q2.1 Room Resource Implementation**

Returning only room IDs significantly reduces the size of the response payload. This approach minimizes network bandwidth usage and improves performance, especially when a large number of rooms exist.
However, clients must then make additional requests to retrieve detailed information for each room. This increases the number of API calls and may reduce overall efficiency and performance.
Returning full room objects provides more complete information in a single response. Clients receive all relevant data such as room name, capacity, and associated sensors without needing additional requests.
The design trade-off therefore involves balancing bandwidth efficiency and payload size against client convenience and the number of network calls. Returning only IDs minimizes payload overhead but increases request overhead, whereas returning full objects increases payload size but reduces the number of client requests.
In this API, returning full room objects is appropriate because the room data structure is relatively small, meaning the additional payload overhead is minimal while significantly improving usability for client applications. However, when dealing with very large datasets, returning only IDs or summary objects may be more efficient.

<br>

**Q2.2 Room Deletion & Safety Logic**

Yes, in this implementation, the DELETE operation is idempotent.
An HTTP method is considered idempotent when performing the same request multiple times produces the same final state on the server.
In the Smart Campus API, deleting a room removes it from the in-memory data structure. If a client sends the same DELETE request again for the same room ID, the room will no longer exist. <br>
Therefore:
* If the room exists and contains no sensors, the room is removed from the system and a success response is returned.
* Subsequent DELETE requests have no additional effect on the system state.

Even though the responses may differ (successful deletion vs. not found), the final result remains the same: the room does not exist in the system.
And since the constraint is about side effects on state, not response uniformity, because repeated DELETE operations produce the same outcome without creating additional changes, the operation satisfies the definition of idempotency.

<br>

### Part 3: Sensor Operations & Linking
**Q3.1 Sensor Resource & Integrity**

The @Consumes(MediaType.APPLICATION_JSON) annotation specifies that the resource method only accepts JSON input. When a client sends a request with a different media type such as text/plain or application/xml, the JAX-RS runtime checks the request’s Content-Type header against the media types defined in the annotation.
If the Content-Type does not match the supported format, the server cannot deserialize the request body into the expected Java object. In this situation, JAX-RS automatically rejects the request and returns an HTTP 415 Unsupported Media Type response. This ensures that the server only processes requests in formats it understands and prevents invalid or incorrectly formatted data from entering the system.

<br>

**Q3.2 Filtered Retrieval & Search**

Filtering operations typically represent optional criteria applied to a collection resource. Query parameters are specifically designed for this purpose. Using query parameters such as ```/api/v1/sensors?type=CO2``` clearly communicates that the client is requesting a filtered subset of the sensors collection. In contrast, embedding the filter in the path such as ```/api/v1/sensors/type/CO2``` suggests that "type" is a sub-resource rather than a filtering condition.
Query parameters provide several advantages:
1.	They allow multiple filtering conditions to be combined easily. For example: ```/api/v1/sensors?type=CO2&status=ACTIVE```, The equivalent path approach ```/sensors/type/CO2/status/ACTIVE``` creates exponentially complex routing as filter combinations grow. 
2.	They keep the resource structure clean by preserving the main collection endpoint. As a single endpoint handles all type-filter combinations. The path approach requires defining a new route for every possible filter field, which becomes unmaintainable in large APIs.
3.	They follow common REST conventions used in widely adopted APIs.

For these reasons, query parameters are the preferred method for filtering and searching collections.

<br>

### Part 4: Deep Nesting with Sub - Resources
**Q4.1 The Sub-Resource Locator Pattern**

The Sub-Resource Locator pattern allows nested resources to be handled by separate classes rather than placing all logic inside a single large resource class.
In this API, sensor readings are accessed through the path:
```/api/v1/sensors/{sensorId}/readings```
Instead of implementing all reading logic inside the SensorResource class, a dedicated SensorReadingResource handles requests related to readings.
In this pattern, the sub-resource locator method returns an instance of the SensorReadingResource class. The JAX-RS runtime then uses this returned object to match and handle the remaining part of the request path. This allows request processing to be delegated dynamically at runtime, rather than defining all nested routes in a single resource class.
This design provides several advantages.
First, it improves separation of concerns. Each resource class focuses on its specific responsibility. SensorResource manages sensors, while SensorReadingResource manages readings.
Second, it improves maintainability. As the API grows, separating functionality into smaller classes prevents the resource layer from becoming overly complex.
Third, it improves scalability and readability. Developers can extend or modify nested resource functionality without modifying unrelated parts of the API.
Overall, the Sub-Resource Locator pattern results in a cleaner and more modular architecture.  Without this pattern, a single resource class could become excessively large and difficult to maintain as the API grows.

<br>

### Part 5: Advanced Error Handling, Exception Mapping & Logging
**Q5.1 Dependency Validation (422 Unprocessable Entity)**

HTTP 404 indicates that the requested resource does not exist at all. In the case of creating a sensor with a non-existent room ID, the request endpoint ```/api/v1/sensors``` is valid and exists. The issue is not that the endpoint is missing, but that the data inside the request body contains an invalid reference. HTTP 422 Unprocessable Entity is more appropriate because it indicates that the server understood the request format but could not process the contained instructions due to semantic errors.
Therefore, returning HTTP 422 correctly communicates that the request structure is valid but references a resource that does not exist.

<br>

**Q5.2 The Global Safety Net (500)**

Exposing raw Java stack traces in API responses is one of the most common and dangerous information vulnerabilities in web services. A single stack trace can provide an attacker with a detailed report about the target system. The specific information categories an attacker can harvest include:
* Technology stack and versions: Package and class names immediately reveal the full technology, the Java version in use, and any third-party libraries with their exact versions. The attacker can then cross-reference these versions to identify known, unpatched vulnerabilities specific to those exact library versions.

* Internal file paths: Stack traces commonly include absolute paths. These reveal the server's directory structure, the operating system, and deployment information. 


* Application logic and control flow: The call stack shows the exact sequence of method invocations that led to the error, including class names, method names, and line numbers. This exposes the internal architecture of the application, allowing an attacker to identify precisely where and how the code processes input and therefore where to inject malicious data. 

* Database and configurations: If an unhandled exception originates from a database driver, connection pool, or configuration loader, the stack trace may inadvertently expose connection string fragments, host names, port numbers, or configuration file paths containing secrets.

An attacker could use this information to identify vulnerabilities in the system or learn about the internal architecture of the application. Therefore, APIs should never expose raw stack traces. Instead, a generic error response should be returned to the client while the detailed error information is logged internally for debugging purposes.

<br>

**Q5.3 API Request & Response Logging Filters**

JAX-RS filters allow developers to intercept incoming requests and outgoing responses at the framework level. Using filters for logging offers several advantages compared to manually inserting logging statements inside every resource method. First, filters centralize logging logic in one place. This reduces code duplication and ensures consistent logging across the entire API. Second, filters automatically capture all requests and responses regardless of which resource handles them. This guarantees full observability without modifying each endpoint individually. Third, filters implement cross-cutting concerns, meaning functionality such as logging, authentication, and monitoring can be handled independently of business logic. Using a ContainerRequestFilter and ContainerResponseFilter ensures that every request and response is automatically logged, regardless of which resource method handles it. This design improves maintainability, keeps resource classes clean, and makes the application easier to extend in the future.





















