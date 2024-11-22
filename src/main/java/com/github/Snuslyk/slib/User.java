package com.github.Snuslyk.slib;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User implements RowData {

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

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getColorData() {
        return -1;
    }
}