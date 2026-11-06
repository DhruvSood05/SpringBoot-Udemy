package in.dhruvsood.chailms.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class StudentDTO {
    private long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDate dayOfJoining;
    @JsonProperty("isActive")
    private boolean isActive;

    public StudentDTO() {
    }

    public StudentDTO(long id, String name, String email, Integer age, LocalDate dayOfJoining, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.dayOfJoining = dayOfJoining;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public LocalDate getDayOfJoining() {
        return dayOfJoining;
    }

    public void setDayOfJoining(LocalDate dayOfJoining) {
        this.dayOfJoining = dayOfJoining;
    }
}
