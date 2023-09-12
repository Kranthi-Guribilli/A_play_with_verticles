package com.example.task_3;
import com.example.task_3.database.MongoDBVerticle;
import com.example.task_3.restapi.RestAPIVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.DeploymentOptions;

public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    // Deploy DeviceRepositoryVerticle
    vertx.deployVerticle(MongoDBVerticle.class.getName(), new DeploymentOptions().setInstances(1), res1 -> {
      if (res1.succeeded()) {
        System.out.println("MongoDBVerticle deployed successfully");
      } else {
        System.out.println("MongoDBVerticle deployment failed");
      }

      // Deploy RestAPIVerticle
      vertx.deployVerticle(RestAPIVerticle.class.getName(), new DeploymentOptions().setInstances(1), res2 -> {
        if (res2.succeeded()) {
          System.out.println("RestAPIVerticle deployed");
        } else {
          System.out.println("RestAPIVerticle deployment failed");
        }
      });
    });
  }
}

