import 'package:FlutterCoinche/fire/fire_auth_service.dart';
import 'package:FlutterCoinche/screen/all_games_screen.dart';
import 'package:flushbar/flushbar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
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
        "You are now signed in as ${user.displayName != null ? user.displayName : user.email}");
    // navigate to other page
    Navigator.of(context).pushNamed(AllGamesScreen.routeName);
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
    return Scaffold(
      backgroundColor: Color.fromRGBO(247, 245, 237, 1),
      body: SafeArea(
        child: SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Container(
            constraints: BoxConstraints.tight(Size(width, height)),
            child: Column(
              children: <Widget>[
                SizedBox(
                  height: height * (signUp ? 0.65 : 0.54),
                  child: ClipRRect(
                    borderRadius:
                        BorderRadius.only(bottomRight: Radius.circular(150)),
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 25),
                      decoration: BoxDecoration(
                          gradient: LinearGradient(
                              begin: Alignment(-1, -1),
                              end: Alignment(1, 1),
                              colors: [
                            Color.fromRGBO(255, 237, 167, 1),
                            Color.fromRGBO(249, 177, 125, 1),
                          ])),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: <Widget>[
                          Expanded(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: <Widget>[
                                Padding(
                                  padding: const EdgeInsets.symmetric(
                                      vertical: 25, horizontal: 5),
                                  child: Center(
                                    child: Text(
                                      signUp ? "Sign Up" : "Sign In",
                                      style: TextStyle(
                                          fontWeight: FontWeight.bold,
                                          fontStyle: FontStyle.italic),
                                    ),
                                  ),
                                ),
                                Text("Email"),
                                SizedBox(
                                  height: 10,
                                ),
                                TextField(
                                  controller: emailController,
                                  onChanged: (value) {
                                    print("$value has changed");
                                  },
                                ),
                                SizedBox(
                                  height: 20,
                                ),
                                Text("Password"),
                                SizedBox(
                                  height: 10,
                                ),
                                Stack(children: [
                                  TextField(
                                    controller: passwordController,
                                    obscureText: true,
                                    onChanged: (value) {
                                      print("$value has changed");
                                    },
                                  ),
                                  Positioned(
                                    right: 2,
                                    child: IconButton(
                                      onPressed: () {
                                        print("pressed help");
                                      },
                                      icon: Icon(
                                        Icons.help_outline,
                                        color: Colors.black54,
                                      ),
                                    ),
                                  )
                                ]),
                                if (signUp) SizedBox(height: 20),
                                if (signUp) Text("Confirm Password"),
                                if (signUp) SizedBox(height: 10),
                                if (signUp)
                                  Stack(children: [
                                    TextField(
                                      controller: password2Controller,
                                      obscureText: true,
                                      onChanged: (value) {
                                        print("$value has changed");
                                      },
                                    ),
                                    Positioned(
                                      right: 2,
                                      child: IconButton(
                                        onPressed: () {
                                          print("pressed help");
                                        },
                                        icon: Icon(
                                          Icons.help_outline,
                                          color: Colors.black54,
                                        ),
                                      ),
                                    )
                                  ]),
                                SizedBox(
                                  height: 45,
                                ),
                                Center(
                                  child: FlatButton(
                                    onPressed: () {
                                      String email = emailController.text;
                                      String password = passwordController.text;
                                      if (email.isEmpty || password.isEmpty) {
                                        _showWarning(
                                            "Please fill email and password fields");
                                        return;
                                      }
                                      if (signUp &&
                                          password2Controller.text !=
                                              password) {
                                        _showWarning("Your passwords differ.");
                                        return;
                                      }
                                      if (signUp) {
                                        _signUp(email, password);
                                      } else {
                                        _signInWithCredentials(
                                          email,
                                          password,
                                        );
                                      }
                                    },
                                    child: Container(
                                      decoration: BoxDecoration(
                                        shape: BoxShape.circle,
                                        color: Color.fromRGBO(42, 48, 57, 1),
                                      ),
                                      child: Padding(
                                        padding: const EdgeInsets.all(20.0),
                                        child: Icon(
                                          Icons.arrow_forward,
                                          color: Colors.white,
                                        ),
                                      ),
                                    ),
                                  ),
                                )
                              ],
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                ),
                Spacer(),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(
                    "Or",
                    style: TextStyle(color: Colors.blueGrey),
                  ),
                ),
                SizedBox(
                    height: height * .22,
                    child: Column(
                      children: <Widget>[
                        SignInButton(
                          Buttons.Google,
                          onPressed: () => _googleSignIn(),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(vertical: 25),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: <Widget>[
                              FlatButton(
                                child: Text(
                                  "Forgot your password?",
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                ),
                                onPressed: () {
                                  String email = emailController.text;
                                  if (email == null || email.isEmpty) {
                                    _showWarning(
                                        "Please fill the email address first");
                                    return;
                                  }
                                  _resetPassword(email);
                                },
                              ),
                              FlatButton(
                                child: Text(
                                  signUp
                                      ? "Have an account?"
                                      : "No account yet?",
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                ),
                                onPressed: () {
                                  setState(() {
                                    signUp = !signUp;
                                  });
                                },
                              )
                            ],
                          ),
                        )
                      ],
                    )),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
