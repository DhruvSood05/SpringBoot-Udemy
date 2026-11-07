package in.dhruvsood.chailms.services;

import in.dhruvsood.chailms.dtos.StudentDTO;
import in.dhruvsood.chailms.entities.StudentEntity;
import in.dhruvsood.chailms.repositories.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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

    public StudentDTO createStudent(StudentDTO student) {
        StudentEntity studentEntity = modelMapper.map(student, StudentEntity.class);

        studentRepository.save(studentEntity);


        return modelMapper.map(studentEntity, StudentDTO.class);
    }

    public StudentDTO updateStudentById(StudentDTO student, Long studentId) {
        boolean existsById = isExistsById(studentId);

        if (!existsById) {
            return null;
        }
        StudentEntity studentEntity = modelMapper.map(student, StudentEntity.class);
        studentEntity.setId(studentId);

        StudentEntity updatedStudent = studentRepository.save(studentEntity);
        return modelMapper.map(updatedStudent, StudentDTO.class);
    }

    public StudentDTO partialUpdateStudentById(Map<String, Object> studentUpdates, Long studentId) {
        boolean existsById = isExistsById(studentId);

        if (!existsById) {
            return null;

        }

        StudentEntity studentEntity = studentRepository.findById(studentId).get();
        studentUpdates.forEach((field, value) -> {
            Field fieldToUpdate = ReflectionUtils.findField(StudentEntity.class, field);
            if (fieldToUpdate == null) {
                return;
            }
            fieldToUpdate.setAccessible(true);
            ReflectionUtils.setField(fieldToUpdate, studentEntity, value);
        });

        studentRepository.save(studentEntity);

        return modelMapper.map(studentEntity, StudentDTO.class);
    }

    public boolean deleteStudentById(Long studentId) {
        boolean existsById = isExistsById(studentId);

        if (!existsById) {
            return false;
        }

        studentRepository.deleteById(studentId);

        return true;

    }

    private boolean isExistsById(Long studentId) {
        return studentRepository.existsById(studentId);
    }
}
