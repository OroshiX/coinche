import 'package:coinche/service/network/fire_auth_service.dart';
import 'package:coinche/service/network/my_auth_user.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/theme/text_styles.dart';
import 'package:coinche/ui/screen/all_games/all_games_screen.dart';
import 'package:coinche/ui/screen/login/login_screen.dart';
import 'package:coinche/ui/screen/login/sign_up.dart';
import 'package:coinche/ui/widget/animated_toggle_button.dart';
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
      appBar: AppBar(),
      body: Container(
        decoration: BoxDecoration(
            gradient: LinearGradient(
          colors: kGradientLightBlue,
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        )),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            SizedBox(height: 20),
            Text(
              "Coinche",
              style: textStyleTitleLogin,
            ),
            Padding(
              padding: const EdgeInsets.only(top: 24, left: 10, right: 10),
              child: SizedBox(
                height: 50,
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
            Expanded(
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
                          // todo show confirmation page, saying that you should check your emails
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
    );
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