package com.example.task_3.handler;
import io.vertx.core.Vertx;

public class Launcher {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new MainVerticle(), ar -> {
      if (ar.succeeded()) {
        System.out.println("Application deployed successfully");
      } else {
        System.err.println("Application deployment failed: " + ar.cause());
      }
    });
  }
}

