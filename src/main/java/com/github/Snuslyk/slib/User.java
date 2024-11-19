package com.github.Snuslyk.slib;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "colorData")
    public Integer colorData;
    @Column(name = "name")
    public String name;
    @Column(name = "age")
    public int age;
    @Override
    public String toString() {
        return "User: " + id + " " + colorData + " " + name + " " + age;
    }
}