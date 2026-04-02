package com.example.Lab5.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity(name = "Student")
public class Student {
    @Id
    private String id;
    private String name;
    private double mark;
    private boolean gender;
}
