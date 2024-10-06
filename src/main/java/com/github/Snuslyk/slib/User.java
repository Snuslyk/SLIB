package com.github.Snuslyk.slib;


import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name")
    public String name;
    @Override
    public String toString() {
        return "User: " + id;
    }
}