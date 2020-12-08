package api;

import java.io.Serializable;

public class GeoLocation implements geo_location, Serializable {
    double x,y,z;

    public GeoLocation() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public GeoLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GeoLocation(String x, String y, String z) {
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
        this.z = Double.parseDouble(z);
    }

    public GeoLocation(geo_location g) {
        this.x = g.x();
        this.y = g.y();
        this.z = g.z();
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public double distance(geo_location g) {
        double newX = Math.pow(this.x - g.x(),2);
        double newY = Math.pow(this.y - g.y(),2);
        double newZ = Math.pow(this.z - g.z(),2);
        return Math.sqrt(newX + newY + newZ);
    }

    @Override
    public String toString() {
        return "" + x + "," + y + "," + z + "";
    }
}
