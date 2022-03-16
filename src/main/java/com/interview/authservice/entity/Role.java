package com.interview.authservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "roles", schema = "auth")
public class Role {
    @Id
    private Long id;
    private String roleKey;
}
