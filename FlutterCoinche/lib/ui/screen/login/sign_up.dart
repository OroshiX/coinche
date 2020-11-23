import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/ui/screen/login/text_field_round.dart';
import 'package:coinche/ui/screen/login/white_box.dart';
import 'package:flutter/material.dart';

class SignUp extends StatefulWidget {
  final void Function(String error) onError;
  final void Function(MyAuthUser user) onSuccess;
  final void Function(String email) onSentEmailVerification;
  final FireAuthService userRepository;
  final TextEditingController controllerEmail,
      controllerPassword,
      controllerPassword2;

  SignUp({
    Key? key,
    required this.onError,
    required this.onSuccess,
    required this.onSentEmailVerification,
    required this.userRepository,
    required this.controllerEmail,
    required this.controllerPassword,
    required this.controllerPassword2,
  }) : super(key: key);

  @override
  _SignUpState createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  final _formKeySignup = GlobalKey<FormState>();

  var _hiddenPassword1 = true;

  var _hiddenPassword2 = true;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Form(
        key: _formKeySignup,
        child: Stack(
          children: [
            Padding(
              padding: const EdgeInsets.only(
                  left: 30, right: 30, top: 40, bottom: 25),
              child: WhiteBox(
                  child: Column(
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
                      controller: widget.controllerPassword),
                  TextFieldRound(
                      hint: "Password",
                      hidden: _hiddenPassword2,
                      iconData: Icons.lock_outlined,
                      validator: (String? value) {
                        if (value != widget.controllerPassword.text) {
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
                      controller: widget.controllerPassword2),
                ],
              )),
            ),
            Positioned(
              bottom: 0,
              right: 0,
              left: 0,
              child: Center(
                child: RaisedButton(
                    onPressed: () {
                      if (_formKeySignup.currentState?.validate() == true) {
                        _doSignUp(context, widget.controllerEmail.text,
                            widget.controllerPassword.text);
                      }
                    },
                    child: Text("Sign up")),
              ),
            )
          ],
        ),
      ),
    );
  }

  Future<bool> _doSignUp(
      BuildContext context, String email, String password) async {
    try {
      var user = await widget.userRepository
          .signUp(context, email: email, password: password);
      if (!user.isEmailVerified) {
        // send a confirmation email
        try {
          await widget.userRepository.sendEmailVerification(context, email);

          widget.onSentEmailVerification(email);
          return true;
        } catch (e) {
          widget.onError(e.toString());
          return false;
        }
      } else {
        widget.onSuccess(user);
        return true;
      }
    } on ServerErrors catch (e) {
      widget.onError(e.cause);
      return false;
    }
  }
}
