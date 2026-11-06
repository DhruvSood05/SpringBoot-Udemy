package in.dhruvsood.chailms.controllers;


import in.dhruvsood.chailms.dtos.StudentDTO;
import in.dhruvsood.chailms.entities.StudentEntity;
import in.dhruvsood.chailms.repositories.StudentRepository;
import in.dhruvsood.chailms.services.StudentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
public class StudentController {

    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("/students/{studentId}")
    public StudentDTO getStudentById(
            @PathVariable long studentId
    ) {
        return studentService.getStudentById(studentId);


    }

    @GetMapping("/students")
    public List<StudentDTO> getStudents(

    ) {
        return studentService.getAllStudents();
    }

    @PostMapping("/students")
    public StudentDTO CreateStudent(
            @RequestBody StudentEntity student
    ) {

      return studentService.createStudent(student);
    }



}
