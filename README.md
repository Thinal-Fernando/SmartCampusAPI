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
smart-campus-api/
├── pom.xml
└── src/main/java/com/smartcampusai/
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
1. Download the zip file of the code and unzip it
2. Open Apache NetBeans
3. Select File → Open Project
4. Choose the SmartCampusAPI project
5. Ensure Apache Tomcat server is configured and running
6. Right click the project
7. select -> Clean and Build
8. Then select -> Run <br>

The API server will start and be available on localhost:8080 (by default)

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
