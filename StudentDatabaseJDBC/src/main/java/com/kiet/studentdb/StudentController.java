
package com.kiet.studentdb;

import java.util.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentDAO dao;

    public StudentController(StudentDAO dao) {
        this.dao = dao;
    }

    // 1. Add student
    @PostMapping
    public String addStudent(@RequestBody Student student) {
        return dao.addStudent(student);
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return dao.getAllStudents();
    }

    // 3. Search student by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable int id) {
        return dao.getStudentById(id);
    }

    // 4. Update student scores and skills
    @PutMapping("/{id}")
    public String updateStudent(
            @PathVariable int id,
            @RequestBody StudentUpdateRequest request) {

        return dao.updateStudent(id, request);
    }

    // 5. Delete student
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable int id) {
        return dao.deleteStudent(id);
    }

    // 6. Display merit list
    @GetMapping("/merit")
    public List<Student> meritList() {
        return dao.getMeritList();
    }
}
