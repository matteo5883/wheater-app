package com.weather.service;

/**
 * Exception thrown when network connectivity issues occur
 */
public class NetworkException extends RuntimeException {

  public NetworkException(String message) {
    super(message);
  }

  public NetworkException(String message, Throwable cause) {
    super(message, cause);
  }
}
