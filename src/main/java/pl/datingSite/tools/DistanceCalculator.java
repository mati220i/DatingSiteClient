package pl.datingSite.tools;

import pl.datingSite.model.City;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DistanceCalculator {

    private City from, to;
    private double latitudeFrom, longitudeFrom, latitudeTo, longitudeTo;
    private List<City> cities;

    public DistanceCalculator() {

    }

    public DistanceCalculator(City from, City to) {
        this.from = from;
        this.to = to;
        latitudeFrom = calculateToDecimal(from.getLatitude());
        longitudeFrom = calculateToDecimal(from.getLongitude());

        latitudeTo = calculateToDecimal(to.getLatitude());
        longitudeTo = calculateToDecimal(to.getLongitude());
    }

    public DistanceCalculator(City from, City to, List<City> cities) {
        this.cities = cities;
        this.from = from;
        this.to = to;
        latitudeFrom = calculateToDecimal(from.getLatitude());
        longitudeFrom = calculateToDecimal(from.getLongitude());

        latitudeTo = calculateToDecimal(to.getLatitude());
        longitudeTo = calculateToDecimal(to.getLongitude());
    }

    private double calculateToDecimal(String coordinate) {
        String[] degrees = coordinate.split("°");
        double degreesF = Float.valueOf(degrees[0]);
        String[] minutes = degrees[1].split("'");
        double minutesF = Float.valueOf(minutes[0]);
        String seconds[] = minutes[1].split("''");
        double secondsF = Float.valueOf(seconds[0]);

        return  (secondsF / 3600) + (minutesF / 60) + degreesF;
    }

    public double getDistance() {
        return calculateDistance();
    }

    public double getDistance(City from, City to) {
        this.from = from;
        this.to = to;
        latitudeFrom = calculateToDecimal(from.getLatitude());
        longitudeFrom = calculateToDecimal(from.getLongitude());

        latitudeTo = calculateToDecimal(to.getLatitude());
        longitudeTo = calculateToDecimal(to.getLongitude());

        return calculateDistance();
    }

    private double calculateDistance() {
        return Math.sqrt(Math.pow((latitudeTo - latitudeFrom), 2) + Math.pow((Math.cos((latitudeFrom * Math.PI) / 180) * (longitudeTo - longitudeFrom)) , 2))
                * (40075.704 / 360);
    }

    public List<City> getNearbyCities(City from, int distance) {
        Iterator<City> iterator = cities.iterator();
        List<City> nearbyCities = new LinkedList<>();

        while (iterator.hasNext()) {
            City current = iterator.next();

            double currentDistance = getDistance(from, current);
            if(currentDistance <= distance)
                nearbyCities.add(current);
        }

        return nearbyCities;
    }

    public City getFrom() {
        return from;
    }

    public void setFrom(City from) {
        this.from = from;
    }

    public City getTo() {
        return to;
    }

    public void setTo(City to) {
        this.to = to;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
