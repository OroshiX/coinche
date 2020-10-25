import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/service/network/server_communication.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';

class FireAuthService with ChangeNotifier {
  FireAuthService._();

  factory FireAuthService() {
    return _fireAuthService;
  }

  static final _auth = FirebaseAuth.instance;
  static final _fireAuthService = FireAuthService._();

  Future<MyAuthUser> getUser() async {
    var user = await _auth.currentUser();
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

  Future sendEmailVerification(BuildContext context, String email) {
    return _auth.sendSignInWithEmailLink(
        email: email,
        url: "<Url with domain from your firebase project>",
        handleCodeInApp: false,
        iOSBundleID: 'fr.hornik.FlutterCoinche',
        androidPackageName: 'fr.hornik.FlutterCoinche',
        androidInstallIfNotAvailable: true,
        androidMinimumVersion: "1");
  }

  Future<bool> _sendTokenIdToServer(IdTokenResult tokenId) {
    print("Send: ${tokenId.token}");
    return ServerCommunication.sendToken(tokenId);
  }

  Future<bool> _logoutFromServer() {
    return ServerCommunication.logout();
  }

  Future<MyAuthUser> signInWithCredentials(
      BuildContext context, String email, String password) async {
    var user = (await _auth.signInWithEmailAndPassword(
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

  Future<bool> signOut(BuildContext context) async {
    await _auth.signOut();
    var success = await _logoutFromServer();
    notifyListeners();
    return success;
  }

  Future<MyAuthUser> signUp(BuildContext context,
      {String email, String password}) async {
    var auth = await _auth.createUserWithEmailAndPassword(
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
  }
}

FireAuthService _fireAuthService;

FireAuthService getFireAuthService() {
  if (_fireAuthService == null) {
    _fireAuthService = FireAuthService();
  }
  return _fireAuthService;
}