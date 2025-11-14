package com.weather.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Location Tests")
public class LocationTest {

  private Location location;

  @BeforeEach
  void setUp() {
    location = new Location();
  }

  @Test
  @DisplayName("Should create location with valid coordinates")
  void shouldCreateLocationWithValidCoordinates() {
    // Given
    String city = "Rome";
    String country = "IT";
    double latitude = 41.9028;
    double longitude = 12.4964;

    // When
    location = new Location(city, country, latitude, longitude);

    // Then
    assertEquals(city, location.getCity());
    assertEquals(country, location.getCountry());
    assertEquals(latitude, location.getLatitude());
    assertEquals(longitude, location.getLongitude());
  }

  @Test
  @DisplayName("Should validate latitude range")
  void shouldValidateLatitudeRange() {
    // Given
    String city = "TestCity";
    String country = "IT";
    double validLatitude = 45.0;
    double invalidLatitudeHigh = 95.0;
    double invalidLatitudeLow = -95.0;
    double longitude = 12.0;

    // When & Then
    assertDoesNotThrow(() -> new Location(city, country, validLatitude, longitude));

    assertThrows(IllegalArgumentException.class,
            () -> new Location(city, country, invalidLatitudeHigh, longitude));
    assertThrows(IllegalArgumentException.class,
            () -> new Location(city, country, invalidLatitudeLow, longitude));
  }

  @Test
  @DisplayName("Should validate longitude range")
  void shouldValidateLongitudeRange() {
    // Given
    String city = "TestCity";
    String country = "IT";
    double latitude = 45.0;
    double validLongitude = 12.0;
    double invalidLongitudeHigh = 185.0;
    double invalidLongitudeLow = -185.0;

    // When & Then
    assertDoesNotThrow(() -> new Location(city, country, latitude, validLongitude));

    assertThrows(IllegalArgumentException.class,
            () -> new Location(city, country, latitude, invalidLongitudeHigh));
    assertThrows(IllegalArgumentException.class,
            () -> new Location(city, country, latitude, invalidLongitudeLow));
  }

  @Test
  @DisplayName("Should handle boundary latitude values")
  void shouldHandleBoundaryLatitudeValues() {
    // Given
    String city = "Pole";
    String country = "XX";
    double northPole = 90.0;
    double southPole = -90.0;
    double longitude = 0.0;

    // When & Then
    assertDoesNotThrow(() -> new Location(city, country, northPole, longitude));
    assertDoesNotThrow(() -> new Location(city, country, southPole, longitude));
  }

  @Test
  @DisplayName("Should handle boundary longitude values")
  void shouldHandleBoundaryLongitudeValues() {
    // Given
    String city = "DateLine";
    String country = "XX";
    double latitude = 0.0;
    double eastDateLine = 180.0;
    double westDateLine = -180.0;

    // When & Then
    assertDoesNotThrow(() -> new Location(city, country, latitude, eastDateLine));
    assertDoesNotThrow(() -> new Location(city, country, latitude, westDateLine));
  }

  @Test
  @DisplayName("Should throw exception for null city name")
  void shouldThrowExceptionForNullCityName() {
    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> new Location(null, "IT", 45.0, 12.0));
  }

  @Test
  @DisplayName("Should throw exception for empty city name")
  void shouldThrowExceptionForEmptyCityName() {
    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> new Location("", "IT", 45.0, 12.0));
  }

  @Test
  @DisplayName("Should throw exception for null country code")
  void shouldThrowExceptionForNullCountryCode() {
    // When & Then
    assertThrows(IllegalArgumentException.class,
            () -> new Location("Rome", null, 45.0, 12.0));
  }

  @Test
  @DisplayName("Should validate country code format")
  void shouldValidateCountryCodeFormat() {
    // Given
    String city = "Rome";
    String validCountryCode = "IT";
    String invalidCountryCodeShort = "I";
    String invalidCountryCodeLong = "ITA";
    double latitude = 41.9028;
    double longitude = 12.4964;

    // When & Then
    assertDoesNotThrow(() -> new Location(city, validCountryCode, latitude, longitude));

    assertThrows(IllegalArgumentException.class,
            () -> new Location(city, invalidCountryCodeShort, latitude, longitude));
    assertThrows(IllegalArgumentException.class,
            () -> new Location(city, invalidCountryCodeLong, latitude, longitude));
  }

  @Test
  @DisplayName("Should calculate distance between two locations")
  void shouldCalculateDistanceBetweenTwoLocations() {
    // Given
    Location rome = new Location("Rome", "IT", 41.9028, 12.4964);
    Location milan = new Location("Milan", "IT", 45.4642, 9.1900);

    // When
    double distance = rome.distanceTo(milan);

    // Then
    assertTrue(distance > 0);
    // Approximate distance between Rome and Milan is around 477 km
    assertTrue(distance > 400 && distance < 600);
  }

  @Test
  @DisplayName("Should return zero distance for same location")
  void shouldReturnZeroDistanceForSameLocation() {
    // Given
    Location location1 = new Location("Rome", "IT", 41.9028, 12.4964);
    Location location2 = new Location("Rome", "IT", 41.9028, 12.4964);

    // When
    double distance = location1.distanceTo(location2);

    // Then
    assertEquals(0.0, distance, 0.1);
  }

  @Test
  @DisplayName("Should handle equals and hashCode correctly")
  void shouldHandleEqualsAndHashCodeCorrectly() {
    // Given
    Location location1 = new Location("Rome", "IT", 41.9028, 12.4964);
    Location location2 = new Location("Rome", "IT", 41.9028, 12.4964);
    Location location3 = new Location("Milan", "IT", 45.4642, 9.1900);

    // When & Then
    assertEquals(location1, location2);
    assertNotEquals(location1, location3);
    assertEquals(location1.hashCode(), location2.hashCode());
    assertNotEquals(location1.hashCode(), location3.hashCode());
  }
}
