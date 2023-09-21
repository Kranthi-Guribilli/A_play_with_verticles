package com.example.task_3.database;


import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;


@VertxGen
@ProxyGen
public interface DatabaseService{
  
  @GenIgnore
  static DatabaseService createProxy(Vertx vertx, String address) {
    return new DatabaseServiceVertxEBProxy(vertx, address);
  }

  void getDevice(String deviceId, Handler<AsyncResult<JsonObject>> resultHandler);

  void addDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler);

  void updateDevice(JsonObject device, Handler<AsyncResult<String>> resultHandler);

  void deleteDevice(String deviceId, Handler<AsyncResult<String>> resultHandler);

}
