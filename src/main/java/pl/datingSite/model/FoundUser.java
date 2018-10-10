package pl.datingSite.model;

import java.util.Date;

public class FoundUser {

    private byte[] avatar;
    private String name, username;
    private Date dateOfBirth;
    private City city;
    private boolean fake;

    public FoundUser() {
    }

    public FoundUser(byte[] avatar, String name, String username, Date dateOfBirth, City city, boolean fake) {
        this.avatar = avatar;
        this.name = name;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.fake = fake;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FoundUser{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", city=" + city +
                ", fake=" + fake +
                '}';
    }
}