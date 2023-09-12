package com.example.task_3.restapi;

import com.example.task_3.database.DatabaseService;
import com.example.task_3.model.Device;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.serviceproxy.ServiceProxyBuilder;

public class RestAPIVerticle extends AbstractVerticle {

  private DatabaseService databaseService;

  @Override
  public void start() {
    Router router = Router.router(vertx);

    ServiceProxyBuilder builder = new ServiceProxyBuilder(vertx).setAddress("device-service");
    databaseService = builder.build(DatabaseService.class);

    router.route().handler(BodyHandler.create());

    // Define routes
    router.post("/devices/addDevice").handler(this::addDevice);
    router.get("/devices/getDevice/:deviceId").handler(this::getDevice);
    router.put("/devices/updateDevice/:deviceId").handler(this::updateDevice);
    router.delete("/devices/deleteDevice/:deviceId").handler(this::deleteDevice);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

  private void addDevice(RoutingContext routingContext) {
    JsonObject deviceJson = routingContext.getBodyAsJson();
    System.out.println("object "+deviceJson);
    databaseService.addDevice(deviceJson, ar -> {
      if (ar.succeeded()) {
        routingContext.response()
          .setStatusCode(201)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(ar.result()));
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(new JsonObject().put("error", ar.cause().getMessage()).encode());
      }
    });
  }

  private void getDevice(RoutingContext routingContext) {
    String deviceId = routingContext.pathParam("deviceId");
    System.out.println("deviceId "+deviceId);
    databaseService.getDevice(deviceId, ar -> {
      if (ar.succeeded()) {
        if (ar.result()!=null) {
          routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(ar.result()));
        } else {
          routingContext.response()
            .setStatusCode(404)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(new JsonObject().put("error", "Device not found").encode());
        }
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(new JsonObject().put("error", ar.cause().getMessage()).encode());
      }
    });
  }

  private void updateDevice(RoutingContext routingContext) {
    String deviceId = routingContext.pathParam("deviceId");
    //System.out.println("deviceId "+deviceId);
    JsonObject deviceJson = routingContext.getBodyAsJson();
    //System.out.println("deviceJson "+deviceJson);
    //Device updatedDevice = deviceJson.mapTo(Device.class);
    Device updatedDevice = Device.fromJson(deviceJson);
    //System.out.println("update "+updatedDevice);
    updatedDevice.setDeviceId(deviceId);

    databaseService.updateDevice(deviceJson, ar -> {
      if (ar.succeeded()) {
        if (ar.result() != null) {
          routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(ar.result()));
        } else {
          routingContext.response()
            .setStatusCode(404)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(new JsonObject().put("error", "Device not found").encode());
        }
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(new JsonObject().put("error", ar.cause().getMessage()).encode());
      }
    });
  }

  private void deleteDevice(RoutingContext routingContext) {
    String deviceId = routingContext.pathParam("deviceId");
    databaseService.deleteDevice(deviceId, ar -> {
      if (ar.succeeded()) {
        if (ar.result()!=null) {
          routingContext.response()
            .setStatusCode(204)
            .end();
        } else {
          routingContext.response()
            .setStatusCode(404)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(new JsonObject().put("error", "Device not found").encode());
        }
      } else {
        routingContext.response()
          .setStatusCode(500)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(new JsonObject().put("error", ar.cause().getMessage()).encode());
      }
    });
  }
}
