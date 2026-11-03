package com.kz.flatmate.model;

public class UserDTO {

    private Long id;
    private String name;
    private Integer age;
    private String university;
    private String course;
    private String photo;
    private String about;
    private String cleanliness;
    private String sleepMode;
    private String noiseLevel;
    private String guests;
    private String smoking;
    private String pets;
    private String sociability;
    private String searchStatus;
    private String gender;
    private String preferredGender;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.age = user.getAge();
        this.university = user.getUniversity();
        this.course = user.getCourse();
        this.photo = user.getPhoto();
        this.about = user.getAbout();
        this.cleanliness = user.getCleanliness();
        this.sleepMode = user.getSleepMode();
        this.noiseLevel = user.getNoiseLevel();
        this.guests = user.getGuests();
        this.smoking = user.getSmoking();
        this.pets = user.getPets();
        this.sociability = user.getSociability();
        this.searchStatus = user.getSearchStatus();
        this.gender = user.getGender();
        this.preferredGender = user.getPreferredGender();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getUniversity() { return university; }
    public String getCourse() { return course; }
    public String getPhoto() { return photo; }
    public String getAbout() { return about; }
    public String getCleanliness() { return cleanliness; }
    public String getSleepMode() { return sleepMode; }
    public String getNoiseLevel() { return noiseLevel; }
    public String getGuests() { return guests; }
    public String getSmoking() { return smoking; }
    public String getPets() { return pets; }
    public String getSociability() { return sociability; }
    public String getSearchStatus() { return searchStatus; }
    public String getGender() { return gender; }
    public String getPreferredGender() { return preferredGender; }
}