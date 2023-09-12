package com.example.task_3.model;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.vertx.core.shareddata.Shareable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device{
  private String deviceId;
  private String Domain;
  private String state;
  private String city;
  private Location location;
  private String deviceType;

  public Device() {
  }

  public Device(String deviceId, String Domain, String state, String city, Location location, String deviceType) {
    this.deviceId = deviceId;
    this.Domain = Domain;
    this.state = state;
    this.city = city;
    this.location = location;
    this.deviceType = deviceType;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getDomain() {
    return Domain;
  }

  public void setDomain(String domain) {
    this.Domain = domain;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
      .put("deviceId", deviceId)
      .put("Domain", Domain)
      .put("state", state)
      .put("city", city)
      .put("location", location.toJson())
      .put("deviceType", deviceType);
    return json;
  }

  public static Device fromJson(JsonObject json) {
    Device device = new Device();
    device.setDeviceId(json.getString("deviceId"));
    device.setDomain(json.getString("Domain"));
    device.setState(json.getString("state"));
    device.setCity(json.getString("city"));
    device.setLocation(Location.fromJson(json.getJsonObject("location")));
    device.setDeviceType(json.getString("deviceType"));
    return device;
  }
}
