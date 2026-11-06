package in.dhruvsood.chailms.services;

import in.dhruvsood.chailms.dtos.StudentDTO;
import in.dhruvsood.chailms.entities.StudentEntity;
import in.dhruvsood.chailms.repositories.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository, ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
    }


    public StudentDTO getStudentById(Long studentId) {
       StudentEntity studentEntity = studentRepository.findById(studentId).orElse(null);

       return modelMapper.map(studentEntity, StudentDTO.class);
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentEntity -> modelMapper.map(studentEntity, StudentDTO.class))
                .toList();
    }

    public StudentDTO createStudent(StudentEntity student) {
        StudentEntity studentEntity = studentRepository.save(student);

        return modelMapper.map(studentEntity, StudentDTO.class);
    }
}
