package com.kz.flatmate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Данные для входа в аккаунт
    private String email;
    private String password;

    // Данные анкеты
    private String name;
    private Integer age;
    private String university;
    private String course; 

    @Column(length = 500)
    private String about;

    private String cleanliness;
    private String sleepMode;
    private String noiseLevel;
    private String guests;
    private String smoking;
    private String pets;
    private String sociability;
    private String searchStatus;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public String getCleanliness() { return cleanliness; }
    public void setCleanliness(String cleanliness) { this.cleanliness = cleanliness; }
    public String getSleepMode() { return sleepMode; }
    public void setSleepMode(String sleepMode) { this.sleepMode = sleepMode; }
    public String getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(String noiseLevel) { this.noiseLevel = noiseLevel; }
    public String getGuests() { return guests; }
    public void setGuests(String guests) { this.guests = guests; }
    public String getSmoking() { return smoking; }
    public void setSmoking(String smoking) { this.smoking = smoking; }
    public String getPets() { return pets; }
    public void setPets(String pets) { this.pets = pets; }
    public String getSociability() { return sociability; }
    public void setSociability(String sociability) { this.sociability = sociability; }
    public String getSearchStatus() { return searchStatus; }
    public void setSearchStatus(String searchStatus) { this.searchStatus = searchStatus; }
}
