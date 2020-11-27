import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/theme/text_styles.dart';
import 'package:coinche/ui/screen/login/text_field_round.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:coinche/util/formats.dart';
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
  final _formKey = GlobalKey<FormState>();

  var _hiddenPassword1 = true;

  var _hiddenPassword2 = true;
  late FocusScopeNode _nodeFocusScope;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _nodeFocusScope = FocusScope.of(context);
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Stack(
            children: [
              Padding(
                padding: const EdgeInsets.only(
                    left: 30, right: 30, top: 40, bottom: 20),
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
                              if (!regExpEmail.hasMatch(value)) {
                                return "Email format invalid";
                              }
                              return null;
                            },
                            onEditingComplete: () => _nodeFocusScope.nextFocus(),
                            autofillHints: [AutofillHints.email],
                            iconData: Icons.email_outlined,
                            hint: "Email",
                            controller: widget.controllerEmail,
                          ),
                          SizedBox(height: 16),
                          TextFieldRound(
                              hint: "Password",
                              onEditingComplete: () =>
                                  _nodeFocusScope.nextFocus(),
                              hidden: _hiddenPassword1,
                              iconData: Icons.lock_outlined,
                              autofillHints: [AutofillHints.newPassword],
                              validator: (String? value) {
                                if (value == null || value.isEmpty) {
                                  return "Please fill your password";
                                }
                                if (value.length < 8) {
                                  return "Password too short";
                                }
                                return null;
                              },
                              suffixIcon: Icons.visibility_outlined,
                              onPressedSuffix: () {
                                setState(() {
                                  _hiddenPassword1 = !_hiddenPassword1;
                                });
                              },
                              controller: widget.controllerPassword),
                          SizedBox(height: 16),
                          TextFieldRound(
                              hint: "Confirm password",
                              hidden: _hiddenPassword2,
                              iconData: Icons.lock_outlined,
                              autofillHints: [AutofillHints.password],
                              onEditingComplete: () => _nodeFocusScope.unfocus(),
                              validator: (String? value) {
                                if (value != widget.controllerPassword.text) {
                                  return "The passwords don't match";
                                }
                                return null;
                              },
                              suffixIcon: Icons.visibility_outlined,
                              onPressedSuffix: () {
                                setState(() {
                                  _hiddenPassword2 = !_hiddenPassword2;
                                });
                              },
                              controller: widget.controllerPassword2),
                        ],
                      ),
                    )),
              ),
              Positioned(
                bottom: 0,
                right: 0,
                left: 0,
                child: Center(
                  child: NeumorphicWidget(
                      sizeShadow: SizeShadow.medium,
                      borderRadius: 60,
                      onTap: () {
                        if (_formKey.currentState?.validate() == true) {
                          _doSignUp(context, widget.controllerEmail.text,
                              widget.controllerPassword.text);
                        }
                      },
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 25, vertical: 10),
                        child: Text(
                          "Sign up",
                          style: textStyleButtonLogin,
                        ),
                      )),
                ),
              )
            ],
          ),
        ],
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
