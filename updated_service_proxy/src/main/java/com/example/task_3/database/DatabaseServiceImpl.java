package com.example.task_3.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.json.JsonObject;

public class DatabaseServiceImpl implements DatabaseService {

  private final MongoClient mongoClient;

  public DatabaseServiceImpl(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }
  @Override
  public void getDevice(String deviceId, Handler<AsyncResult<JsonObject>> resultHandler) {
    JsonObject query = new JsonObject().put("deviceId", deviceId);
    //System.out.println("query "+query);
    mongoClient.findOne("devices", query, null, ar -> {
      if (ar.succeeded()) {
        //System.out.println("yes");
        //System.out.println(ar.result());
        resultHandler.handle(Future.succeededFuture(ar.result()));
      } else {
       // System.out.println("no");
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void addDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler) {
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
