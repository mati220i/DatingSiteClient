package pl.datingSite.tools;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FilenameUtils;
import pl.datingSite.enums.*;
import pl.datingSite.model.AppearanceAndCharacter;
import pl.datingSite.model.City;
import pl.datingSite.model.User;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class DatabaseGenerator {

    private List<String[]> listOfNames, listOfFemaleSurnames, listOfMaleSurnames, listOfInterests;
    private List<String> femaleNames = new LinkedList<>(), maleNames = new LinkedList<>();
    private List<String> femaleSurnames = new LinkedList<>(), maleSurnames = new LinkedList<>();
    private List<File> femaleImages, maleImages;
    private List<City> cities;

    private User user;

    private Random generator = new Random();
    private CSVReader csvReader;
    private DistanceCalculator distanceCalculator;

    public DatabaseGenerator(List<City> cities) {
        File file = new File("src/main/resources/otherData/namesPl.csv");
        this.cities = cities;
        csvReader = new CSVReader(file);
        distanceCalculator = new DistanceCalculator();
        distanceCalculator.setCities(cities);
    }

    public DatabaseGenerator(List<City> cities, List<File> femaleImages, List<File> maleImages) {
        File file = new File("src/main/resources/otherData/namesPl.csv");
        this.cities = cities;
        csvReader = new CSVReader(file);
        distanceCalculator = new DistanceCalculator();
        distanceCalculator.setCities(cities);this.femaleImages = femaleImages;
        this.maleImages = maleImages;
    }

    public DatabaseGenerator(File file, List<City> cities, List<File> femaleImages, List<File> maleImages) {
        csvReader = new CSVReader(file);
        this.femaleImages = femaleImages;
        this.maleImages = maleImages;
        this.cities = cities;
        distanceCalculator = new DistanceCalculator();
        distanceCalculator.setCities(cities);
    }

    public void generate() {
        generateData(100, false, null);
    }

    public void generate(int quantity) {
        generateData(quantity, false, null);
    }

    public void generate(int quantity, boolean nearby, City city) {
        generateData(quantity, nearby, city);
    }

    private void generateData(int quantity, boolean nearby, City city) {
        this.listOfNames = csvReader.read();
        this.listOfFemaleSurnames = csvReader.read(new File("src/main/resources/otherData/femaleSurnames.csv"));
        this.listOfMaleSurnames = csvReader.read(new File("src/main/resources/otherData/maleSurnames.csv"));
        this.listOfInterests = csvReader.read(new File("src/main/resources/otherData/interests.csv"));

        prepareNames();
        prepareSurnames();

        for(int i = 0; i < quantity; i++) {
            user = new User();
            user.setSex(generateSex());
            user.setName(generateName());
            user.setSurname(generateSurname());
            prepareLoginAndPassword();
            user.setEmail(generateEmail());
            user.setDateOfBirth(generateDateOfBirth());
            user.setZodiacSign(generateZodiacSign());
            user.setProfession(generateProfession());
            user.setMaritalStatus(generateMaritalStatus());
            user.setEducation(generateEducation());
            user.setCity(generateCity(nearby, city));
            user.setInterests(new HashSet<>(generateInterests()));
            user.setAppearanceAndCharacter(new AppearanceAndCharacter());
            user.getAppearanceAndCharacter().setFigure(generateFigure());
            user.getAppearanceAndCharacter().setHairColor(generateHairColor());
            user.getAppearanceAndCharacter().setEyeColor(generateEyeColor());
            user.getAppearanceAndCharacter().setSmoking(generateSmoking());
            user.getAppearanceAndCharacter().setAlcohol(generateAlkohol());
            user.getAppearanceAndCharacter().setChildren(generateChildren());
            user.getAppearanceAndCharacter().setReligion(generateReligion());
            user.getAppearanceAndCharacter().setHeight(generateHeight());
            user.getAppearanceAndCharacter().setHoliday(generateHoliday());
            user.getAppearanceAndCharacter().setLookingFor(generateLookingFor());
            user.getAppearanceAndCharacter().setMovieType(generateMovieType());
            user.getAppearanceAndCharacter().setStyle(generateStyle());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            File image = generateImage();
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(new Image(image.toURI().toString()), null), FilenameUtils.getExtension(image.toURI().toString()), stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setAvatar(stream.toByteArray());
        }
    }

    public void generatePrepareData(City nearCity) {
        this.listOfNames = csvReader.read();
        this.listOfFemaleSurnames = csvReader.read(new File("src/main/resources/otherData/femaleSurnames.csv"));
        this.listOfMaleSurnames = csvReader.read(new File("src/main/resources/otherData/maleSurnames.csv"));
        this.listOfInterests = csvReader.read(new File("src/main/resources/otherData/interests.csv"));
        this.femaleImages = new ArrayList<>(Arrays.asList(new File("src/main/resources/images/avatars/woman").listFiles()));
        this.maleImages = new ArrayList<>(Arrays.asList(new File("src/main/resources/images/avatars/man").listFiles()));

        prepareNames();
        prepareSurnames();

        generateWoman(true, nearCity, true);
        generateWoman(false, nearCity, true);
        generateWoman(false, nearCity, false);
        generateMan(true, nearCity, true);
        generateMan(false, nearCity, true);
        generateMan(false, nearCity, false);

    }

    private void generateWoman(boolean nearby, City nearCity, boolean withAvatar) {
        for(int i = 0; i < femaleImages.size(); i++) {
            File woman = femaleImages.get(i);

            user = new User();
            user.setSex("Kobieta");
            user.setName(generateName());
            user.setSurname(generateSurname());
            prepareLoginAndPassword();
            user.setEmail(generateEmail());

            user.setDateOfBirth(generatePrepareDateOfBirth(woman, withAvatar));

            user.setZodiacSign(generateZodiacSign());
            user.setProfession(generateProfession());
            user.setMaritalStatus(generateMaritalStatus());
            user.setEducation(generateEducation());

            user.setCity(generateCity(nearby, nearCity));
            user.setInterests(new HashSet<>(generateInterests()));
            user.setAppearanceAndCharacter(new AppearanceAndCharacter());
            user.getAppearanceAndCharacter().setFigure(generateFigure());
            user.getAppearanceAndCharacter().setHairColor(generateHairColor());
            user.getAppearanceAndCharacter().setEyeColor(generateEyeColor());
            user.getAppearanceAndCharacter().setSmoking(generateSmoking());
            user.getAppearanceAndCharacter().setAlcohol(generateAlkohol());
            user.getAppearanceAndCharacter().setChildren(generateChildren());
            user.getAppearanceAndCharacter().setReligion(generateReligion());
            user.getAppearanceAndCharacter().setHeight(generateHeight());
            user.getAppearanceAndCharacter().setHoliday(generateHoliday());
            user.getAppearanceAndCharacter().setLookingFor(generateLookingFor());
            user.getAppearanceAndCharacter().setMovieType(generateMovieType());
            user.getAppearanceAndCharacter().setStyle(generateStyle());

            setAvatar(withAvatar, woman);
        }
    }

    private void generateMan(boolean near, City nearCity, boolean withAvatar) {
        for(int i = 0; i < maleImages.size(); i++) {
            File man = maleImages.get(i);

            user = new User();
            user.setSex("Mężczyzna");
            user.setName(generateName());
            user.setSurname(generateSurname());
            prepareLoginAndPassword();
            user.setEmail(generateEmail());

            user.setDateOfBirth(generatePrepareDateOfBirth(man, withAvatar));
            user.setZodiacSign(generateZodiacSign());
            user.setProfession(generateProfession());
            user.setMaritalStatus(generateMaritalStatus());
            user.setEducation(generateEducation());

            user.setCity(generateCity(near, nearCity));
            user.setInterests(new HashSet<>(generateInterests()));
            user.setAppearanceAndCharacter(new AppearanceAndCharacter());
            user.getAppearanceAndCharacter().setFigure(generateFigure());
            user.getAppearanceAndCharacter().setHairColor(generateHairColor());
            user.getAppearanceAndCharacter().setEyeColor(generateEyeColor());
            user.getAppearanceAndCharacter().setSmoking(generateSmoking());
            user.getAppearanceAndCharacter().setAlcohol(generateAlkohol());
            user.getAppearanceAndCharacter().setChildren(generateChildren());
            user.getAppearanceAndCharacter().setReligion(generateReligion());
            user.getAppearanceAndCharacter().setHeight(generateHeight());
            user.getAppearanceAndCharacter().setHoliday(generateHoliday());
            user.getAppearanceAndCharacter().setLookingFor(generateLookingFor());
            user.getAppearanceAndCharacter().setMovieType(generateMovieType());
            user.getAppearanceAndCharacter().setStyle(generateStyle());

            setAvatar(withAvatar, man);
        }
    }

    private void setAvatar(boolean withAvatar, File file) {
        if(withAvatar) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(new Image(file.toURI().toString()), null), FilenameUtils.getExtension(file.toURI().toString()), stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setAvatar(stream.toByteArray());
        }
    }

    private String generateSex() {
        String sex = "";
        int val = generator.nextInt(2);

        if(val == 0)
            sex = "Kobieta";
        else
            sex = "Mężczyzna";
        return sex;
    }

    private String generateName() {
        int size;
        if(user.getSex().equals("Kobieta")) {
            size = this.femaleNames.size();
            return this.femaleNames.get(generator.nextInt(size));
        } else {
            size = this.maleNames.size();
            return this.maleNames.get(generator.nextInt(size));
        }
    }

    private String generateSurname() {
        int size;
        if(user.getSex().equals("Kobieta")) {
            size = this.femaleSurnames.size();
            return this.femaleSurnames.get(generator.nextInt(size - 1));
        } else {
            size = this.maleSurnames.size();
            return this.maleSurnames.get(generator.nextInt(size - 1));
        }
    }

    private void prepareNames() {
        Iterator<String[]> iterator = listOfNames.iterator();

        while (iterator.hasNext()) {
            String[] data = iterator.next();
            if(data[1].equals("K"))
                femaleNames.add(data[0]);
            if(data[1].equals("M"))
                maleNames.add(data[0]);

        }
    }

    private void prepareSurnames() {
        Iterator<String[]> iterator = listOfFemaleSurnames.iterator();

        while (iterator.hasNext())
            femaleSurnames.add(iterator.next()[0]);

        iterator = listOfMaleSurnames.iterator();
        while (iterator.hasNext())
            maleSurnames.add(iterator.next()[0]);
    }

    private void prepareLoginAndPassword() {
        int endNameChar = user.getName().length() / 2;
        int endSurnameChar = user.getSurname().length() / 2;
        String data = user.getName().toLowerCase().substring(0, endNameChar) + user.getSurname().toLowerCase().substring(0, endSurnameChar);
        if(data.contains("�")) {
            data = data.replace("�", "");
        }
        // TODO jesli jest to dodaj literke
        user.setUsername(data);
        user.setPassword(data);
    }

    private String generateEmail() {
        String[] domain = {"wp.pl", "onet.pl", "o2.pl", "interia.pl", "home.pl", "gazeta.pl", "g.pl", "mail.com",
                "hotmail.com", "aol.com", "e-post.pl", "poczta.duno.pl", "poczta.pl", "migmail.pl", "yahoo.com",
                "gmail.com", };

        return user.getUsername() + "@" + domain[generator.nextInt(domain.length)];
    }

    private Date generateDateOfBirth() {
        GregorianCalendar gc = new GregorianCalendar();

        int minYear = LocalDate.now().getYear() - 90;
        int maxYear = LocalDate.now().getYear() - 18;
        int year = generator.nextInt(90) + minYear;
        if(year > maxYear)
            year = maxYear;

        gc.set(gc.YEAR, year);

        int dayOfYear = generator.nextInt(gc.getActualMaximum(gc.DAY_OF_YEAR) - 1) + 1;
        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        int month = generator.nextInt(12) + 1;
        gc.set(gc.MONTH, month);

        return gc.getTime();
    }

    private Date generatePrepareDateOfBirth(File file, boolean withAvatar) {
        int minYear = 0, maxYear = 0, year = 0;
        GregorianCalendar gc = new GregorianCalendar();

        if(withAvatar) {
            String[] data = prepareAgeFromFile(file);

            minYear = LocalDate.now().getYear() - Integer.valueOf(data[2]);
            maxYear = LocalDate.now().getYear() - Integer.valueOf(data[1]);
        } else {
            minYear = LocalDate.now().getYear() - 90;
            maxYear = LocalDate.now().getYear() - 18;
        }

        year = generator.nextInt((maxYear - minYear)) + minYear;

        gc.set(gc.YEAR, year);

        int dayOfYear = generator.nextInt(gc.getActualMaximum(gc.DAY_OF_YEAR) - 1) + 1;
        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        int month = generator.nextInt(12) + 1;
        gc.set(gc.MONTH, month);

        return gc.getTime();
    }

    private String[] prepareAgeFromFile(File file) {
        return file.getName().split("-");
    }

    private ZodiacSign generateZodiacSign() {
        int randVal = generator.nextInt(ZodiacSign.values().length + 1);
        int enumQnt = ZodiacSign.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return ZodiacSign.values()[randVal];
    }

    private Profession generateProfession() {
        int randVal = generator.nextInt(Profession.values().length + 1);
        int enumQnt = Profession.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return Profession.values()[randVal];
    }

    private MaritalStatus generateMaritalStatus() {
        int randVal = generator.nextInt(MaritalStatus.values().length + 1);
        int enumQnt = MaritalStatus.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return MaritalStatus.values()[randVal];
    }

    private Education generateEducation() {
        int randVal = generator.nextInt(Education.values().length + 1);
        int enumQnt = Education.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return Education.values()[randVal];
    }

    private City generateCity(boolean nearby, City city) {
        int numberOfCities = cities.size();
        int distance = 100;

        if(nearby) {
            List<City> nearbyCities = distanceCalculator.getNearbyCities(city, distance);
            return  nearbyCities.get(generator.nextInt(nearbyCities.size()));
        } else
            return cities.get(generator.nextInt(numberOfCities));
    }

    private List<String> generateInterests() {
        int size = listOfInterests.size();
        int numberOfInterests = generator.nextInt(15);
        List<String> interests = new LinkedList<>();

        for(int i = 0; i < numberOfInterests; i++)
            interests.add(listOfInterests.get(generator.nextInt(size))[0]);

        return interests;
    }

    private Figure generateFigure() {
        int randVal = generator.nextInt(Figure.values().length + 1);
        int enumQnt = Figure.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return Figure.values()[randVal];
    }

    private HairColor generateHairColor() {
        int randVal = generator.nextInt(HairColor.values().length + 1);
        int enumQnt = HairColor.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return HairColor.values()[randVal];
    }

    private EyeColor generateEyeColor() {
        int randVal = generator.nextInt(EyeColor.values().length + 1);
        int enumQnt = EyeColor.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return EyeColor.values()[randVal];
    }

    private Smoking generateSmoking() {
        int randVal = generator.nextInt(Smoking.values().length + 1);
        int enumQnt = Smoking.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return Smoking.values()[randVal];
    }

    private Alcohol generateAlkohol() {
        int randVal = generator.nextInt(Alcohol.values().length + 1);
        int enumQnt = Alcohol.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return Alcohol.values()[randVal];
    }

    private Children generateChildren() {
        int randVal = generator.nextInt(Children.values().length + 1);
        int enumQnt = Children.values().length;

        if(randVal >= enumQnt)
            return null; // someone may not give

        return Children.values()[randVal];
    }

    private Religion generateReligion() {
        int randVal = generator.nextInt(Religion.values().length + 1);
        int enumQnt = Religion.values().length;
        int christianity = generator.nextInt(4);

        if(randVal >= enumQnt)
            return null; // someone may not give
        if(christianity != 0)
            return Religion.CHRISTIANITY;

        return Religion.values()[randVal];
    }

    private Integer generateHeight() {
        return generator.nextInt(60) + 150;
    }

    private Set<Holiday> generateHoliday() {
        int size = Holiday.values().length;
        int numbersOfHoliday = generator.nextInt(size);
        Set<Holiday> holidays = new HashSet<>();

        for (int i = 0; i < numbersOfHoliday; i++)
            holidays.add(Holiday.values()[generator.nextInt(size)]);

        return holidays;
    }

    private Set<LookingFor> generateLookingFor() {
        int size = LookingFor.values().length;
        int numbersOfLookingFor = generator.nextInt(size);
        Set<LookingFor> lookingFors = new HashSet<>();

        for (int i = 0; i < numbersOfLookingFor; i++)
            lookingFors.add(LookingFor.values()[generator.nextInt(size)]);

        return lookingFors;
    }

    private Set<MovieType> generateMovieType() {
        int size = MovieType.values().length;
        int numbersOfMovieType = generator.nextInt(size);
        Set<MovieType> movieTypes = new HashSet<>();

        for (int i = 0; i < numbersOfMovieType; i++)
            movieTypes.add(MovieType.values()[generator.nextInt(size)]);

        return movieTypes;
    }

    private Set<Style> generateStyle() {
        int size = Style.values().length;
        int numbersOfStyle = generator.nextInt(size);
        Set<Style> styles = new HashSet<>();

        for (int i = 0; i < numbersOfStyle; i++)
            styles.add(Style.values()[generator.nextInt(size)]);

        return styles;
    }

    private File generateImage() {
        if(user.getSex().equals("Kobieta"))
            return femaleImages.get(generator.nextInt(femaleImages.size()));
        else
            return maleImages.get(generator.nextInt(maleImages.size()));
    }

}
