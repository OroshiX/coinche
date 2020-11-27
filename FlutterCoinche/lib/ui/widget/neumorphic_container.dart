import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class NeumorphicWidget extends StatefulWidget {
  final Widget child;
  final double borderRadius;
  final SizeShadow sizeShadow;
  final bool interactable;
  final bool alwaysPressedPosition;

  final void Function()? onTap;

  NeumorphicWidget(
      {Key? key,
      required this.child,
      this.borderRadius = 20,
      this.sizeShadow = SizeShadow.large,
      this.interactable = true,
      this.alwaysPressedPosition = false,
      required this.onTap})
      : super(key: key);

  @override
  NeumorphicWidgetState createState() => NeumorphicWidgetState();
}

class NeumorphicWidgetState extends State<NeumorphicWidget> {
  late bool _isPressed;
  late bool isInteractable;

  @override
  void initState() {
    super.initState();
    _isPressed = widget.alwaysPressedPosition;
    isInteractable = widget.interactable;
  }

  void _onPointerDown(TapDownDetails event) {
    if (isInteractable) {
      setState(() {
        _isPressed = true;
      });
    }
  }

  void _onPointerUp(TapUpDetails event) {
    if (isInteractable) {
      setState(() {
        _isPressed = false;
      });
    }
  }

  void _onTapCancel() {
    if (isInteractable) {
      setState(() {
        _isPressed = false;
      });
    }
  }

  void _onTap() {
    if (isInteractable) {
      widget.onTap?.call();
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: _onPointerDown,
      onTapUp: _onPointerUp,
      onTap: _onTap,
      onTapCancel: _onTapCancel,
      child: AnimatedContainer(
        curve: Curves.easeOutExpo,
        decoration: buildNeumorphicDecoration(
          borderRadius: widget.borderRadius,
          pressed: _isPressed,
          sizeShadow: widget.sizeShadow,
        ),
        duration: const Duration(milliseconds: 200),
        child: isInteractable
            ? widget.child
            : ClipRRect(
                borderRadius: BorderRadius.circular(widget.borderRadius),
                child: Material(
                  color: Colors.black26,
                  child: widget.child,
                ),
              ),
      ),
    );
  }
}

enum SizeShadow { small, medium, large }
