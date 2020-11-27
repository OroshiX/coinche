class ServerErrors implements Exception {
  String cause;
  ServerErrors({this.cause = ""});
}

class InvalidEmail extends ServerErrors {
  InvalidEmail({String email = ""})
      : super(cause: "The email $email is malformed");
}

class WeakPassword extends ServerErrors {
  WeakPassword() : super(cause: "The password you chose is too weak");
}

class EmailAlreadyInUse extends ServerErrors {
  EmailAlreadyInUse({String email = ""})
      : super(cause: "The email $email is already in use");
}

class InvalidCredentialsException extends ServerErrors {
  InvalidCredentialsException() : super(cause: "Invalid credentials");
}

class UserNotFoundException extends ServerErrors {
  UserNotFoundException({String user = ""})
      : super(cause: "The user $user was not found");
}

class AccountDisabledException extends ServerErrors {
  AccountDisabledException({String email = ""})
      : super(
            cause:
                "The account associated with the email address $email is disabled");
}

class QuotaException extends ServerErrors {
  QuotaException({String cause = ""}) : super(cause: cause);
}

class UnknownException extends ServerErrors {
  UnknownException({String cause = ""}) : super(cause: cause);
}
