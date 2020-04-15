import 'package:FlutterCoinche/resources/colors.dart';
import 'package:FlutterCoinche/ui/inner_shadow.dart';
import 'package:FlutterCoinche/widget/neumorphic_container.dart';
import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class NeuRoundInset extends StatefulWidget {
  final Widget child;
  final VoidCallback onTap;

  const NeuRoundInset({Key key, this.onTap, this.child}) : super(key: key);

  @override
  _NeuRoundInsetState createState() => _NeuRoundInsetState();
}

class _NeuRoundInsetState extends State<NeuRoundInset> {
  bool _isPressed;

  @override
  void initState() {
    super.initState();
    _isPressed = false;
  }

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
            sizeShadow: SizeShadow.LARGE,
          ),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: TweenAnimationBuilder(
              curve: curve,
              tween: Tween<double>(begin: 1, end: _isPressed ? 1 : 5),
              builder: (_, d, __) => InnerShadow(
                color: colorShadow,
                offset: Offset(d, d),
                blur: 2,
                child: InnerShadow(
                  color: Colors.white,
                  offset: Offset(-d, -d),
                  blur: 2,
                  child: Container(
                    padding: EdgeInsets.all(10),
//              width: 200,
//              height: 50,
                    decoration: BoxDecoration(
                        color: colorGradient1,
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
