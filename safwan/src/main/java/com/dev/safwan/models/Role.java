package com.dev.safwan.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=Role.class)
public class Role extends BaseModel {

  private String role;
}
