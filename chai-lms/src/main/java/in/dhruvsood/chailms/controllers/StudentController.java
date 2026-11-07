package in.dhruvsood.chailms.controllers;


import in.dhruvsood.chailms.dtos.StudentDTO;
import in.dhruvsood.chailms.entities.StudentEntity;
import in.dhruvsood.chailms.repositories.StudentRepository;
import in.dhruvsood.chailms.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "/students")
public class StudentController {

    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("/{studentId}")
    public StudentDTO getStudentById(
            @PathVariable long studentId
    ) {

        return studentService.getStudentById(studentId);


    }

    @GetMapping
    public List<StudentDTO> getStudents(

    ) {
        return studentService.getAllStudents();
    }

    @PostMapping
    public StudentDTO CreateStudent(
            @RequestBody @Valid StudentDTO student
    ) {

        return studentService.createStudent(student);
    }

    @PutMapping("/{studentId}")
    public StudentDTO updateStudentById(
            @RequestBody @Valid StudentDTO student,
            @PathVariable Long studentId
    ) {
        return studentService.updateStudentById(student, studentId);
    }

    @PatchMapping("/{studentId}")
    public StudentDTO partialUpdateStudentById(
            @RequestBody Map<String, Object> studentUpdates,
            @PathVariable Long studentId
    ) {
        return studentService.partialUpdateStudentById(studentUpdates, studentId);
    }

    @DeleteMapping("/{studentId}")
    public boolean deleteStudent(
            @PathVariable Long studentId
    ) {

        return studentService.deleteStudentById(studentId);
    }


}
