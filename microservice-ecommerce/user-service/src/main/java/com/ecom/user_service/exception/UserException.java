package com.ecom.user_service.exception;

public class UserException extends BaseException {

  public UserException(String code, String message) {
    super("user." + code, message);
  }

  public UserException(String code, String message, Throwable cause) {
    super("user." + code, message, cause);
  }
}
