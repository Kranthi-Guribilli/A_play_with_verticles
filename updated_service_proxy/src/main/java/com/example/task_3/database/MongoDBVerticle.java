package com.example.task_3.database;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.ext.mongo.MongoClient;

public class MongoDBVerticle extends AbstractVerticle {

  private DatabaseService databaseService;

  @Override
  public void start(Promise<Void> startPromise) {
    // Configure MongoDB
    JsonObject config = new JsonObject()
      .put("connection_string", "mongodb+srv://KranthiGuribilli:Iudx%40517@cluster0.fd7xe8x.mongodb.net/")
      .put("db_name", "iot_data");

    MongoClient mongoClient = MongoClient.createShared(vertx, config);
    databaseService = new DatabaseServiceImpl(mongoClient);

    ServiceBinder binder = new ServiceBinder(vertx);
    binder.setAddress("device-service")
      .register(DatabaseService.class, databaseService);

    startPromise.complete();
  }
}
