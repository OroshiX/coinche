import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/ui/screen/login/text_field_round.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';
import 'package:flutter_signin_button/button_list.dart';
import 'package:flutter_signin_button/button_view.dart';

class LoginScreen extends StatefulWidget {
  final void Function(String error) onError;
  final void Function(MyAuthUser user) onSuccess;
  final FireAuthService userRepository;

  final TextEditingController controllerEmail, controllerPassword;
  LoginScreen({
    required this.controllerEmail,
    required this.controllerPassword,
    required this.onError,
    required this.onSuccess,
    required this.userRepository,
  });

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final formKey = GlobalKey<FormState>();

  bool _hiddenPassword = true;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Form(
        key: formKey,
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.only(
                  left: 30, right: 30, top: 40, bottom: 30),
              child: Stack(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(bottom: 15),
                    child: NeumorphicNoStateWidget(
                      pressed: true,
                      sizeShadow: SizeShadow.large,
                      child: Padding(
                        padding: const EdgeInsets.only(
                            left: 20, right: 20, top: 10, bottom: 30),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            TextFieldRound(
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
                              controller: widget.controllerEmail,
                            ),
                            Divider(),
                            TextFieldRound(
                                hint: "Password",
                                hidden: _hiddenPassword,
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
                                      _hiddenPassword = !_hiddenPassword;
                                    });
                                  },
                                ),
                                controller: widget.controllerPassword),
                          ],
                        ),
                      ),
                    ),
                  ),
                  Positioned(
                      bottom: 0,
                      right: 0,
                      left: 0,
                      child: Center(
                        child: NeumorphicWidget(
                            sizeShadow: SizeShadow.medium,
                            onTap: _pressedSignIn,
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  vertical: 10, horizontal: 25),
                              child: Text("Sign in"),
                            )),
                      ))
                ],
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
    );
  }

  void _pressedSignIn() {
    if (formKey.currentState?.validate() == true) {
      _signInWithCredentials(
          context, widget.controllerEmail.text, widget.controllerPassword.text);
    }
  }

  Future<String?> _signInWithCredentials(
      BuildContext context, String email, String password) async {
    MyAuthUser myAuthUser;
    try {
      myAuthUser = await widget.userRepository
          .signInWithCredentials(context, email, password);
      widget.onSuccess(myAuthUser);
      return null;
    } on ServerErrors catch (e) {
      widget.onError(e.cause);
      return null;
    } catch (e) {
      return "Unknown error";
    }
  }

  void _googleSignIn(BuildContext context) {
    widget.userRepository
        .signInWithGoogle(context)
        .then(widget.onSuccess)
        .catchError((e) => widget.onError(e.toString()));
  }
}
