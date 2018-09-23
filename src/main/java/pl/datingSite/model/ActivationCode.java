package pl.datingSite.model;

import pl.datingSite.tools.ActivationCodeGenerator;

import java.util.Objects;

public class ActivationCode {
    private Long id;

    private String email, activationCode;

    public ActivationCode() {
    }

    public ActivationCode(String email) {
        this.email = email;
        ActivationCodeGenerator generator = new ActivationCodeGenerator();
        this.activationCode = generator.nextString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivationCode that = (ActivationCode) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(activationCode, that.activationCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(email, activationCode);
    }

    @Override
    public String toString() {
        return "ActivationCode{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", activationCode='" + activationCode + '\'' +
                '}';
    }
}
