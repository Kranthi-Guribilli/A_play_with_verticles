package com.example.task_2;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMongoDBVerticle{

  private MongoDBVerticle mongoDBVerticle;
  private MongoClient mongoClient;

  @BeforeEach
  void setUp(Vertx vertx, VertxTestContext testContext) {
    JsonObject mongoConfig = new JsonObject()
      .put("connection_string", "mongodb+srv://KranthiGuribilli:Iudx%40517@cluster0.fd7xe8x.mongodb.net/") // Use a test MongoDB instance
      .put("db_name", "iot_data");

    mongoClient = MongoClient.createShared(vertx, mongoConfig);
    mongoDBVerticle = new MongoDBVerticle(vertx, mongoConfig);

    // Deploying the MongoDBVerticle
    vertx.deployVerticle(mongoDBVerticle, testContext.succeeding(id -> testContext.completeNow()));
  }

  @AfterEach
  void tearDown(Vertx vertx, VertxTestContext testContext) {
    // Cleanup resources and close the MongoClient
    mongoClient.close();
    vertx.close(testContext.succeeding(event -> testContext.completeNow()));
  }

  @Test
  void testAddDevice(VertxTestContext testContext) {
    // Creating a Device instance for testing
    Device device = new Device("123", "smart-transport", "MH", "Pune", new Location("point", new JsonArray().add(34.56).add(76.34)), "smart-cam");
    mongoDBVerticle.addDevice(device, ar -> {
      if (ar.succeeded()) {
        testContext.completeNow();
      } else {
        testContext.failNow(ar.cause());
      }
    });
  }

  @Test
  public void testGetDevice(VertxTestContext context) {
    // Assuming that added a device with device ID "123" to the database
    mongoDBVerticle.getDevice("123", ar -> {
      if (ar.succeeded()) {
        Device device = ar.result();
        //context.assertNotNull(device);
        //context.assertEquals("123", device.getDeviceId());
        //context.async().completeNow();
        context.completeNow();
      } else {
        context.failNow(ar.cause());
      }
    });
  }

  @Test
  public void testUpdateDevice(VertxTestContext context) {
    Device updatedDevice = new Device(
      "123",
      "smart-transport-updated",
      "MH",
      "Mumbai",
      new Location("point", new JsonArray().add(19.07).add(72.87)),
      "smart-cam"
    );

    mongoDBVerticle.updateDevice("123", updatedDevice, ar -> {
      if (ar.succeeded()) {
        context.completeNow();
      } else {
        context.failNow(ar.cause());
      }
    });
  }

  @Test
  public void testDeleteDevice(VertxTestContext context) {
    mongoDBVerticle.deleteDevice("123", ar -> {
      if (ar.succeeded()) {
        context.completeNow();
      } else {
        context.failNow(ar.cause());
      }
    });
  }
}
