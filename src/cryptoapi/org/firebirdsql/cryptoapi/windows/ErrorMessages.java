package org.firebirdsql.cryptoapi.windows;

import org.firebirdsql.cryptoapi.windows.advapi.*;
import org.firebirdsql.cryptoapi.windows.sspi.SspiLib;

import static org.firebirdsql.cryptoapi.windows.sspi.SspiLib.*;


/**
* User: xmel
* Date: 7/11/12
* Time: 10:47 PM
*/
@SuppressWarnings("unused")
public enum ErrorMessages {
  E_UNKNOWN(-1, "Unknown code", "Код ошибки не может быть расшифрован"),
  E_OK(SspiLib.SEC_E_OK, "No error"),
  E_UNKNOWN_CREDENTIALS       (SEC_E_UNKNOWN_CREDENTIALS, "The credentials supplied to the package were not recognized"),
  E_NO_CREDENTIALS            (SEC_E_NO_CREDENTIALS,      "No credentials are available in the security package"),
  E_INVALID_HANDLE            (SEC_E_INVALID_HANDLE,      "The handle specified is invalid"),
  E_INCOMPLETE_MESSAGE        (0x80090318,               "The supplied message is incomplete.  The signature was not verified"),
  I_INCOMPLETE_CREDENTIALS    (0x00090320,               "The credentials supplied were not complete, and could not be verified. Additional information can be returned from the context"),
  I_CONTINUE_NEEDED           (SEC_I_CONTINUE_NEEDED,     "The function completed successfully, but must be called again to complete the context"),
  E_INVALID_TOKEN             (0x80090308,               "The token supplied to the function is invalid"),
  E_UNSUPPORTED_FUNCTION      (0x80090302,               "The function requested is not supported"),
  E_INSUFFICIENT_MEMORY       (SEC_E_INSUFFICIENT_MEMORY, "Not enough memory is available to complete this request"),
  CRYPT_E_INVALID_MSG_TYPE    (0x80091004,               "Invalid cryptographic message type"),
  E_EXPIRED                   (SEC_E_CONTEXT_EXPIRED,     "The context has expired and can no longer be used"),
  E_INTERNAL_ERROR            (0x80090304,               "The Local Security Authority cannot be contacted"),
  SEC_E_CERT_UNKNOWN          (0x80090327,               "An unknown error occurred while processing the certificate"),
  SEC_E_WRONG_PRINCIPAL       (0x80090322,               "The target principal name is incorrect"),
  SEC_E_MESSAGE_ALTERED       (0x8009030F,               "The message or signature supplied for verification has been altered"),
  SCARD_W_WRONG_CHV           (0x8010006B,               "The card cannot be accessed because the wrong PIN was presented"),
  NTE_BAD_KEYSET              (0x80090016,               "Key container does not exist"),
  ERROR_CANCELLED_BY_USER     (Winerror.ERROR_CANCELLED_BY_USER,  "The action was cancelled by the user")
  ;
  private int code;
  private String message;
  private String localizedMessage;

  ErrorMessages(int code, String message) {
    this.code = code;
    this.message = message;
  }

  ErrorMessages(int code, String message, String localizedMessage) {
    this.code = code;
    this.message = message;
    this.localizedMessage = localizedMessage;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public String getLocalizedMessage() {
    return localizedMessage != null ? localizedMessage : message;
  }

  @Override
  public String toString() {
    if (this != E_UNKNOWN)
      return String.format("%s (%s: 0x%08x)", getLocalizedMessage(), this.name(), code);
    else
      return String.format("%s", getLocalizedMessage());
  }

  public static ErrorMessages getError(long code) {
    for (ErrorMessages sec : ErrorMessages.values())
      if (code == sec.code)
        return sec;
    return ErrorMessages.E_UNKNOWN;
  }

  public static String getMessage(int code) {
    ErrorMessages sec = getError(code);
    if (sec != E_UNKNOWN)
      return sec.toString();
    else {
      final String s = Advapi.formatMessage(code);
      return String.format("%s (0x%08x)", s.isEmpty() ? sec.toString() : s, code);
    }
  }
}
