import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/inner_shadow.dart';
import 'package:coinche/ui/widget/neumorphic_container.dart';
import 'package:coinche/ui/widget/neumorphic_no_state.dart';
import 'package:flutter/material.dart';

class AnimatedToggleButton extends StatelessWidget {
  final bool on;
  final Widget? textOff, textOn;
  final void Function(bool on)? toggleOnOff;

  const AnimatedToggleButton(
      {Key? key, this.on = false, this.textOff, this.textOn, this.toggleOnOff})
      : super(key: key);
  @override
  Widget build(BuildContext context) {
    final o = 3.0;
    final blurTop = 6.0;
    final blurBottom = 5.0;
    return LayoutBuilder(
      builder: (context, constraints) {
        final midWidth = constraints.maxWidth / 2;
        return GestureDetector(
          onTapDown: (TapDownDetails tapDownDetails) {
            var tappedOn = tapDownDetails.localPosition.dx > midWidth;
            if (on != tappedOn) {
              toggleOnOff?.call(tappedOn);
            }
          },
          onHorizontalDragUpdate: (DragUpdateDetails details) {
            var dx = details.primaryDelta;
            if (dx != null && dx != 0) {
              var shouldBeOn = dx > 0;
              if (on != shouldBeOn) {
                toggleOnOff?.call(shouldBeOn);
              }
            }
          },
          child: InnerShadow(
            color: kColorShadow,
            offset: Offset(o, o),
            blur: blurTop,
            child: InnerShadow(
              color: Colors.white,
              offset: Offset(-o, -o),
              blur: blurBottom,
              child: Container(
                width: constraints.maxWidth, // - 2 * o,
                height: constraints.maxHeight, // - 2 * o,
                decoration: BoxDecoration(
                    color: kColorGradientMiddle,
                    borderRadius: BorderRadius.circular(80)),
                padding: EdgeInsets.symmetric(
                  vertical: (blurBottom + blurTop) / 2 + o,
                  horizontal: (blurBottom + blurTop) / 2 + o,
                ),
                child: Stack(
                  children: [
                    AnimatedPositioned(
                      curve: Curves.fastOutSlowIn,
                      duration: Duration(milliseconds: 300),
                      left: on ? midWidth : 0,
                      right: on ? 0 : midWidth,
                      bottom: 0,
                      top: 0,
                      child: SizedBox(
                        width: midWidth,
                        height: constraints.maxHeight,
                        child: NeumorphicNoStateWidget(
                          sizeShadow: SizeShadow.small,
                          pressed: false,
                          borderRadius: 80,
                          child: SizedBox(),
                        ),
                      ),
                    ),
                    Positioned(
                      left: 0,
                      right: midWidth,
                      child: textOff ?? SizedBox(),
                      top: 0,
                      bottom: 0,
                    ),
                    Positioned(
                      right: 0,
                      left: midWidth,
                      child: textOn ?? SizedBox(),
                      top: 0,
                      bottom: 0,
                    )
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}
