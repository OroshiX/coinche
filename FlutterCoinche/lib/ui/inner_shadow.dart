import 'dart:ui' as ui;

import 'package:coinche/theme/colors.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:universal_platform/universal_platform.dart';

class InnerShadow extends SingleChildRenderObjectWidget {
  const InnerShadow({
    Key? key,
    this.color,
    this.blur,
    required this.offset,
    this.showIos = true,
    required Widget child,
  }) : super(key: key, child: child);

  final Color? color;
  final double? blur;
  final Offset offset;
  final bool showIos;

  @override
  RenderInnerShadow createRenderObject(BuildContext context) {
    return RenderInnerShadow()
      ..color = color ?? kColorDark
      ..blur = blur ?? 0
      ..showIos = showIos
      ..offset = offset;
  }

  @override
  void updateRenderObject(
      BuildContext context, RenderInnerShadow renderObject) {
    renderObject
      ..color = color ?? kColorDark
      ..blur = blur ?? 0
      ..showIos = showIos
      ..offset = offset;
  }
}

class RenderInnerShadow extends RenderProxyBox {
  RenderInnerShadow({
    RenderBox? child,
  }) : super(child);

  @override
  bool get alwaysNeedsCompositing => child != null;

  Color _color = kColorDark;
  double _blur = 0;
  Offset _offset = Offset.zero;
  bool _showIos = false;

  Color get color => _color;

  set color(Color value) {
    if (_color == value) return;
    _color = value;
    markNeedsPaint();
  }

  bool get showIos => _showIos;

  set showIos(bool value) {
    if (_showIos == value) return;
    _showIos = value;
    markNeedsPaint();
  }

  double get blur => _blur;

  set blur(double value) {
    if (_blur == value) return;
    _blur = value;
    markNeedsPaint();
  }

  Offset get offset => _offset;

  set offset(Offset value) {
    if (_offset == value) return;
    _offset = value;
    markNeedsPaint();
  }

  @override
  void paint(PaintingContext context, Offset offset) {
    if (child != null) {
      if (UniversalPlatform.isIOS && !showIos) {
        // Don'use inner shadow on iOS
        context.paintChild(child!, offset);
        return;
      }
      var layerPaint = Paint()..color = Colors.white;

      var canvas = context.canvas;
      canvas.saveLayer(offset & size, layerPaint);
      context.paintChild(child!, offset);
      var shadowPaint = Paint()
        ..blendMode = ui.BlendMode.srcATop
        ..imageFilter = ui.ImageFilter.blur(sigmaX: blur, sigmaY: blur)
        ..colorFilter = ui.ColorFilter.mode(color, ui.BlendMode.srcIn);
      canvas.saveLayer(offset & size, shadowPaint);

      // Invert the alpha to compute inner part.
      var invertPaint = Paint()
        ..colorFilter = const ui.ColorFilter.matrix([
          1,
          0,
          0,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          0,
          0,
          -1,
          255,
        ]);
      canvas.saveLayer(offset & size, invertPaint);
      canvas.translate(_offset.dx, _offset.dy);
      context.paintChild(child!, offset);
      context.canvas.restore();
      context.canvas.restore();
      context.canvas.restore();
    }
  }

  @override
  void visitChildrenForSemantics(RenderObjectVisitor visitor) {
    if (child != null) visitor(child!);
  }
}
