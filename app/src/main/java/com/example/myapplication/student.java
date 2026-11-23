package com.example.myapplication;

import java.io.Serializable;

public class student implements Serializable {
    String firstName, lastName, email, grade, major;
    boolean notifications, terms;

    public student(String firstName, String lastName,
                   String email, String grade, String major,
                   boolean notifications, boolean terms) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.grade = grade;
        this.major = major;
        this.notifications = notifications;
        this.terms = terms;
    }
}