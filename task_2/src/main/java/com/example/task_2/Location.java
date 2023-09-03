package com.example.task_2;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Location {
  private String Type;
  private JsonArray Coordinates;

  public Location() {
  }

  public Location(String Type, JsonArray Coordinates) {
    this.Type = Type;
    this.Coordinates = Coordinates;
  }

  public String getType() {
    return Type;
  }

  public void setType(String Type) {
    this.Type = Type;
  }

  public JsonArray getCoordinates() {
    return Coordinates;
  }

  public void setCoordinates(JsonArray Coordinates) {
    this.Coordinates = Coordinates;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
      .put("Type", Type)
      .put("Coordinates", Coordinates);
    return json;
  }

  public static Location fromJson(JsonObject json) {
    Location location = new Location();
    location.setType(json.getString("Type"));
    location.setCoordinates(json.getJsonArray("Coordinates"));
    return location;
  }
}
