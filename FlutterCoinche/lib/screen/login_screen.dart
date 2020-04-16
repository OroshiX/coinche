import 'package:FlutterCoinche/bloc/games_bloc.dart';
import 'package:FlutterCoinche/fire/fire_auth_service.dart';
import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/screen/all_games_screen.dart';
import 'package:bloc_provider/bloc_provider.dart';
import 'package:flushbar/flushbar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_login/flutter_login.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';

class LoginScreen extends StatefulWidget {
  static const routeName = "/login";

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  double height, width;
  FireAuthService userRepository;
  TextEditingController emailController,
      passwordController,
      password2Controller;

  bool signUp;

  @override
  void initState() {
    userRepository = getFireAuthService();
    emailController = TextEditingController();
    passwordController = TextEditingController();
    password2Controller = TextEditingController();
    signUp = false;
    super.initState();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    height = MediaQuery.of(context).size.height;
    width = MediaQuery.of(context).size.width;
  }

  _resetPassword(String email) {
    userRepository.resetPassword(email).then((value) {
      _showSuccess(
          "An email was sent to this address: $email. Please check it, and come back after");
      print("reset ok");
    }).catchError((error) => _showError(error));
  }

  _signUp(String email, String password) {
    userRepository
        .signUp(context, email: email, password: password)
        .then((MyAuthUser user) {
      if (!user.isEmailVerified) {
        // send a confirmation email
        userRepository.sendEmailVerification(context, email).then((value) {
          _showSuccess(
              "An email was sent to this address: $email. Please check it, and come back after");
        }).catchError((error) => _showError(error));
      } else {
        _loginSuccess(user);
      }
    }).catchError((error) => _showError(error));
  }

  _signInWithCredentials(String email, String password) {
    userRepository
        .signInWithCredentials(context, email, password)
        .then((user) async {
      _loginSuccess(user);
    }).catchError((error) => _showError(error));
  }

  _googleSignIn() {
    userRepository.signInWithGoogle(context).then((value) {
      _loginSuccess(value);
    }).catchError((error) => _showError(error));
  }

  _loginSuccess(MyAuthUser user) {
    _showSuccess(
        "You are now signed in as ${user.displayName != null ? user.displayName : user.email}, and we set user!");

    BlocProvider.of<GamesBloc>(context).setUser(user);

    Navigator.of(context).popUntil((route) => route.isFirst);
    // navigate to other page
    Navigator.of(context).pushReplacementNamed(AllGamesScreen.routeName);
  }

  void _showWarning(String warningMessage) {
    Flushbar(
      margin: EdgeInsets.all(8),
      borderRadius: 8,
      icon: Icon(
        Icons.warning,
        size: 28,
        color: Colors.orange,
      ),
      leftBarIndicatorColor: Colors.orange,
      message: warningMessage,
      duration: Duration(seconds: 3),
    )..show(context);
  }

  _showError(error) {
    print(error);
    Flushbar(
      message: error.toString(),
      leftBarIndicatorColor: Colors.red,
      duration: Duration(seconds: 3),
      margin: EdgeInsets.all(8),
      borderRadius: 8,
      icon: Icon(
        Icons.error,
        color: Colors.red,
      ),
    )..show(context);
  }

  _showSuccess(String message) {
    Flushbar(
      message: message,
      leftBarIndicatorColor: Colors.lightGreen,
      duration: Duration(seconds: 3),
      margin: EdgeInsets.all(8),
      borderRadius: 8,
      icon: Icon(
        Icons.check,
        color: Colors.lightGreen,
      ),
    )..show(context);
  }

  @override
  Widget build(BuildContext context) {
    final inputBorder = BorderRadius.vertical(
        bottom: Radius.circular(10), top: Radius.circular(20));
    return Stack(
      children: <Widget>[
        FlutterLogin(
          onSignup: (LoginData loginData) =>
              _signUp(loginData.name, loginData.password),
          onLogin: (LoginData loginData) =>
              _signInWithCredentials(loginData.name, loginData.password),
          onRecoverPassword: (String email) => _resetPassword(email),
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
//              shadows: [Shadow(color: Colors.yellow, blurRadius: 2)],
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
//                backgroundColor: Colors.orange,
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
//              shape: BeveledRectangleBorder(
//                borderRadius: BorderRadius.circular(10),
//              ),
            ),
          ),
          emailValidator: (value) {
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
              onPressed: () => _googleSignIn(),
            ),
          ),
        ),
      ],
    );
  }
}
