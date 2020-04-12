import 'package:flutter/material.dart';

class NeumorphicWidget extends StatelessWidget {
  final Widget child;
  final double borderRadius;

  NeumorphicWidget({this.child, this.borderRadius = 20});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          shape: BoxShape.rectangle,
          gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [
                Color.fromRGBO(230, 239, 245, 1),
                Color.fromRGBO(204, 223, 237, 1),
              ]),
          borderRadius: BorderRadius.circular(borderRadius),
          boxShadow: [
            BoxShadow(
              color: Colors.white,
              blurRadius: 8,
              spreadRadius: -5,
              offset: Offset(-5, -5),
            ),
            BoxShadow(
                color: Color.fromRGBO(146, 182, 216, 1),
                blurRadius: 5,
                offset: Offset(7, 7),
                spreadRadius: -1)
          ]),
      child: child,
    );
  }
}
