import 'package:flutter/cupertino.dart';

class TranslatedTransition extends AnimatedWidget {
  final Widget child;
  final Animation<Offset> animation;
  TranslatedTransition({Key? key, required this.child, required this.animation})
      : super(key: key, listenable: animation);

  @override
  Widget build(BuildContext context) {
    return Transform.translate(
      offset: animation.value,
      child: child,
    );
  }
}
