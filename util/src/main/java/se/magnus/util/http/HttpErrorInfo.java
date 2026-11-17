package se.magnus.util.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

public class HttpErrorInfo {
  private final ZonedDateTime timestamp;
  private final String path;
  private final HttpStatus httpStatus;
  private final String message;

  public HttpErrorInfo() {
    timestamp = null;
    this.httpStatus = null;
    this.path = null;
    this.message = null;
  }

  public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
    timestamp = ZonedDateTime.now();
    this.httpStatus = httpStatus;
    this.path = path;
    this.message = message;
  }

  // Jackson 3 (Spring Boot 4) no longer writes into final fields when deserializing
  // (ALLOW_FINAL_FIELDS_AS_MUTATORS is off by default), so tell it explicitly
  // which constructor to use when reading an error response back from JSON.
  @JsonCreator
  public HttpErrorInfo(
    @JsonProperty("timestamp") ZonedDateTime timestamp,
    @JsonProperty("path") String path,
    @JsonProperty("status") Integer status,
    @JsonProperty("message") String message) {
    this.timestamp = timestamp;
    this.path = path;
    this.httpStatus = status == null ? null : HttpStatus.resolve(status);
    this.message = message;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public String getPath() {
    return path;
  }

  public int getStatus() {
    return httpStatus.value();
  }

  public String getError() {
    return httpStatus.getReasonPhrase();
  }

  public String getMessage() {
    return message;
  }
}
