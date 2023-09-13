package com.example.task_3;

import com.example.task_3.database.DatabaseServiceImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class DatabaseServiceImplTest {

  private MongoClient mongoClient;
  private DatabaseServiceImpl databaseService;

  @BeforeEach
  void setUp(Vertx vertx, VertxTestContext testContext) {
    JsonObject config = new JsonObject()
      .put("connection_string", "mongodb+srv://KranthiGuribilli:Iudx%40517@cluster0.fd7xe8x.mongodb.net/")
      .put("db_name", "iot_data");
    mongoClient = MongoClient.createShared(vertx, config);
    databaseService = new DatabaseServiceImpl(mongoClient);

    testContext.completeNow();
  }

  @AfterEach
  void tearDown(Vertx vertx, VertxTestContext testContext) {
    vertx.close(testContext.succeeding(ar -> testContext.completeNow()));
  }

  @Test
  void testGetDevice(Vertx vertx, VertxTestContext testContext) {
    String deviceId = "1234";

    databaseService.getDevice(deviceId, ar -> {
      if (ar.succeeded()) {
        testContext.verify(() -> {
          Assertions.assertNotNull(ar.result());
          Assertions.assertEquals(deviceId, ar.result().getString("deviceId"));
          Assertions.assertNotNull(ar.result().getString("Domain"));
          testContext.completeNow();
        });
      } else {
        testContext.failNow(ar.cause());
      }
    });
  }

  @Test
  void testAddDevice(Vertx vertx, VertxTestContext testContext) {
    JsonObject device = new JsonObject()
      .put("deviceId", "1234")
      .put("Domain", "smart-transport")
      .put("state", "MH")
      .put("city", "Pune")
      .put("location", new JsonObject()
        .put("Type", "point")
        .put("Coordinates", new JsonArray().add(34.56).add(76.34)))
      .put("deviceType", "smart-cam");

    databaseService.addDevice(device, ar -> {
      if (ar.succeeded()) {
        testContext.verify(() -> {
          Assertions.assertEquals("Device added successfully", ar.result());
          testContext.completeNow();
        });
      } else {
        testContext.failNow(ar.cause());
      }
    });
  }

  @Test
  void testUpdateDevice(Vertx vertx, VertxTestContext testContext) {
    JsonObject device = new JsonObject()
      .put("deviceId", "123")
      .put("Domain", "smart-transport")
      .put("state", "MH")
      .put("city", "Pune")
      .put("location", new JsonObject()
        .put("Type", "point")
        .put("Coordinates", new JsonArray().add(34.56).add(76.34)))
      .put("deviceType", "smart-camera");

    databaseService.updateDevice(device, ar -> {
      if (ar.succeeded()) {
        testContext.verify(() -> {
          Assertions.assertEquals("Device updated successfully", ar.result());
          testContext.completeNow();
        });
      } else {
        testContext.failNow(ar.cause());
      }
    });
  }

  @Test
  void testDeleteDevice(Vertx vertx, VertxTestContext testContext) {
    String deviceId = "1234";

    databaseService.deleteDevice(deviceId, ar -> {
      if (ar.succeeded()) {
        testContext.verify(() -> {
          Assertions.assertEquals("Device deleted successfully", ar.result());
          testContext.completeNow();
        });
      } else {
        testContext.failNow(ar.cause());
      }
    });
  }
}
