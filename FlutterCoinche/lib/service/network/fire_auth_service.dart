import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/service/network/server_communication.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_sign_in/google_sign_in.dart';

class FireAuthService extends ChangeNotifier {
  FireAuthService._();

  factory FireAuthService() {
    return _fireAuthService;
  }

  static final _auth = FirebaseAuth.instance;
  static final _fireAuthService = FireAuthService._();

  Future<MyAuthUser?> getUser() async {
    FirebaseUser? user = await _auth.currentUser();
    // ignore: unnecessary_null_comparison
    return user != null
        ? MyAuthUser(
            uid: user.uid,
            displayName: user.displayName,
            email: user.email,
            isEmailVerified: user.isEmailVerified,
            isAnonymous: user.isAnonymous)
        : null;
  }

  Future<dynamic> resetPassword(String email) {
    return _auth.sendPasswordResetEmail(email: email);
  }

  Future<void> sendEmailVerification(BuildContext context, String email) async {
    var firebaseUser = await _auth.currentUser();
    await firebaseUser.sendEmailVerification();
    // _auth.currentUser().then((value) => value.sendEmailVerification());
    // return _auth.sendSignInWithEmailLink(
    //     email: email,
    //     url: "https://coinche-47d27.web.app",
    //     handleCodeInApp: true,
    //     iOSBundleID: 'fr.hornik.FlutterCoinche',
    //     androidPackageName: 'fr.hornik.FlutterCoinche',
    //     androidInstallIfNotAvailable: true,
    //     androidMinimumVersion: "1");
  }

  Future<void> _sendTokenIdToServer(IdTokenResult tokenId) {
    return ServerCommunication.sendToken(tokenId, onSuccess: () {
      // todo
      if (kDebugMode) print("sent successfully token");
    }, onError: (message) {
      // todo
      print("Problem sending token ${tokenId.token} to server");
    });
  }

  Future<void> _logoutFromServer(
      {required void Function() onSuccess,
      required void Function(String message) onError}) {
    return ServerCommunication.logout(
        onSuccess: onSuccess, onError: onError);
  }

  Future<MyAuthUser> signInWithCredentials(
      BuildContext context, String email, String password) async {
    FirebaseUser user;
    try {
      user = (await _auth.signInWithEmailAndPassword(
              email: email, password: password))
          .user;
      var idToken = await user.getIdToken();
      await _sendTokenIdToServer(idToken);
      notifyListeners();
      return MyAuthUser(
          uid: user.uid,
          displayName: user.displayName,
          email: user.email,
          isEmailVerified: user.isEmailVerified,
          isAnonymous: user.isAnonymous);
    } on PlatformException catch (e) {
      switch (e.code) {
        case "ERROR_INVALID_EMAIL":
          throw InvalidEmail(email: email);
        case "ERROR_WRONG_PASSWORD":
          throw InvalidCredentialsException();
        case "ERROR_USER_NOT_FOUND":
          throw UserNotFoundException(user: email);
        case "ERROR_USER_DISABLED":
          throw AccountDisabledException(email: email);
        case "ERROR_TOO_MANY_REQUESTS":
          throw QuotaException(cause: "Too many requests");
        case "ERROR_OPERATION_NOT_ALLOWED":
          throw UnknownException(cause: "Operation not allowed");
        default:
          throw UnknownException();
      }
    } catch (e) {
      throw UnknownException();
    }
  }

  Future<MyAuthUser> signInWithGoogle(BuildContext context) async {
    GoogleSignInAccount googleUser = await GoogleSignIn().signIn();
    GoogleSignInAuthentication googleAuth = await googleUser.authentication;
    AuthCredential credential = GoogleAuthProvider.getCredential(
        idToken: googleAuth.idToken, accessToken: googleAuth.accessToken);
    FirebaseUser firebaseUser =
        (await _auth.signInWithCredential(credential)).user;
    var idToken = await firebaseUser.getIdToken();
    await _sendTokenIdToServer(idToken);
    notifyListeners();
    return MyAuthUser(
        uid: firebaseUser.uid,
        displayName: firebaseUser.displayName,
        email: firebaseUser.email,
        isEmailVerified: firebaseUser.isEmailVerified,
        isAnonymous: firebaseUser.isAnonymous);
  }

  Future<void> signOut(BuildContext context,
      {required void Function() onSuccess,
      required void Function(String message) onError}) async {
    await _auth.signOut();
    await _logoutFromServer(onSuccess: onSuccess, onError: onError);
    notifyListeners();
  }

  Future<MyAuthUser> signUp(BuildContext context,
      {required String email, required String password}) async {
    AuthResult auth;
    try {
      auth = await _auth.createUserWithEmailAndPassword(
          email: email, password: password);

      FirebaseUser user = auth.user;
      var idToken = await user.getIdToken();
      await _sendTokenIdToServer(idToken);
      notifyListeners();
      return MyAuthUser(
          uid: user.uid,
          displayName: user.displayName,
          email: user.email,
          isEmailVerified: user.isEmailVerified,
          isAnonymous: user.isAnonymous);
    } on PlatformException catch (e) {
      switch (e.code) {
        case "ERROR_WEAK_PASSWORD":
          throw WeakPassword();
        case "ERROR_INVALID_EMAIL":
          throw InvalidEmail(email: email);
        case "ERROR_EMAIL_ALREADY_IN_USE":
          throw EmailAlreadyInUse(email: email);
        default:
          throw UnknownException();
      }
    } catch (e) {
      throw UnknownException();
    }
  }
}

FireAuthService? _fireAuthService;

FireAuthService getFireAuthService() {
  _fireAuthService ??= FireAuthService();
  return _fireAuthService!;
}
