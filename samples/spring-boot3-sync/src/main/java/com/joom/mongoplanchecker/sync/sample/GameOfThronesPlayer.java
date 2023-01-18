package com.joom.mongoplanchecker.sync.sample;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class GameOfThronesPlayer {
  @Id private ObjectId id;
  @Indexed private String house;
  private String name;

  public GameOfThronesPlayer() {}

  public GameOfThronesPlayer(String house, String name) {
    this.house = house;
    this.name = name;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getHouse() {
    return house;
  }

  public void setHouse(String house) {
    this.house = house;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
