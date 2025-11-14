package com.weather.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a geographical location with city, country, and coordinates
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Location {

  private static final double MIN_LATITUDE = -90.0;
  private static final double MAX_LATITUDE = 90.0;
  private static final double MIN_LONGITUDE = -180.0;
  private static final double MAX_LONGITUDE = 180.0;
  private static final int COUNTRY_CODE_LENGTH = 2;
  private static final double EARTH_RADIUS_KM = 6371.0;
  private String city;
  private String country;
  private double latitude;
  private double longitude;

  public Location(String city, String country, double latitude, double longitude) {
    setCity(city);
    setCountry(country);
    setLatitude(latitude);
    setLongitude(longitude);
  }

  public void setCity(String city) {
    if (city == null) {
      throw new IllegalArgumentException("City name cannot be null");
    }
    if (city.trim().isEmpty()) {
      throw new IllegalArgumentException("City name cannot be empty");
    }
    this.city = city.trim();
  }

  public void setCountry(String country) {
    if (country == null) {
      throw new IllegalArgumentException("Country code cannot be null");
    }
    if (country.length() != COUNTRY_CODE_LENGTH) {
      throw new IllegalArgumentException("Country code must be exactly 2 characters (ISO 3166-1 alpha-2)");
    }
    this.country = country.toUpperCase();
  }

  public void setLatitude(double latitude) {
    if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    this.longitude = longitude;
  }

  /**
   * Calculates the distance to another location using the Haversine formula
   *
   * @param other the other location
   * @return distance in kilometers
   */
  public double distanceTo(Location other) {
    if (other == null) {
      throw new IllegalArgumentException("Other location cannot be null");
    }

    if (this.equals(other)) {
      return 0.0;
    }

    double currentLatitudeRadians = Math.toRadians(this.latitude);
    double haversineValue = getHaversineValue(other, currentLatitudeRadians);

    double angularDistance = 2 * Math.atan2(Math.sqrt(haversineValue), Math.sqrt(1 - haversineValue));

    return EARTH_RADIUS_KM * angularDistance;
  }

  private double getHaversineValue(Location other, double currentLatitudeRadians) {
    double otherLatitudeRadians = Math.toRadians(other.latitude);
    double latitudeDifferenceRadians = Math.toRadians(other.latitude - this.latitude);
    double longitudeDifferenceRadians = Math.toRadians(other.longitude - this.longitude);

    // Haversine formula
    return Math.sin(latitudeDifferenceRadians / 2) * Math.sin(latitudeDifferenceRadians / 2) +
            Math.cos(currentLatitudeRadians) * Math.cos(otherLatitudeRadians) *
                    Math.sin(longitudeDifferenceRadians / 2) * Math.sin(longitudeDifferenceRadians / 2);
  }

  /**
   * Returns the full location name
   *
   * @return city, country format
   */
  public String getFullName() {
    return city + ", " + country;
  }
}
