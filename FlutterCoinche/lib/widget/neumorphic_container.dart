import 'package:FlutterCoinche/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class NeumorphicWidget extends StatefulWidget {
  final Widget child;
  final double borderRadius;
  final SizeShadow sizeShadow;
  final bool interactable;
  final bool alwaysPressedPosition;

  final void Function() onTap;

  NeumorphicWidget(
      {this.child,
      this.borderRadius = 20,
      this.sizeShadow = SizeShadow.LARGE,
      this.interactable = true,
      this.alwaysPressedPosition = false,
      @required this.onTap});

  @override
  _NeumorphicWidgetState createState() => _NeumorphicWidgetState();
}

class _NeumorphicWidgetState extends State<NeumorphicWidget> {
  bool _isPressed;

  @override
  void initState() {
    super.initState();
    _isPressed = widget.alwaysPressedPosition;
  }

  void _onPointerDown(TapDownDetails event) {
    if (widget.interactable) {
      setState(() {
        _isPressed = true;
      });
    }
  }

  void _onPointerUp(TapUpDetails event) {
    if (widget.interactable) {
      setState(() {
        _isPressed = false;
      });
    }
  }

  void _onTapCancel() {
    if (widget.interactable) {
      setState(() {
        _isPressed = false;
      });
    }
  }

  void _onTap() {
    if (widget.interactable && widget.onTap != null) {
      widget.onTap();
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
        decoration: buildNeumorphicDecoration(
          borderRadius: widget.borderRadius,
          pressed: _isPressed,
          sizeShadow: widget.sizeShadow,
        ),
        duration: const Duration(milliseconds: 200),
        child: widget.interactable
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

enum SizeShadow { SMALL, MEDIUM, LARGE }
