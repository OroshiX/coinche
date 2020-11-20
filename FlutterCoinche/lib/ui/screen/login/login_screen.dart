import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/ui/resources/colors.dart';
import 'package:coinche/ui/screen/all_games/all_games_screen.dart';
import 'package:coinche/ui/screen/testing_offline_game.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_login/flutter_login.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';
import 'package:provider/provider.dart';

class LoginScreen extends StatefulWidget {
  static const routeName = "/login";

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  late FireAuthService _userRepository;
  // late TextEditingController _emailController,
  //     _passwordController,
  //     _password2Controller;
  //
  // late bool _signUp;

  @override
  void initState() {
    _userRepository = getFireAuthService();
    // _emailController = TextEditingController();
    // _passwordController = TextEditingController();
    // _password2Controller = TextEditingController();
    // _signUp = false;
    context.read<LoginModel>().checkLoggedFuture.then((value) {
      if (context.read<LoginModel>().loggedIn) {
        Navigator.of(context)?.pushReplacementNamed(AllGamesScreen.routeName);
      }
    });
    super.initState();
  }

  Future<String?> _resetPassword(String email) {
    return _userRepository.resetPassword(email).then((value) {
      _showSuccess(
          "An email was sent to this address: $email. Please check it, and come back after");
      print("reset ok");
      return null;
    }).catchError((error) {
      _showError(error);
      return null;
    });
  }

  Future<String?> _doSignUp(String email, String password) {
    return _userRepository
        .signUp(context, email: email, password: password)
        .then((MyAuthUser user) {
      if (!user.isEmailVerified) {
        // send a confirmation email
        _userRepository.sendEmailVerification(context, email).then((value) {
          _showSuccess(
              "An email was sent to this address: $email. Please check it, and come back after");
        }).catchError((error) {
          _showError(error);
          return null;
        });
      } else {
        _loginSuccess(user);
        return null;
      }
    }).catchError((error) {
      _showError(error);
      return null;
    });
  }

  Future<String?> _signInWithCredentials(String email, String password) async {
    MyAuthUser myAuthUser;
    try {
      myAuthUser =
          await _userRepository.signInWithCredentials(context, email, password);
      _loginSuccess(myAuthUser);
      return null;
    } on ServerErrors catch (e) {
      _showError(e.cause);
      return null;
    } catch (e) {
      return "Unknown error";
    }
  }

  void _googleSignIn() {
    _userRepository
        .signInWithGoogle(context)
        .then(_loginSuccess)
        .catchError(_showError);
  }

  void _loginSuccess(MyAuthUser user) {
    _showSuccess(
        "You are now signed in as ${user.displayName ?? user.email}, and we set user!");

    context.read<LoginModel>().setUser(user);

    Navigator.of(context)?.popUntil((route) => route.isFirst);
    // navigate to other page
    Navigator.of(context)?.pushReplacementNamed(AllGamesScreen.routeName);
  }

  void _showError(error) {
    print(error);
    FlushUtil.showError(context, error.toString());
  }

  void _showSuccess(String message) {
    FlushUtil.showSuccess(context, message);
  }

  @override
  Widget build(BuildContext context) {
    final inputBorder = BorderRadius.vertical(
        bottom: Radius.circular(10), top: Radius.circular(20));
    return Stack(
      children: <Widget>[
        FlutterLogin(
          onSignup: (LoginData loginData) =>
              _doSignUp(loginData.name, loginData.password),
          onLogin: (LoginData loginData) =>
              _signInWithCredentials(loginData.name, loginData.password),
          onRecoverPassword: _resetPassword,
          messages: LoginMessages(
            usernameHint: "Email",
            passwordHint: "Password",
            confirmPasswordHint: "Confirm password",
            loginButton: "LOG IN",
            signupButton: "REGISTER",
            forgotPasswordButton: "Forgot your password?",
            recoverPasswordButton: "RECOVER PASSWORD",
            goBackButton: "BACK",
            confirmPasswordError: "Passwords should match",
            recoverPasswordDescription:
                "We'll send you a mail at this address to change your password.",
            recoverPasswordSuccess: "Now you can check your emails",
          ),
          title: "Coinchons!",
          theme: LoginTheme(
            pageColorLight: colorGradient2,
            pageColorDark: colorGradient1,
            primaryColor: colorTextDark,
            accentColor: colorGradient2,
            errorColor: Colors.amber,
            titleStyle: TextStyle(
              color: colorTextDark,
              fontFamily: 'Quicksand',
              letterSpacing: 4,
            ),
            bodyStyle: TextStyle(
              fontStyle: FontStyle.italic,
            ),
            textFieldStyle: TextStyle(
              color: colorTextDark,
            ),
            buttonStyle: TextStyle(
              fontWeight: FontWeight.w800,
              color: colorTextDark,
            ),
            cardTheme: CardTheme(
              color: colorLightBlue,
              elevation: 5,
              margin: EdgeInsets.only(top: 15),
              shape: ContinuousRectangleBorder(
                  borderRadius: BorderRadius.circular(100.0)),
            ),
            inputTheme: InputDecorationTheme(
              filled: true,
              fillColor: Colors.blueGrey.withOpacity(.1),
              contentPadding: EdgeInsets.zero,
              errorStyle: TextStyle(
                color: Colors.purple.shade900,
              ),
              labelStyle: TextStyle(fontSize: 12),
              enabledBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: colorTextDark, width: 5),
                borderRadius: inputBorder,
              ),
              focusedBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: colorTextDark, width: 6),
                borderRadius: inputBorder,
              ),
              errorBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: Colors.purple.shade700, width: 7),
                borderRadius: inputBorder,
              ),
              focusedErrorBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: Colors.purple.shade700, width: 8),
                borderRadius: inputBorder,
              ),
              disabledBorder: UnderlineInputBorder(
                borderSide: BorderSide(color: Colors.grey, width: 5),
                borderRadius: inputBorder,
              ),
            ),
            buttonTheme: LoginButtonTheme(
              splashColor: colorGradientMiddle,
              backgroundColor: colorGradient1,
              highlightColor: colorGradientMiddle,
              elevation: 9,
              highlightElevation: 6,
            ),
          ),
          emailValidator: (String? value) {
            if (value == null) return "Please fill your email";
            var regex = RegExp(
                r"^[\w^@]+(\.[\w^@]+)*(\+[\w^@]+(\.[\w^@]+)*)?@\w+(\.\w+)+$");
            if (!regex.hasMatch(value)) return "Email format invalid";
            return null;
          },
          showDebugButtons: false,
        ),
        Positioned(
          bottom: 20,
          left: 0,
          right: 0,
          child: Center(
            child: SignInButton(
              Buttons.Google,
              onPressed: _googleSignIn,
            ),
          ),
        ),
        Positioned(
            bottom: 50,
            child: Center(
              child: RaisedButton(
                onPressed: () {
                  Navigator.of(context)
                      ?.pushNamed(TestingOfflineGame.routeName);
                },
                child: Text("Test"),
              ),
            ))
      ],
    );
  }
}
