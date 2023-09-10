package com.example.task_3.handler;

import com.example.task_3.database.MongoDBVerticle;
import com.example.task_3.model.Device;
import com.example.task_3.restapi.RestAPIVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.example.task_3.config.MongoDBConfig.CONNECTION_STRING;
import static com.example.task_3.config.MongoDBConfig.DB_NAME;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    DeploymentOptions options = new DeploymentOptions().setWorker(true);

    vertx.deployVerticle(new RestAPIVerticle(), options, ar1 -> {
      if (ar1.succeeded()) {
        System.out.println("RestAPIVerticle deployed successfully");
      } else {
        System.err.println("RestAPIVerticle deployment failed: " + ar1.cause());
      }
    });

    vertx.deployVerticle(new MongoDBVerticle(), options, ar2 -> {
      if (ar2.succeeded()) {
        System.out.println("MongoDBVerticle deployed successfully");
        startPromise.complete();
      } else {
        System.err.println("MongoDBVerticle deployment failed: " + ar2.cause());
        startPromise.fail(ar2.cause());
      }
    });
  }
}
