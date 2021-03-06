import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/inner_shadow.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class NeuRoundInset extends StatefulWidget {
  final Widget child;
  final VoidCallback onTap;

  const NeuRoundInset({Key? key, required this.onTap, required this.child})
      : super(key: key);

  @override
  _NeuRoundInsetState createState() => _NeuRoundInsetState();
}

class _NeuRoundInsetState extends State<NeuRoundInset> {
  bool _isPressed = false;

  void _onPointerDown(TapDownDetails event) {
    setState(() {
      _isPressed = true;
    });
  }

  void _onPointerUp(TapUpDetails event) {
    setState(() {
      _isPressed = false;
    });
  }

  void _onTap() {
    widget.onTap();
  }

  void _onTapCancel() {
    setState(() {
      _isPressed = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final curve = Curves.easeOutCirc;
    final duration = Duration(milliseconds: 100);
    return GestureDetector(
        onTapDown: _onPointerDown,
        onTapUp: _onPointerUp,
        onTap: _onTap,
        onTapCancel: _onTapCancel,
        child: AnimatedContainer(
          curve: curve,
          duration: duration,
          decoration: buildNeumorphicDecoration(
            borderRadius: 50,
            pressed: _isPressed,
            sizeShadow: SizeShadow.large,
          ),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: TweenAnimationBuilder(
              curve: curve,
              tween: Tween<double>(begin: 1, end: _isPressed ? 1 : 5),
              builder: (_, double d, __) => InnerShadow(
                color: kColorShadow,
                offset: Offset(d, d),
                blur: 2,
                child: InnerShadow(
                  color: Colors.white,
                  offset: Offset(-d, -d),
                  blur: 2,
                  child: Container(
                    padding: EdgeInsets.all(10),
                    decoration: BoxDecoration(
                        color: kColorGradient1,
                        borderRadius: BorderRadius.circular(50)),
                    child: widget.child,
                  ),
                ),
              ),
              duration: duration,
            ),
          ),
        ));
  }
}
