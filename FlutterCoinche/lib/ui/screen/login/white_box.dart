import 'package:flutter/material.dart';

class WhiteBox extends StatelessWidget {
  final Widget child;

  const WhiteBox({Key? key, required this.child}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.8), borderRadius: BorderRadius.circular(10)),
      padding: const EdgeInsets.all(20),
      child: child,
    );
  }
}
