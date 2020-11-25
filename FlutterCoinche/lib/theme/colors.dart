import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

const Color kColorDark = Color(0xFF353535);
const Color kColorClear = Color(0xFFf0f0f0);

/// Varied palette
const Color kColorBeauBlue = Color(0xFFc0d6e9);
const Color kColorBlueSapphire = Color(0xFF05668d);
const Color kColorDarkPurple = Color(0xFF270722);
const Color kColorPastelPink = Color(0xFFd89a9e);
const Color kColorAmaranthRed = Color(0xFFdd0426);

/// Pink palette
const Color kColorCerise = Color(0xFFd8315b);
const Color kColorSalmonPink = Color(0xFFf39a9d);

const Color kColorSpanishPink = Color(0xFFF7B6B8);
const Color kColorNadeshikoPink = Color(0xFFEFA9BB);

/// Pink gradient palette
const Color kColorApricot = Color(0xFFffcdb2);
const Color kColorMelon = Color(0xFFFFB3A2);
// and pastel pink
const Color kColorEnglishLavender = Color(0xFFB5838D);
const Color kColorOldLavender = Color(0xFF6d6875);

const Color kColorText = Color(0xFF8897b0);
const Color kColorTextDark = Color(0xFF6d727d);

const Color kColorGradient1 = Color(0xFFe6eff5);
const Color kColorGradient2 = Color(0xFFccdfed);
final Color kColorGradientMiddle =
    Color.lerp(kColorGradient1, kColorGradient2, 0.5)!;
const Color kColorShadow = Color(0xFF92b6d8);
const List<Color> kGradientLightBlue = [
  Color(0xFFE0EAFC),
  Color(0xFFCFDEF3),
];
const List<Color> kGradientPinkDark = [kColorPastelPink, kColorAmaranthRed];
const List<Color> kGradientPink = [kColorSalmonPink, kColorCerise];
const List<Color> kGradientPinkLight = [kColorNadeshikoPink, kColorSpanishPink];
const List<Color> kGradientPinkUnsaturated = [
  kColorApricot,
  kColorMelon,
  kColorPastelPink,
  kColorEnglishLavender,
  kColorOldLavender
];
const List<Color> kGradientPinkUnsaturated1 = [kColorApricot, kColorMelon];

const List<Color> kGradientFullPink = [
  kColorSalmonPink,
  kColorPastelPink,
  kColorCerise,
  kColorAmaranthRed
];

// region helpers
MaterialColor generateMaterialColor(Color color) {
  return MaterialColor(color.value, {
    50: _tintColor(color, 0.9),
    100: _tintColor(color, 0.8),
    200: _tintColor(color, 0.6),
    300: _tintColor(color, 0.4),
    400: _tintColor(color, 0.2),
    500: color,
    600: _shadeColor(color, 0.1),
    700: _shadeColor(color, 0.2),
    800: _shadeColor(color, 0.3),
    900: _shadeColor(color, 0.4),
  });
}

int _tintValue(int value, double factor) =>
    max(0, min((value + ((255 - value) * factor)).round(), 255));

Color _tintColor(Color color, double factor) => Color.fromRGBO(
    _tintValue(color.red, factor),
    _tintValue(color.green, factor),
    _tintValue(color.blue, factor),
    1);

int _shadeValue(int value, double factor) =>
    max(0, min(value - (value * factor).round(), 255));

Color _shadeColor(Color color, double factor) => Color.fromRGBO(
    _shadeValue(color.red, factor),
    _shadeValue(color.green, factor),
    _shadeValue(color.blue, factor),
    1);
// endregion
