package com.example.task_2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);
    // Deploying the MongoDBVerticle
    JsonObject mongoConfig = new JsonObject()
            .put("connection_string", "urlToconnectDB")
            .put("db_name", "iot_data");
    MongoDBVerticle mongoDBVerticle = new MongoDBVerticle(vertx, mongoConfig);
    vertx.deployVerticle(mongoDBVerticle, mongoDeployment -> {
      if (mongoDeployment.succeeded()) {
        System.out.println("MongoDBVerticle deployed successfully");

        // Deploying the MainVerticle
        vertx.deployVerticle(new MainVerticle(), mainDeployment -> {
          if (mainDeployment.succeeded()) {
            System.out.println("MainVerticle deployed successfully");
          } else {
            System.err.println("MainVerticle deployment failed: " + mainDeployment.cause());
          }
        });
      } else {
        System.err.println("MongoDBVerticle deployment failed: " + mongoDeployment.cause());
      }
    });
    //Routing
    router.post("/devices/postDevice").handler(ctx -> addDevice(ctx, mongoDBVerticle));
    router.get("/devices/getDevice/:deviceId").handler(ctx -> getDevice(ctx, mongoDBVerticle));
    router.put("/devices/updateDevice/:deviceId").handler(ctx -> updateDevice(ctx, mongoDBVerticle));
    router.delete("/devices/deleteDevice/:deviceId").handler(ctx -> deleteDevice(ctx, mongoDBVerticle));

    vertx.createHttpServer().requestHandler(router).listen(8080);

  }

  private static void addDevice(RoutingContext routingContext, MongoDBVerticle mongoDBVerticle) {
    routingContext.request().bodyHandler(buffer -> {
      String requestBody = buffer.toString();
      try {
        JsonObject requestBodyJson = new JsonObject(requestBody);
        // Logging the request body for debugging
        System.out.println("Request Body: " + requestBodyJson.encodePrettily());
        // Converts JSON to Device object using Device.fromJson method
        Device newDevice = Device.fromJson(requestBodyJson);
        // Using MongoDBVerticle to insert the device into the database
        mongoDBVerticle.addDevice(newDevice, ar -> {
          if (ar.succeeded()) {
            routingContext.response().setStatusCode(201) // Created
              .putHeader("content-type", "application/json")
              .end(Json.encodePrettily(newDevice));
          } else {
            routingContext.response().setStatusCode(500) // Internal Server Error
              .end("Error adding device to the database");
          }
        });
      } catch (DecodeException e) {
        routingContext.response().setStatusCode(400) // Bad Request
          .end("Invalid JSON payload");
      }
    });
  }

  private static void getDevice(RoutingContext routingContext, MongoDBVerticle mongoDBVerticle) {
    //System.out.println("Just logging to check where i am");
    String deviceId = routingContext.request().getParam("deviceId");
    System.out.println(deviceId);
    if (deviceId == null) {
      routingContext.response().setStatusCode(400) // Bad Request
        .end("Missing deviceId parameter");
      return;
    }
    // Using the MongoDBVerticle to retrieve the device by deviceId from the database
    mongoDBVerticle.getDevice(deviceId, ar -> {
      if (ar.succeeded()) {
        Device device = ar.result();
        if (device != null) {
          System.out.println("found");
          routingContext.response().setStatusCode(200) // OK
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(device));
        } else {
          routingContext.response().setStatusCode(404) // Not Found
            .end("Device not found");
        }
      } else {
        routingContext.response().setStatusCode(500) // Internal Server Error
          .end("Error retrieving device from the database");
      }
    });
  }
  private static void updateDevice(RoutingContext routingContext, MongoDBVerticle mongoDBVerticle) {
    // Extract the device ID from the request
    String deviceId = routingContext.request().getParam("deviceId");
    if (deviceId == null) {
      // If device ID is missing, respond with a 400 Bad Request
      routingContext.response().setStatusCode(400)
        .end("Missing deviceId parameter");
      return;
    }
    //Converting the data from json
    routingContext.request().bodyHandler(buffer -> {
      String requestBody = buffer.toString();
      try {
        JsonObject requestBodyJson = new JsonObject(requestBody);
        // Log the request body for debugging
        System.out.println("Request Body: " + requestBodyJson.encodePrettily());
        // Convert JSON to Device object using Device.fromJson method
        Device updatedDevice = Device.fromJson(requestBodyJson);
        updatedDevice.setDeviceId(deviceId);
        mongoDBVerticle.updateDevice(deviceId, updatedDevice, ar -> {
        if (ar.succeeded()) {
          // if Device update successful, respond with a 200 OK
          routingContext.response().setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(updatedDevice));
        } else {
          routingContext.response().setStatusCode(500)
            .end("Error updating device in the database");
        }
      });
    } catch (Exception e) {
      routingContext.response().setStatusCode(400)
        .end("Invalid JSON payload");
    }
    });
  }

  private static void deleteDevice(RoutingContext routingContext, MongoDBVerticle mongoDBVerticle) {
    String deviceId = routingContext.request().getParam("deviceId");

    if (deviceId == null) {
      routingContext.response().setStatusCode(400) // Bad Request
        .end("Missing deviceId parameter");
      return;
    }
    //to delete the device from the database
    mongoDBVerticle.deleteDevice(deviceId, ar -> {
      if (ar.succeeded()) {
        routingContext.response().setStatusCode(204) // No Content
          .end();
      } else {
        routingContext.response().setStatusCode(500) // Internal Server Error
          .end("Error deleting device from the database");
      }
    });
  }

}
