import 'package:FlutterCoinche/fire/fire_auth_service.dart';
import 'package:flushbar/flushbar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  double height, width;
  FireAuthService userRepository;
  TextEditingController emailController, passwordController;

  @override
  void initState() {
    userRepository = getFireAuthService();
    emailController = TextEditingController();
    passwordController = TextEditingController();
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
      print("reset ok");
    });
  }

  _signUp(String email, String password) {
    userRepository
        .signUp(context, email: email, password: password)
        .then((MyAuthUser user) {
      if (!user.isEmailVerified) {
        // send a confirmation email
        userRepository.sendEmailVerification(context).then((value) {
          // TODO set state OK
          showDialog(
              context: context,
              builder: (context) => AlertDialog(
                    title: Text(
                        "Veuillez confirmer votre adresse email en vérifiant vos emails."),
                    actions: <Widget>[
                      FlatButton(
                          onPressed: () {
                            // OK
                          },
                          child: Text("C'est fait!"))
                    ],
                  ));
        });
      }
    });
  }

  _signInWithCredentials(String email, String password) {
    userRepository
        .signInWithCredentials(context, email, password)
        .then((user) async {
      print("ok cred");
      // TODO set state OK
    });
  }

  _googleSignIn() {
    userRepository.signInWithGoogle(context).then((value) {
      print("OK google");
      // TODO state OK
    });
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
                  height: height * 0.54,
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
                                      "Sign In",
                                      style: TextStyle(
                                          fontWeight: FontWeight.bold,
                                          fontStyle: FontStyle.italic),
                                    ),
                                  ),
                                ),
                                Text("User name"),
                                SizedBox(
                                  height: 10,
                                ),
                                TextField(
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
                                    style: TextStyle(),
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
                                    onPressed: () {},
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
                    height: height * 0.3,
                    child: Column(
                      children: <Widget>[
                        SignInButton(
                          Buttons.Facebook,
                          onPressed: () => print("Pressed"),
                        ),
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
                              Text("Forgot your password?"),
                              FlatButton(
                                child: Text(
                                  "We got you",
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                ),
                                onPressed: () {
                                  String email = emailController.text;
                                  if (email == null || email.isEmpty) {
                                    Flushbar().show(context);
                                    return;
                                  }
                                  _resetPassword(email);
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
