import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/ui/screen/all_games/all_games_screen.dart';
import 'package:coinche/ui/screen/login/white_box.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';
import 'package:provider/provider.dart';

class LoginManualScreen extends StatefulWidget {
  static const String routeName = "/loginManual";

  @override
  _LoginManualScreenState createState() => _LoginManualScreenState();
}

class _LoginManualScreenState extends State<LoginManualScreen> {
  final _controllerEmail = TextEditingController();
  final _controllerPassword = TextEditingController(),
      _controllerPassword2 = TextEditingController();
  final FireAuthService _userRepository = getFireAuthService();
  final formKey = GlobalKey<FormState>(),
      formKeySignup = GlobalKey<FormState>();
  var _hiddenPassword1 = true, _hiddenPassword2 = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Container(
        decoration: BoxDecoration(
            gradient: LinearGradient(
          colors: [Colors.pinkAccent, Colors.orangeAccent],
          begin: Alignment.bottomLeft,
          end: Alignment.topRight,
        )),
        child: PageView(children: [
          SingleChildScrollView(
            child: Form(
              key: formKey,
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(
                        left: 30, right: 30, top: 40, bottom: 30),
                    child: WhiteBox(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          _fieldWithValidator(
                            validator: (value) {
                              if (value == null) {
                                return "Please fill your email";
                              }
                              var regex = RegExp(
                                  r"^[\w^@]+(\.[\w^@]+)*(\+[\w^@]+(\.[\w^@]+)*)?@\w+(\.\w+)+$");
                              if (!regex.hasMatch(value)) {
                                return "Email format invalid";
                              }
                              return null;
                            },
                            iconData: Icons.email_outlined,
                            hint: "Email",
                            controller: _controllerEmail,
                          ),
                          Divider(),
                          _fieldWithValidator(
                              hint: "Password",
                              hidden: _hiddenPassword1,
                              iconData: Icons.lock_outlined,
                              validator: (String? value) {
                                if (value == null || value.isEmpty) {
                                  return "Please fill your password";
                                }
                                if (value.length < 8) {
                                  return "Password too short";
                                }
                                return null;
                              },
                              suffix: IconButton(
                                icon: Icon(Icons.visibility_outlined),
                                onPressed: () {
                                  setState(() {
                                    _hiddenPassword1 = !_hiddenPassword1;
                                  });
                                },
                              ),
                              controller: _controllerPassword),
                          FlatButton(
                              onPressed: () {
                                if (formKey.currentState?.validate() == true) {
                                  _signInWithCredentials(
                                      context,
                                      _controllerEmail.text,
                                      _controllerPassword.text);
                                }
                              },
                              child: Text("Login")),
                        ],
                      ),
                    ),
                  ),
                  InkWell(
                    onTap: () {
                      // todo
                    },
                    child: Text("Forgot password?"),
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: Container(
                          color: Colors.white,
                          height: 1,
                        ),
                      ),
                      Text("Or"),
                      Expanded(
                        child: Container(
                          height: 1,
                          color: Colors.white,
                        ),
                      )
                    ],
                  ),
                  SignInButton(Buttons.Google, onPressed: () {
                    _googleSignIn(context);
                  })
                ],
              ),
            ),
          ),
          SingleChildScrollView(
            child: Form(
              key: formKeySignup,
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(
                        left: 30, right: 30, top: 40, bottom: 30),
                    child: WhiteBox(
                        child: Column(
                      children: [
                        _fieldWithValidator(
                          validator: (value) {
                            if (value == null) {
                              return "Please fill your email";
                            }
                            var regex = RegExp(
                                r"^[\w^@]+(\.[\w^@]+)*(\+[\w^@]+(\.[\w^@]+)*)?@\w+(\.\w+)+$");
                            if (!regex.hasMatch(value)) {
                              return "Email format invalid";
                            }
                            return null;
                          },
                          iconData: Icons.email_outlined,
                          hint: "Email",
                          controller: _controllerEmail,
                        ),
                        Divider(),
                        _fieldWithValidator(
                            hint: "Password",
                            hidden: _hiddenPassword1,
                            iconData: Icons.lock_outlined,
                            validator: (String? value) {
                              if (value == null || value.isEmpty) {
                                return "Please fill your password";
                              }
                              if (value.length < 8) {
                                return "Password too short";
                              }
                              return null;
                            },
                            suffix: IconButton(
                              icon: Icon(Icons.visibility_outlined),
                              onPressed: () {
                                setState(() {
                                  _hiddenPassword1 = !_hiddenPassword1;
                                });
                              },
                            ),
                            controller: _controllerPassword),
                        _fieldWithValidator(
                            hint: "Password",
                            hidden: _hiddenPassword2,
                            iconData: Icons.lock_outlined,
                            validator: (String? value) {
                              if (value != _controllerPassword.text) {
                                return "The passwords don't match";
                              }
                              return null;
                            },
                            suffix: IconButton(
                              icon: Icon(Icons.visibility_outlined),
                              onPressed: () {
                                setState(() {
                                  _hiddenPassword2 = !_hiddenPassword2;
                                });
                              },
                            ),
                            controller: _controllerPassword2),
                        FlatButton(
                            onPressed: () {
                              if (formKeySignup.currentState?.validate() ==
                                  true) {
                                _doSignUp(context, _controllerEmail.text,
                                    _controllerPassword.text);
                              }
                            },
                            child: Text("Sign up"))
                      ],
                    )),
                  ),
                ],
              ),
            ),
          )
        ]),
      ),
    );
  }

  Widget _fieldWithValidator({
    required String hint,
    required IconData iconData,
    required String? Function(String? value) validator,
    TextEditingController? controller,
    bool hidden = false,
    Widget? suffix,
  }) {
    return TextFormField(
      validator: validator,
      obscureText: hidden,
      decoration: InputDecoration(
        border: InputBorder.none,
        hintText: hint,
        prefixIcon: Icon(iconData),
        suffix: suffix,
      ),
      controller: controller,
    );
  }

  Future<bool> _doSignUp(
      BuildContext context, String email, String password) async {
    try {
      var user = await _userRepository.signUp(context,
          email: email, password: password);
      if (!user.isEmailVerified) {
        // send a confirmation email
        try {
          await _userRepository.sendEmailVerification(context, email);
          _showSuccess(context,
              "An email was sent to this address: $email. Please check it, and come back after");
          return true;
        } catch (e) {
          _showError(context, e.toString());
          return false;
        }
      } else {
        _loginSuccess(context, user);
        return true;
      }
    } on ServerErrors catch (e) {
      _showError(context, e.cause);
      return false;
    }
  }

  Future<String?> _signInWithCredentials(
      BuildContext context, String email, String password) async {
    MyAuthUser myAuthUser;
    try {
      myAuthUser =
          await _userRepository.signInWithCredentials(context, email, password);
      _loginSuccess(context, myAuthUser);
      return null;
    } on ServerErrors catch (e) {
      _showError(context, e.cause);
      return null;
    } catch (e) {
      return "Unknown error";
    }
  }

  void _googleSignIn(BuildContext context) {
    _userRepository.signInWithGoogle(context).then((user) {
      _loginSuccess(context, user);
    }).catchError(_showError);
  }

  void _loginSuccess(BuildContext context, MyAuthUser user) {
    _showSuccess(context,
        "You are now signed in as ${user.displayName ?? user.email}, and we set user!");

    context.read<LoginModel>().setUser(user);

    Navigator.of(context)?.popUntil((route) => route.isFirst);
    // navigate to other page
    Navigator.of(context)?.pushReplacementNamed(AllGamesScreen.routeName);
  }

  void _showError(BuildContext context, String error) {
    print(error);
    FlushUtil.showError(context, error);
  }

  void _showSuccess(BuildContext context, String message) {
    FlushUtil.showSuccess(context, message);
  }
}
