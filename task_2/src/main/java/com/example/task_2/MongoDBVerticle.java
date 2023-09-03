package com.example.task_2;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoDBVerticle extends AbstractVerticle {
  private final MongoClient mongoClient;

  public MongoDBVerticle(Vertx vertx, JsonObject config) {
    this.mongoClient = MongoClient.createShared(vertx, config);
  }

  public void addDevice(Device device, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject document=device.toJson();
    mongoClient.insert("devices", document, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        //System.out.println("failed..");
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  public void getDevice(String deviceId, Handler<AsyncResult<Device>> resultHandler) {
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    //System.out.println("query.."+query);
    mongoClient.findOne("devices", query, null, ar -> {
      if (ar.succeeded()) {
        if (ar.result() != null) {
          //System.out.println(ar.result());
          JsonObject json = ar.result();
          Device device = Device.fromJson(json);
          //Device device = ar.result().mapTo(Device.class);
          resultHandler.handle(Future.succeededFuture(device));
        } else {
          resultHandler.handle(Future.succeededFuture(null));
        }
      } else {
              System.out.println("Failed to fetch device: " + ar.cause().getMessage());
              resultHandler.handle(Future.failedFuture(ar.cause()));
            }
          });
  }

  public void updateDevice(String deviceId, Device updatedDevice, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    JsonObject update=updatedDevice.toJson();
    mongoClient.updateCollection("devices", query, new JsonObject().put("$set", update), ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  public void deleteDevice(String deviceId, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    mongoClient.removeDocument("devices", query, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }
}
