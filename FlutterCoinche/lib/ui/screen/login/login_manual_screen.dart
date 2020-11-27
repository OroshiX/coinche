import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/theme/text_styles.dart';
import 'package:coinche/ui/screen/all_games/all_games_screen.dart';
import 'package:coinche/ui/screen/login/login_screen.dart';
import 'package:coinche/ui/screen/login/sign_up.dart';
import 'package:coinche/ui/widget/animated_toggle_button.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
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

  var _signUp = false;

  final _pageController = PageController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Center(
            child: Text(
          "Coinche",
          style: textStyleTitleLogin,
        )),
        flexibleSpace: Container(
          decoration: BoxDecoration(
              gradient: LinearGradient(
                  colors: kGradientLightBlue,
                  end: Alignment.topCenter,
                  begin: Alignment.bottomCenter)),
        ),
      ),
      body: Container(
        decoration: BoxDecoration(
            gradient: LinearGradient(
          colors: kGradientBlue,
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        )),
        child: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                SizedBox(height: 20),
                Padding(
                  padding: const EdgeInsets.only(top: 24, left: 10, right: 10),
                  child: SizedBox(
                    height: 60,
                    child: AnimatedToggleButton(
                      on: _signUp,
                      textOn: Center(
                        child: Text(
                          "Sign up",
                          textAlign: TextAlign.center,
                          style: textStyleSwitchLogin,
                        ),
                      ),
                      textOff: Center(
                          child: Text(
                        "Login",
                        style: textStyleSwitchLogin,
                        textAlign: TextAlign.center,
                      )),
                      toggleOnOff: (on) {
                        _pageController.animateToPage(on ? 1 : 0,
                            duration: Duration(milliseconds: 300),
                            curve: Curves.fastOutSlowIn);
                        setState(() {
                          _signUp = on;
                        });
                      },
                    ),
                  ),
                ),
                LimitedBox(
                  maxHeight: 500,
                  child: PageView(
                      controller: _pageController,
                      onPageChanged: (value) {
                        switch (value) {
                          case 0:
                            if (_signUp != false) {
                              setState(() {
                                _signUp = false;
                              });
                            }
                            break;
                          case 1:
                            if (_signUp != true) {
                              setState(() {
                                _signUp = true;
                              });
                            }
                            break;
                        }
                      },
                      children: [
                        LoginScreen(
                          controllerEmail: _controllerEmail,
                          controllerPassword: _controllerPassword,
                          onError: (error) => _showError(context, error),
                          onSuccess: (user) => _loginSuccess(context, user),
                          userRepository: _userRepository,
                        ),
                        SignUp(
                            onError: (error) => _showError(context, error),
                            onSuccess: (user) {
                              _loginSuccess(context, user);
                            },
                            onSentEmailVerification: (email) {
                              OverlayEntry? entry;
                              entry = OverlayEntry(
                                builder: (context) {
                                  return Material(
                                    color: kColorShadow.withOpacity(0.3),
                                    child: Center(
                                      child: Padding(
                                        padding: const EdgeInsets.symmetric(
                                            horizontal: 20),
                                        child: Container(
                                          decoration: BoxDecoration(
                                              color: Colors.white,
                                              borderRadius:
                                                  BorderRadius.circular(20)),
                                          padding: const EdgeInsets.symmetric(
                                              vertical: 20, horizontal: 20),
                                          child: Column(
                                            mainAxisSize: MainAxisSize.min,
                                            children: [
                                              Text(
                                                  "Please check your email address ($email). An email has been sent to you. You will then be able to login"),
                                              SizedBox(height: 10),
                                              NeumorphicWidget(
                                                borderRadius: 20,
                                                sizeShadow: SizeShadow.medium,
                                                child: Padding(
                                                  padding: const EdgeInsets
                                                          .symmetric(
                                                      vertical: 8,
                                                      horizontal: 16),
                                                  child:
                                                      Text("Ok".toUpperCase()),
                                                ),
                                                onTap: () {
                                                  entry?.remove();
                                                },
                                              )
                                            ],
                                          ),
                                        ),
                                      ),
                                    ),
                                  );
                                },
                              );
                              // show confirmation page, saying that you should check your emails
                              Overlay.of(context)?.insert(entry);
                            },
                            userRepository: _userRepository,
                            controllerEmail: _controllerEmail,
                            controllerPassword: _controllerPassword,
                            controllerPassword2: _controllerPassword2),
                      ]),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _loginSuccess(BuildContext context, MyAuthUser user) {
    context.read<LoginModel>().setUser(user);

    Navigator.of(context)?.popUntil((route) => route.isFirst);
    // navigate to other page
    Navigator.of(context)?.pushReplacementNamed(AllGamesScreen.routeName);
  }

  void _showError(BuildContext context, String error) {
    print(error);
    FlushUtil.showError(context, error);
  }
}
