package com.interview.authservice.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Set;

@Accessors(chain = true)
@Setter
@Getter
@Entity
@Table(schema = "auth", name = "users")
@SequenceGenerator(name = "users_id_seq", schema = "auth", sequenceName = "users_id_seq", allocationSize = 1)
public class User extends Aggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    private Long id;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> roles;

    private String username;
    private String firstName;
    private String lastName;
    private String password;
}
