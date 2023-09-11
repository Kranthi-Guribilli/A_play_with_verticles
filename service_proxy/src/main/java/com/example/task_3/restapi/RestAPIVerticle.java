//RestAPIVerticle.java
package com.example.task_3.restapi;

import com.example.task_3.services.DeviceService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import com.example.task_3.constants.HttpStatusCodes;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class RestAPIVerticle extends AbstractVerticle {

  @Override
  public void start() {
    // Define your routes here
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    router.get("/devices/getDevice/:deviceId").handler(this::handleGetDevice);
    router.post("/devices/postDevice").handler(this::handleAddDevice);
    router.put("/devices/updateDevice/:deviceId").handler(this::handleUpdateDevice);
    router.delete("/devices/deleteDevice/:deviceId").handler(this::handleDeleteDevice);

    server.requestHandler(router).listen(8081);
  }

  private void handleGetDevice(RoutingContext routingContext) {
    String deviceId = routingContext.request().getParam("deviceId");
    JsonObject messageBody = new JsonObject()
      .put("deviceId", deviceId)
      .put("action", "getDevice");
    EventBus eventBus = vertx.eventBus();
    System.out.println("am in handleGetDevice");
    eventBus.request("device-service", messageBody, ar -> {
      if (ar.succeeded()) {
        routingContext.response().putHeader("content-type", "application/json")
          .end(ar.result().body().toString());
      } else {
        routingContext.response().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .end(ar.cause().getMessage());
      }
    });
  }

  // Add other route handlers (handleAddDevice, handleUpdateDevice, handleDeleteDevice) similarly
  private void handleAddDevice(RoutingContext routingContext) {
    routingContext.request().bodyHandler(buffer -> {
      JsonObject requestBodyJson = buffer.toJsonObject();

      EventBus eventBus = vertx.eventBus();
      JsonObject messageBody = new JsonObject()
              .put("device", requestBodyJson)
              .put("action", "postDevice");
      System.out.println("am in handleAddDevice");
      eventBus.request("device-service", messageBody, ar -> {
        if (ar.succeeded()) {
          routingContext.response().setStatusCode(HttpStatusCodes.CREATED)
            .end(ar.result().body().toString());
        } else {
          routingContext.response().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR)
            .end(ar.cause().getMessage());
        }
      });
    });
  }


  private void handleUpdateDevice(RoutingContext routingContext) {
    String deviceId = routingContext.request().getParam("deviceId");

    routingContext.request().bodyHandler(buffer -> {
      JsonObject requestBodyJson = buffer.toJsonObject();

      EventBus eventBus = vertx.eventBus();
      System.out.println("am in handleUpdateDevice");
      eventBus.request("device-service", requestBodyJson.put("deviceId", deviceId), ar -> {
        if (ar.succeeded()) {
          routingContext.response().setStatusCode(HttpStatusCodes.OK)
            .end(ar.result().body().toString());
        } else {
          routingContext.response().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR)
            .end(ar.cause().getMessage());
        }
      });
    });
  }

  private void handleDeleteDevice(RoutingContext routingContext) {
    String deviceId = routingContext.request().getParam("deviceId");

    EventBus eventBus = vertx.eventBus();
    System.out.println("am in handleDeleteDevice");
    eventBus.request("device-service", deviceId, ar -> {
      if (ar.succeeded()) {
        routingContext.response().setStatusCode(HttpStatusCodes.NO_CONTENT).end();
      } else {
        routingContext.response().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .end(ar.cause().getMessage());
      }
    });
  }

}
