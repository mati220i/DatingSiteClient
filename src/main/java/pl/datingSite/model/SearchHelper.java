package pl.datingSite.model;

import pl.datingSite.enums.*;

import java.util.List;

public class SearchHelper {
    private String sex;
    private Integer ageFrom, ageTo, distance, heightFrom, heightTo;
    private boolean withAvatar, real;
    private City location;

    private List<MaritalStatus> maritalStatuses;
    private List<Figure> figures;
    private List<HairColor> hairColors;
    private List<Smoking> smokings;
    private List<Alcohol> alcohol;
    private List<Children> children;
    private List<LookingFor> lookingFors;
    private List<Religion> religions;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(Integer ageFrom) {
        this.ageFrom = ageFrom;
    }

    public Integer getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(Integer ageTo) {
        this.ageTo = ageTo;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getHeightFrom() {
        return heightFrom;
    }

    public void setHeightFrom(Integer heightFrom) {
        this.heightFrom = heightFrom;
    }

    public Integer getHeightTo() {
        return heightTo;
    }

    public void setHeightTo(Integer heightTo) {
        this.heightTo = heightTo;
    }

    public boolean isWithAvatar() {
        return withAvatar;
    }

    public void setWithAvatar(boolean withAvatar) {
        this.withAvatar = withAvatar;
    }

    public City getLocation() {
        return location;
    }

    public void setLocation(City location) {
        this.location = location;
    }

    public List<MaritalStatus> getMaritalStatuses() {
        return maritalStatuses;
    }

    public void setMaritalStatuses(List<MaritalStatus> maritalStatuses) {
        this.maritalStatuses = maritalStatuses;
    }

    public List<Figure> getFigures() {
        return figures;
    }

    public void setFigures(List<Figure> figures) {
        this.figures = figures;
    }

    public List<HairColor> getHairColors() {
        return hairColors;
    }

    public void setHairColors(List<HairColor> hairColors) {
        this.hairColors = hairColors;
    }

    public List<Smoking> getSmokings() {
        return smokings;
    }

    public void setSmokings(List<Smoking> smokings) {
        this.smokings = smokings;
    }

    public List<Alcohol> getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(List<Alcohol> alcohol) {
        this.alcohol = alcohol;
    }

    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }

    public List<LookingFor> getLookingFors() {
        return lookingFors;
    }

    public void setLookingFors(List<LookingFor> lookingFors) {
        this.lookingFors = lookingFors;
    }

    public List<Religion> getReligions() {
        return religions;
    }

    public void setReligions(List<Religion> religions) {
        this.religions = religions;
    }

    public boolean isReal() {
        return real;
    }

    public void setReal(boolean real) {
        this.real = real;
    }

    @Override
    public String toString() {
        return "SearchHelper{" +
                "sex='" + sex + '\'' +
                ", ageFrom=" + ageFrom +
                ", ageTo=" + ageTo +
                ", distance=" + distance +
                ", heightFrom=" + heightFrom +
                ", heightTo=" + heightTo +
                ", withAvatar=" + withAvatar +
                ", real=" + real +
                ", location=" + location +
                ", maritalStatuses=" + maritalStatuses +
                ", figures=" + figures +
                ", hairColors=" + hairColors +
                ", smokings=" + smokings +
                ", alcohol=" + alcohol +
                ", children=" + children +
                ", lookingFors=" + lookingFors +
                ", religions=" + religions +
                '}';
    }
}
