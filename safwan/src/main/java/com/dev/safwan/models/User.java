package com.dev.safwan.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Role> roles=new HashSet<>();
}
