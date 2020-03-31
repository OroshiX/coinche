import 'package:fb_auth/fb_auth.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';

class FireAuthService with ChangeNotifier {
  FireAuthService._();

  factory FireAuthService() {
    return _fireAuthService;
  }

  static final _auth = FBAuth();
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
    return _auth.forgotPassword(email);
  }

  Future sendEmailVerification(BuildContext context) {
    return _auth.sendEmailVerification();
  }

  Future<MyAuthUser> signInWithCredentials(
      BuildContext context, String email, String password) async {
    var auth = await _auth.login(email, password);
    notifyListeners();
    AuthUser user = auth;
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
        (await FirebaseAuth.instance.signInWithCredential(credential)).user;

    notifyListeners();
    return MyAuthUser(
        uid: firebaseUser.uid,
        displayName: firebaseUser.displayName,
        email: firebaseUser.email,
        isEmailVerified: firebaseUser.isEmailVerified,
        isAnonymous: firebaseUser.isAnonymous);
  }

  Future<void> signOut(BuildContext context) {
    var logout = _auth.logout();
    notifyListeners();
    return logout;
  }

  Future<MyAuthUser> signUp(BuildContext context,
      {String email, String password}) async {
    var auth = await FirebaseAuth.instance
        .createUserWithEmailAndPassword(email: email, password: password);

    FirebaseUser user = auth.user;
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

class MyAuthUser {
  final String uid;
  final String displayName;
  final String email;
  final bool isEmailVerified;
  final bool isAnonymous;

  MyAuthUser({
    @required this.uid,
    @required this.displayName,
    @required this.email,
    @required this.isEmailVerified,
    @required this.isAnonymous,
  });

  @override
  String toString() {
    return '$displayName';
  }
}
