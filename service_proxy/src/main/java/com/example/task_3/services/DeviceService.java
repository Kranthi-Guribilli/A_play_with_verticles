package com.example.task_3.services;

import com.example.task_3.services.impl.DeviceServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@ProxyGen
public interface DeviceService{
  static DeviceService createProxy(Vertx vertx, String address) {
    return new DeviceServiceVertxEBProxy(vertx, address);
  }

  /*static DeviceService createProxy(Vertx vertx, String address) {
    return new ServiceProxyBuilder(vertx)
      .setAddress(address)
      .build(DeviceService.class);
  }*/

  void getDeviceById(String deviceId, Handler<AsyncResult<JsonObject>> resultHandler);

  void addDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler);

  void updateDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler);

  void deleteDevice(String deviceId, Handler<AsyncResult<String>> resultHandler);
}
