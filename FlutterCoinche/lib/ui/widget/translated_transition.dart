import 'package:flutter/cupertino.dart';

class TranslatedTransition extends AnimatedWidget {
  final Widget child;

  TranslatedTransition(
      {Key key, @required this.child, @required Animation<Offset> animation})
      : super(key: key, listenable: animation);

  @override
  Widget build(BuildContext context) {
    final Animation<Offset> animation = listenable;
    return Transform.translate(
      offset: animation.value,
      child: child,
    );
  }
}
