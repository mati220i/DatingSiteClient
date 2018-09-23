package pl.datingSite.model;

import pl.datingSite.enums.Education;
import pl.datingSite.enums.MaritalStatus;
import pl.datingSite.enums.Profession;
import pl.datingSite.enums.ZodiacSign;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class User {
    private Long id;

    private String username, password, email, name, surname, sex;
    private Date dateOfBirth;
    private City city;

    private ZodiacSign zodiacSign;
    private Profession profession;
    private MaritalStatus maritalStatus;
    private Education education;

    private byte[] avatar;

    private AppearanceAndCharacter appearanceAndCharacter;

    private Set<String> interests;

    private boolean enabled, fake;

    public User() {
        this.enabled = true;
    }

    public User(String username, String password, String email, String name, String surname, City city, String sex, Date dateOfBirth) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public ZodiacSign getZodiacSign() {
        return zodiacSign;
    }

    public void setZodiacSign(ZodiacSign zodiacSign) {
        this.zodiacSign = zodiacSign;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public AppearanceAndCharacter getAppearanceAndCharacter() {
        return appearanceAndCharacter;
    }

    public void setAppearanceAndCharacter(AppearanceAndCharacter appearanceAndCharacter) {
        this.appearanceAndCharacter = appearanceAndCharacter;
    }

    public Set<String> getInterests() {
        return interests;
    }

    public void setInterests(Set<String> interests) {
        this.interests = interests;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", sex='" + sex + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", city=" + city +
                ", zodiacSign=" + zodiacSign +
                ", profession=" + profession +
                ", maritalStatus=" + maritalStatus +
                ", education=" + education +
                ", avatar=" + avatar +
                ", appearanceAndCharacter=" + appearanceAndCharacter +
                ", interests=" + interests +
                ", enabled=" + enabled +
                ", fake=" + fake +
                '}';
    }
}