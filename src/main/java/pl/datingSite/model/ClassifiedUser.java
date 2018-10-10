package pl.datingSite.model;

import java.util.Date;

public class ClassifiedUser extends FoundUser {

    private float fitPercentage;

    public ClassifiedUser() {

    }

    public ClassifiedUser(byte[] avatar, String name, String username, Date dateOfBirth, City city, boolean fake, float fitPercentage) {
        super(avatar, name, username, dateOfBirth, city, fake);
        this.fitPercentage = fitPercentage;
    }

    public float getFitPercentage() {
        return fitPercentage;
    }

    public void setFitPercentage(float fitPercentage) {
        this.fitPercentage = fitPercentage;
    }

    @Override
    public String toString() {
        return "ClassifiedUser{" +
                "name='" + this.getName() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", dateOfBirth=" + this.getDateOfBirth() +
                ", city=" + this.getCity() +
                ", fake=" + this.isFake() +
                ", fitPercentage=" + fitPercentage +
                '}';
    }
}
