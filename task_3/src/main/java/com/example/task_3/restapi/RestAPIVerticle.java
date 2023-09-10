package com.example.task_3.restapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import com.example.task_3.constants.HttpStatusCodes;

public class RestAPIVerticle extends AbstractVerticle {

  @Override
  public void start() {
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

    EventBus eventBus = vertx.eventBus();
    eventBus.request("getDeviceById", deviceId, ar -> {
      if (ar.succeeded()) {
        routingContext.response().putHeader("content-type", "application/json")
          .end(ar.result().body().toString());
      } else {
        routingContext.response().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .end(ar.cause().getMessage());
      }
    });
  }

  private void handleAddDevice(RoutingContext routingContext) {
    routingContext.request().bodyHandler(buffer -> {
      JsonObject requestBodyJson = buffer.toJsonObject();

      EventBus eventBus = vertx.eventBus();
      eventBus.request("addDevice", requestBodyJson, ar -> {
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
      eventBus.request("updateDevice", requestBodyJson.put("deviceId", deviceId), ar -> {
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
    eventBus.request("deleteDevice", deviceId, ar -> {
      if (ar.succeeded()) {
        routingContext.response().setStatusCode(HttpStatusCodes.NO_CONTENT).end();
      } else {
        routingContext.response().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR)
          .end(ar.cause().getMessage());
      }
    });
  }
}
