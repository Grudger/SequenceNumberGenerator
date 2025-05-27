# Tracking Number Generator
A Spring Boot application for managing tracking numbers for shipments with features for creating, retrieving, and filtering tracking information.
## Table of Contents
- [Overview](#overview)
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
    - [Get All Tracking Numbers](#get-all-tracking-numbers)
    - [Get Next Tracking Number](#get-next-tracking-number)
    - [Create Tracking Number](#create-tracking-number)
    - [Filter Tracking Numbers](#filter-tracking-numbers)

- [Technologies Used](#technologies-used)
- [Demo Collection](#demo-collection)

## Overview
This application provides a RESTful API for managing tracking numbers. It allows users to create new tracking numbers, retrieve existing ones, and filter tracking numbers based on various criteria such as origin and destination countries, weight, and customer information.
## Setup Instructions
### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation
1. Clone the repository:
``` bash
   git clone <repository-url>
   cd tracking-gen
```
1. Build the application:
``` bash
   ./mvnw clean install
```
1. Run the application:
``` bash
   ./mvnw spring-boot:run
```
The application will start and be available at `http://localhost:8080`.
## API Endpoints
The application exposes the following REST endpoints:
### Get All Tracking Numbers
Retrieves all available tracking numbers.
- **URL**: `/v1/getAll`
- **Method**: `GET`
- **Produces**: `application/json`
- **Response**: List of objects `TrackingNumberResponse`
- **Example Request**:
``` bash
  curl -X GET http://localhost:8080/v1/getAll
```
### Get Next Tracking Number
Retrieves the next available tracking number without creating a record.
- **URL**: `/v1/next-tracking-number`
- **Method**: `GET`
- **Produces**: `application/json`
- **Response**: String containing the next tracking number
- **Example Request**:
``` bash
  curl -X GET http://localhost:8080/v1/next-tracking-number
```
### Create Tracking Number
Creates a new tracking number record.
- **URL**: `/v1/create`
- **Method**: `POST`
- **Consumes**: `application/json`
- **Produces**: `application/json`
- **Request Body**: object `TrackingNumberRequest`
- **Response**: object `TrackingNumberResponse`
- **Example Request**:
``` bash
  curl -X POST http://localhost:8080/v1/create \
  -H "Content-Type: application/json" \
  -d '{
    "originCountry": "US",
    "destinationCountry": "CA",
    "weight": "2.5",
    "customerId": "123",
    "customerName": "Example Customer",
    "customerSlug": "example-customer"
  }'
```
### Filter Tracking Numbers
Filters tracking numbers based on specified criteria.
- **URL**: `/v1/filter`
- **Method**: `GET`
- **Produces**: `application/json`
- **Query Parameters**:
    - (optional): Origin country code `origin_country_id`
    - (optional): Destination country code `destination_country_id`
    - (optional): Package weight in KGs `weight`
    - (optional): Creation date `created_at`
    - (optional): Customer ID `customer_id`
    - (optional): Customer name `customer_name`
    - (optional): Customer slug `customer_slug`

- **Response**: List of objects `TrackingNumberResponse`
- **Example Request**:
``` bash
  curl -X GET "http://localhost:8080/v1/filter?origin_country_id=US&destination_country_id=CA&weight=2.5"
```
## Technologies Used
- Java 17
- Spring Boot
- Spring Data JPA
- Spring MVC
- Lombok
- Jakarta EE

This application follows a layered architecture with controllers, services, and repositories for separation of concerns and maintainability.

## Demo Collection
The application is documented and demonstrated in the following video collection:

This collection can be imported into [Insomnia client](https://app.insomnia.rest)
