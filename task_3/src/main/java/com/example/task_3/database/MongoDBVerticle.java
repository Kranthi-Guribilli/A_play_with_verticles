package com.example.task_3.database;

import com.example.task_3.model.Device;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import com.example.task_3.config.MongoDBConfig;
import io.vertx.ext.web.RoutingContext;

public class MongoDBVerticle extends AbstractVerticle {

  private MongoClient mongoClient;

  @Override
  public void start() {
    JsonObject config = new JsonObject()
      .put("connection_string", MongoDBConfig.CONNECTION_STRING)
      .put("db_name",MongoDBConfig.DB_NAME);

    mongoClient = MongoClient.createShared(vertx, config);

    EventBus eventBus = vertx.eventBus();
    eventBus.consumer("getDeviceById", this::handleGetDevice);
    eventBus.consumer("addDevice", this::handleAddDevice);
    eventBus.consumer("updateDevice", this::handleUpdateDevice);
    eventBus.consumer("deleteDevice", this::handleDeleteDevice);
  }

  private void handleGetDevice(Message<Object> message) {
    String deviceId = message.body().toString();

    mongoClient.findOne("devices", new JsonObject().put("deviceId", deviceId), null, ar -> {
      if (ar.succeeded()) {
        message.reply(ar.result());
      } else {
        message.fail(500, ar.cause().getMessage());
      }
    });
  }

  private void handleAddDevice(io.vertx.core.eventbus.Message<Object> message) {
    try {
      Device device = Device.fromJson((JsonObject) message.body());

      JsonObject document = device.toJson();
      mongoClient.insert("devices", document, ar -> {
        if (ar.succeeded()) {
          message.reply("Device added successfully");
        } else {
          message.fail(500, ar.cause().getMessage());
        }
      });
    } catch (Exception e) {
      message.fail(400, "Invalid JSON payload");
    }
  }


  private void handleUpdateDevice(io.vertx.core.eventbus.Message<Object> message) {
    JsonObject requestBodyJson = (JsonObject) message.body();
    try {
      // Log the request body for debugging
      System.out.println("Request Body: " + requestBodyJson.encodePrettily());

      // Convert JSON to Device object using Device.fromJson method
      Device updatedDevice = Device.fromJson(requestBodyJson);
      String deviceId = updatedDevice.getDeviceId();
      JsonObject query = new JsonObject().put("deviceId", deviceId);
      JsonObject update = updatedDevice.toJson();

      mongoClient.updateCollection("devices", query, new JsonObject().put("$set", update), ar -> {
        if (ar.succeeded()) {
          message.reply("Device updated successfully");
        } else {
          message.fail(500, ar.cause().getMessage());
        }
      });
    } catch (Exception e) {
      message.fail(400, "Invalid JSON payload");
    }
  }


  private void handleDeleteDevice(io.vertx.core.eventbus.Message<Object> message) {
    String deviceId = message.body().toString();

    JsonObject query = new JsonObject().put("deviceId", deviceId);
    mongoClient.removeDocument("devices", query, ar -> {
      if (ar.succeeded()) {
        message.reply("Device deleted successfully");
      } else {
        message.fail(500, ar.cause().getMessage());
      }
    });
  }

}
