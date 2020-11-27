import 'package:coinche/error/exceptions.dart';
import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/theme/text_styles.dart';
import 'package:coinche/ui/screen/login/text_field_round.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:coinche/util/formats.dart';
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
  final _formKey = GlobalKey<FormState>();

  bool _hiddenPassword = true;
  late FocusScopeNode _nodeFocusScope;

  final _keyButtonSignIn = GlobalKey<NeumorphicWidgetState>();

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
                          autofillHints: [AutofillHints.email],
                          onEditingComplete: () => _nodeFocusScope.nextFocus(),
                          iconData: Icons.email_outlined,
                          hint: "Email",
                          controller: widget.controllerEmail,
                        ),
                        SizedBox(height: 16),
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
                            autofillHints: [AutofillHints.password],
                            onEditingComplete: () => _nodeFocusScope.unfocus(),
                            onPressedSuffix: () {
                              setState(() {
                                _hiddenPassword = !_hiddenPassword;
                              });
                            },
                            suffixIcon: Icons.visibility_outlined,
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
                        key: _keyButtonSignIn,
                        sizeShadow: SizeShadow.medium,
                        borderRadius: 60,
                        onTap: _pressedSignIn,
                        child: Padding(
                          padding: const EdgeInsets.symmetric(
                              vertical: 10, horizontal: 25),
                          child: Text(
                            "Sign in",
                            style: textStyleButtonLogin,
                          ),
                        )),
                  ))
            ],
          ),
          SizedBox(height: 30),
          InkWell(
            onTap: () {
              var emailInit = widget.controllerEmail.text;
              // show a dialog asking for an email address to fill, then send
              showDialog(
                context: context,
                builder: (ctx) {
                  final controller = TextEditingController(text: emailInit);
                  final keyResetPasswordForm = GlobalKey<FormState>();
                  return StatefulBuilder(
                    builder: (ctx, setState) => AlertDialog(
                      backgroundColor: kColorBlue3,
                      title: Text(
                          "Fill your email in order to reset your password"),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30)),
                      content: Container(
                        color: kColorPastelPink,
                        child: Form(
                          key: keyResetPasswordForm,
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              TextFieldRound(
                                hint: "Email",
                                controller: controller,
                                iconData: Icons.email_outlined,
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return "Please fill your email address in order to reset your password.";
                                  }
                                  if (!regExpEmail.hasMatch(value)) {
                                    return "Email format invalid";
                                  }
                                  return null;
                                },
                              ),
                              NeumorphicWidget(
                                child: Text("Reset"),
                                onTap: () {
                                  if (keyResetPasswordForm.currentState
                                          ?.validate() ==
                                      true) {
                                    widget.userRepository.resetPassword(
                                      controller.text,
                                      onError: (message) =>
                                          FlushUtil.showError(ctx, message),
                                      onSuccess: () {
                                        Navigator.of(ctx)?.pop();
                                        FlushUtil.showSuccess(context,
                                            "An email has been sent to you in order to reset your password.");
                                      },
                                    );
                                  }
                                },
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  );
                },
              );
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
              Padding(
                padding: const EdgeInsets.only(
                    left: 20, right: 20, top: 25, bottom: 25),
                child: Text("Or"),
              ),
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
    );
  }

  void _pressedSignIn() {
    if (_formKey.currentState?.validate() == true) {
      _signInWithCredentials(
          context, widget.controllerEmail.text, widget.controllerPassword.text);
    }
  }

  Future<String?> _signInWithCredentials(
      BuildContext context, String email, String password) async {
    setState(() {
      _keyButtonSignIn.currentState?.isInteractable = false;
    });
    MyAuthUser myAuthUser;
    try {
      myAuthUser = await widget.userRepository
          .signInWithCredentials(context, email, password);
      widget.onSuccess(myAuthUser);
      return null;
    } on ServerErrors catch (e) {
      widget.onError(e.cause);
      setState(() {
        _keyButtonSignIn.currentState?.isInteractable = true;
      });
      return null;
    } catch (e) {
      widget.onError(e.toString());
      setState(() {
        _keyButtonSignIn.currentState?.isInteractable = true;
      });
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
