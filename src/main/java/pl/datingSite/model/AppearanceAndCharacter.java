package pl.datingSite.model;

import pl.datingSite.enums.*;

import java.util.Set;

public class AppearanceAndCharacter {
    private Long id;

    private Figure figure;
    private Integer height;
    private HairColor hairColor;
    private EyeColor eyeColor;
    private Smoking smoking;
    private Alcohol alcohol;
    private Children children;
    private Set<Holiday> holiday;
    private Set<LookingFor> lookingFor;
    private Set<MovieType> movieType;
    private Religion religion;
    private Set<Style> style;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public HairColor getHairColor() {
        return hairColor;
    }

    public void setHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
    }

    public EyeColor getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Smoking getSmoking() {
        return smoking;
    }

    public void setSmoking(Smoking smoking) {
        this.smoking = smoking;
    }

    public Alcohol getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(Alcohol alcohol) {
        this.alcohol = alcohol;
    }

    public Children getChildren() {
        return children;
    }

    public void setChildren(Children children) {
        this.children = children;
    }

    public Set<Holiday> getHoliday() {
        return holiday;
    }

    public void setHoliday(Set<Holiday> holiday) {
        this.holiday = holiday;
    }

    public Set<LookingFor> getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(Set<LookingFor> lookingFor) {
        this.lookingFor = lookingFor;
    }

    public Set<MovieType> getMovieType() {
        return movieType;
    }

    public void setMovieType(Set<MovieType> movieType) {
        this.movieType = movieType;
    }

    public Religion getReligion() {
        return religion;
    }

    public void setReligion(Religion religion) {
        this.religion = religion;
    }

    public Set<Style> getStyle() {
        return style;
    }

    public void setStyle(Set<Style> style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return "AppearanceAndCharacter{" +
                "id=" + id +
                ", figure=" + figure +
                ", height=" + height +
                ", hairColor=" + hairColor +
                ", eyeColor=" + eyeColor +
                ", smoking=" + smoking +
                ", alcohol=" + alcohol +
                ", children=" + children +
                ", holiday=" + holiday +
                ", lookingFor=" + lookingFor +
                ", movieType=" + movieType +
                ", religion=" + religion +
                ", style=" + style +
                '}';
    }
}
