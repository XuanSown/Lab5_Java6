package com.example.Lab5.controller;

import com.example.Lab5.entity.Student;
import com.example.Lab5.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*") //cho phép tất cả các domain truy cập
public class StudentRestApiController {
    @Autowired
    StudentService service;

    @GetMapping("students")
    public List<Student> findAll(){
        return service.findAll();
    }

    @GetMapping("students/{id}")
    public Student findById(@PathVariable("id") String id){
        return service.findById(id);
    }

    @PostMapping("students")
    public Student create(@RequestBody Student student){
        return service.create(student);
    }

    @PutMapping("students/{id}")
    public Student update(@PathVariable("id") String id,
                          @RequestBody Student student){
        return service.update(student);
    }

    @DeleteMapping("students/{id}")
    public void deleteById(@PathVariable("id") String id){
        service.deleteById(id);
    }
}
