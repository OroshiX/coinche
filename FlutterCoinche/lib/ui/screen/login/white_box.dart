import 'package:coinche/theme/colors.dart';
import 'package:flutter/material.dart';

class WhiteBox extends StatelessWidget {
  final Widget child;

  const WhiteBox({Key? key, required this.child}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(10),
          boxShadow: [
            BoxShadow(
                color: kColorShadow,
                blurRadius: 10,
                offset: Offset(5, 2),
                spreadRadius: 2)
          ]),
      padding: const EdgeInsets.all(20),
      child: child,
    );
  }
}
