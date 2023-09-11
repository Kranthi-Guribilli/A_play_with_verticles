package com.example.task_3.services.impl;

import com.example.task_3.config.MongoDBConfig;
import io.vertx.core.*;
import com.example.task_3.services.DeviceService;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class DeviceServiceImpl extends AbstractVerticle implements DeviceService{
  private DeviceService deviceService;
  private MongoClient mongoClient;

  public DeviceServiceImpl(Vertx vertx, MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }
  @Override
  public void start() {
    EventBus eventBus = vertx.eventBus();
    //JsonObject config = new JsonObject()
      //.put("connection_string", MongoDBConfig.CONNECTION_STRING)
     // .put("db_name",MongoDBConfig.DB_NAME);

    //mongoClient = MongoClient.createShared(vertx, config);
    deviceService = DeviceService.createProxy(vertx, "device-service");
    eventBus.consumer("device-service", this::handleDeviceServiceMessage);
  }

  private void handleDeviceServiceMessage(Message<Object> message) {
    System.out.println("handleDeviceServiceMessage called");
    JsonObject body = (JsonObject) message.body();
    //System.out.println(body.encodePrettily());
    String action = body.getString("action");
    System.out.println(action);
    switch(action){
      case "getDevice":
        String deviceId = body.getString("deviceId");
        System.out.println("deviceId "+deviceId);
        //String deviceId = message.body().toString();
        deviceService.getDeviceById(deviceId, ar -> {
          if (ar.succeeded()) {
            JsonObject result = ar.result();
            message.reply(result);
          } else {
            message.fail(500, ar.cause().getMessage());
          }
        });
        break;
      case "postDevice":
        JsonObject device = body.getJsonObject("device");
        deviceService.addDevice(device, ar -> {
          if (ar.succeeded()) {
            message.reply(ar.result());
          } else {
            message.fail(500, ar.cause().getMessage());
          }
        });
        break;
      case "updateDevice":
        JsonObject deviceToUpdate = body.getJsonObject("device");
        deviceService.updateDevice(deviceToUpdate, ar -> {
          if (ar.succeeded()) {
            message.reply(ar.result());
          } else {
            message.fail(500, ar.cause().getMessage());
          }
        });
        break;

      case "deleteDevice":
        String deviceIdToDelete = body.getString("deviceId");
        deviceService.deleteDevice(deviceIdToDelete, ar -> {
          if (ar.succeeded()) {
            message.reply(ar.result());
          } else {
            message.fail(500, ar.cause().getMessage());
          }
        });
        break;
      default:
        message.fail(400, "Unknown action: " + action);
    }
    System.out.println("handleDeviceServiceMessage finished");
  }


  @Override
  public void getDeviceById(String deviceId, Handler<AsyncResult<JsonObject>> resultHandler) {
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    mongoClient.findOne("devices", query, null, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture(ar.result()));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void addDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler) {
    // Implement your logic to add device to MongoDB
    // Example:
    JsonObject document = device;
    mongoClient.insert("devices", document, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture("Device added successfully"));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void updateDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler) {
    // Implement your logic to update device in MongoDB
    // Example:
    String deviceId = device.getString("deviceId");
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    JsonObject update = device;
    mongoClient.updateCollection("devices", query, new JsonObject().put("$set", update), ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture("Device updated successfully"));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void deleteDevice(String deviceId, Handler<AsyncResult<String>> resultHandler) {
    // Implement your logic to delete device from MongoDB
    // Example:
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    mongoClient.removeDocument("devices", query, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture("Device deleted successfully"));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

}
