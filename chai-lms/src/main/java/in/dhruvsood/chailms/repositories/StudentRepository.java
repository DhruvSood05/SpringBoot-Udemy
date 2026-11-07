package in.dhruvsood.chailms.repositories;

import in.dhruvsood.chailms.entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
