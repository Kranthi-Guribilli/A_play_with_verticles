// MainVerticle.java
package com.example.task_3.handler;

import com.example.task_3.config.MongoDBConfig;
import com.example.task_3.restapi.RestAPIVerticle;
import com.example.task_3.services.DeviceService;
import com.example.task_3.services.impl.DeviceServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    DeploymentOptions options = new DeploymentOptions().setWorker(true);
    // Create a MongoClient
    JsonObject config = new JsonObject()
      .put("connection_string", MongoDBConfig.CONNECTION_STRING)
      .put("db_name", MongoDBConfig.DB_NAME);

    MongoClient mongoClient = MongoClient.createShared(vertx, config);

    // Deploy DeviceService verticle
    vertx.deployVerticle(new DeviceServiceImpl(vertx, mongoClient), options, ar1 -> {
      if (ar1.succeeded()) {
        System.out.println("DeviceService deployed successfully");

        // Deploy RestAPIVerticle after DeviceService is deployed
        vertx.deployVerticle(new RestAPIVerticle(), options, ar2 -> {
          if (ar2.succeeded()) {
            System.out.println("RestAPIVerticle deployed successfully");
            startPromise.complete();
          } else {
            System.err.println("RestAPIVerticle deployment failed: " + ar2.cause());
            startPromise.fail(ar2.cause());
          }
        });
      } else {
        System.err.println("DeviceService deployment failed: " + ar1.cause());
        startPromise.fail(ar1.cause());
      }
    });
  }
}
