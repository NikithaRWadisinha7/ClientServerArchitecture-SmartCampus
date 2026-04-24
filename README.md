# Smart Campus Sensor & Room Management API

Name: Nikitha Randinu Wadisinha
IIT ID: 20231977
UOW ID: W2120201

A robust, Java-based RESTful Web Service designed to monitor and manage a Smart Campus environment. Developed as part of the Client-Server Architectures coursework, this API strictly adheres to Jakarta EE specifications and REST architectural constraints. It handles dynamic room allocation, IoT sensor registration, and real-time telemetry data collection using thread-safe, in-memory persistence.

## Project Overview

This API serves as the backend infrastructure for a campus management system. Core capabilities include:
- **Container-Managed JAX-RS:** Fully deployable on Apache Tomcat (unlike embedded server implementations).
- **HATEOAS-Driven Discovery:** Dynamic root endpoint for client-side API navigation.
- **Sub-Resource Locators:** Clean delegation of hierarchical data (e.g., routing readings through their parent sensors).
- **Thread-Safe State Management:** Concurrent memory structures preventing race conditions during parallel requests.
- **Semantic Error Masking:** Custom exception mappers intercepting domain errors and system crashes to return sanitized, structured JSON payloads.

## Build and Deployment Instructions

Unlike standalone applications, this project is packaged as a Web Archive (`.war`) for deployment on a Servlet Container.

### Prerequisites
- Java Development Kit (JDK) 11
- Apache Maven 3.8+
- Apache Tomcat 10.1.x
- Apache Netbeans IDE 

### Deployment via Terminal

Navigate to the root directory containing the `pom.xml` and execute:
```bash
# Clean the target directory and compile the WAR file:
mvn clean install
```
Deploy the resulting `SmartCampus-1.0-SNAPSHOT.war` file to your Tomcat `webapps` directory and start the server.

### Deployment via NetBeans

1. Open the project in Apache NetBeans.
2. Right-click the project node and select **Clean and Build**.
3. Right-click the project node and select **Run** to automatically deploy to your configured Tomcat instance.

## API Environment

By default, Tomcat deploys the application using the project name as the context path. 

**Base URL:**

```
http://localhost:8080/SmartCampus/api/v1
```

## Endpoint Summary

### System Discovery

- `GET /` — Retrieves API metadata and dynamic routing links.

### Room Subsystem

- `GET /rooms` — Retrieves a collection of all registered rooms.
- `POST /rooms` — Provisions a new room in the system.
- `DELETE /rooms/{roomId}` — Removes a room (enforces referential integrity checks).

### Sensor Subsystem

- `GET /sensors` — Retrieves sensors, with optional `?type=` query parameter filtering.
- `POST /sensors` — Registers a new sensor and links it to a room.
- `POST /sensors/{sensorId}/readings` — Sub-resource endpoint to record a new telemetry reading.

## API Interaction Examples

All requests below assume the server is running on `localhost:8080`.

### 1. API Discovery (HATEOAS)

**Request:**

```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/
```
**Response (200 OK):**

```json
{
  "api_version": "1.0",
  "description": "Smart Campus Sensor & Room Management API",
  "developer_contact": "W2120201@westminster.ac.uk",
  "endpoints": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

### 2. Provision a New Room

**Request:**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Advanced Computing Lab",
    "capacity": 35
  }'
```
**Response (201 Created):**

```json
{
  "id": "a1b2c3d4-5678-90ab-cdef-1234567890ab",
  "name": "Advanced Computing Lab",
  "capacity": 35,
  "sensorIds": []
}
```

### 3. Register a New Sensor

**Request:**

```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "a1b2c3d4-5678-90ab-cdef-1234567890ab",
    "type": "CO2"
  }'
```
**Response (201 Created):**

```json
{
  "id": "f9e8d7c6-5432-10fe-dcba-0987654321fe",
  "roomId": "a1b2c3d4-5678-90ab-cdef-1234567890ab",
  "type": "CO2",
  "status": "ACTIVE",
  "currentValue": 0.0
}
```

### 4. Record a Telemetry Reading
**Request:**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/f9e8d7c6-5432-10fe-dcba-0987654321fe/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 415.5,
    "timestamp": "2026-04-23T10:00:00Z"
  }'
```
**Response (201 Created):**
```json
{
  "id": "11223344-5566-7788-9900-aabbccddeeff",
  "value": 415.5,
  "timestamp": "2026-04-23T10:00:00Z"
}
```
*Note: This operation automatically updates the `currentValue` of the parent sensor object.*

### 5. Filtered Collection Retrieval
**Request:**
```bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=CO2"
```
**Response (200 OK):**
```json
[
  {
    "id": "f9e8d7c6-5432-10fe-dcba-0987654321fe",
    "roomId": "a1b2c3d4-5678-90ab-cdef-1234567890ab",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 415.5
  }
]
```

## Exception Handling and Edge Cases

The system replaces default Tomcat HTML error pages with strict JSON responses to ensure seamless client consumption.

### Case A: Semantic Validation Failure
Attempting to assign a sensor to a non-existent `roomId` triggers a validation check.
**Response (422 Unprocessable Entity):**
```json
{
  "errorCode": 422,
  "errorType": "Unprocessable Entity",
  "errorMessage": "Cannot create sensor. Room ID invalid-room-99 does not exist."
}
```

### Case B: Referential Integrity Violation
Attempting to delete a room that still contains active sensors triggers a conflict.
**Response (409 Conflict):**
```json
{
  "errorCode": 409,
  "errorType": "Conflict",
  "errorMessage": "Cannot delete Room a1b2c3d4... It still contains active sensors."
}
```

### Case C: Resource State Restriction
Attempting to record a reading for a sensor whose status is set to `MAINTENANCE`.
**Response (403 Forbidden):**
```json
{
  "errorCode": 403,
  "errorType": "Forbidden",
  "errorMessage": "Cannot add reading. Sensor is currently in maintenance."
}
```

### Case D: Global System Failure
Any unhandled Java exception (e.g., NullPointerException) is caught by `GenericExceptionMapper`. This prevents raw stack traces from leaking to the client, securing internal system architecture from potential reconnaissance.
**Response (500 Internal Server Error):**
```json
{
  "errorCode": 500,
  "errorType": "Internal Server Error",
  "errorMessage": "An unexpected system error occurred. Our team has been notified."
}
```

## Architectural and Implementation Notes

### Thread-Safe Persistence Strategy
To accommodate the JAX-RS default Request-scoped lifecycle (where Resource classes are instantiated and destroyed per request), data is delegated to a static `MockDatabase` class. Because Apache Tomcat serves requests via a multi-threaded pool, standard collections were avoided. The system utilizes `ConcurrentHashMap` for all entities, locking data at the bucket level rather than the entire map. This guarantees high-throughput parallel processing without race conditions or memory corruption.

### Sub-Resource Locator Pattern
The API adheres strictly to the Single Responsibility Principle. The URI path `/sensors/{sensorId}/readings` is intercepted by the `SensorResource`, which then dynamically instantiates and delegates the HTTP handling to `SensorReadingResource`. This prevents the parent class from swelling into a monolithic structure while properly representing the nested relationship of the domain models.

### Semantic Status Codes
The use of **422 Unprocessable Entity** instead of 404 Not Found for invalid foreign keys (`roomId` missing during sensor creation) ensures HTTP semantic accuracy. The endpoint URI exists, and the payload is syntactically valid JSON, making a 400 or 404 incorrect. The 422 specifically informs the consumer that the logical references within the entity are unprocessable.

## Technology Stack Breakdown

- **Runtime Environment:** Java SE 11+
- **Application Server:** Apache Tomcat 10.1.x
- **REST Specification:** Jakarta EE 10
- **Implementation Provider:** Jersey 3.1.5 (jersey-container-servlet)
- **JSON Binding:** Jackson (jersey-media-json-jackson)
- **Dependency Management:** Maven 3.8.1+
