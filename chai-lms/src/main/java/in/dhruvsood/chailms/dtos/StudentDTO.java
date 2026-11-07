package in.dhruvsood.chailms.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;

    @NotNull(message = "Required field is student: Name")
    @Size(min = 3, max = 36, message = "No of characters should be in range of : (3, 36)")
    private String name;

    @NotBlank(message = "Email field cannot be blank")
    @Email(message = "Email should be a valid input")
    private String email;

    @NotNull(message = "Age field cannot be null")
    @Min(value = 3, message = "Age of students must be min of 3 years")
    @Max(value = 18, message = "Age of student cannot be greater than 18 years")
    private Integer age;

    @PastOrPresent(message = "date of joining cannot be in future")
    private LocalDate dayOfJoining;

    @AssertTrue(message = "Student should be activ e")
    @JsonProperty("isActive")
    private Boolean isActive;


}
