import 'package:coinche/domain/dto/card.dart';

const _left = 0.25;
const _right = 0.75;
const _center = 0.5;
const _v1 = 1 / 6;
const _v2 = 7 / 24;
const _v3 = 5 / 12;
const _v4 = 7 / 12;
const _v5 = 17 / 24;
const _v6 = 5 / 6;

const _v7 = 1 / 3;
const _v8 = 1 / 2;
const _v9 = 2 / 3;

const royaltyX = [_center, _center];
const royaltyY = [_v7, _v9];
const royaltyRot = [true, false];

const aceX = [_center];
const aceY = [_v8];
const aceRot = [true];

const sevenX = [_left, _right, _center, _left, _right, _left, _right];
const sevenY = [_v1, _v1, _v7, _v8, _v8, _v6, _v6];
const sevenRot = [true, true, true, true, true, false, false];

const eightX = [_left, _right, _center, _left, _right, _center, _left, _right];
const eightY = [_v1, _v1, _v7, _v8, _v8, _v9, _v6, _v6];
const eightRot = [true, true, true, true, true, false, false, false];

const nineX = [
  _left,
  _right,
  _left,
  _right,
  _center,
  _left,
  _right,
  _left,
  _right
];
const nineY = [_v1, _v1, _v3, _v3, _v8, _v4, _v4, _v6, _v6];
const nineRot = [true, true, true, true, true, false, false, false, false];

const tenX = [
  _left,
  _right,
  _center,
  _left,
  _right,
  _left,
  _right,
  _center,
  _left,
  _right
];
const tenY = [_v1, _v1, _v2, _v3, _v3, _v4, _v4, _v5, _v6, _v6];
const tenRot = [
  true,
  true,
  true,
  true,
  true,
  false,
  false,
  false,
  false,
  false
];

extension ImageExtension on CardModel {
  String getAssetImageFromColorAndValue() {
    var imageColor = color.getAssetImageFromColor();
    switch (value) {
      case CardValue.king:
        return "images/king.svg";
      case CardValue.queen:
        return "images/queen.svg";
      case CardValue.jack:
        return "images/jack.svg";
      default:
        return imageColor;
    }
  }

  double getSizeSuit() {
    switch (value) {
      case CardValue.ace:
        return 1 / 1.5;
      case CardValue.king:
      case CardValue.queen:
      case CardValue.jack:
        return 0.8;
      case CardValue.ten:
      case CardValue.nine:
      case CardValue.eight:
      case CardValue.seven:
        return 1 / 5;
    }
    return 0;
  }
}

extension ColorExtension on CardColor {
  String getAssetImageFromColor() {
    String file;
    switch (this) {
      case CardColor.diamond:
        file = "diamond.png";
        break;
      case CardColor.spade:
        file = "spade.png";
        break;
      case CardColor.club:
        file = "club.png";
        break;
      case CardColor.heart:
        file = "heart.png";
        break;
    }
    return "images/$file";
  }
}

extension ValueExtension on CardValue {
  String getLetterFromValue() {
    switch (this) {
      case CardValue.ace:
        return "A";
      case CardValue.king:
        return "K";
      case CardValue.queen:
        return 'Q';
      case CardValue.jack:
        return "J";
      case CardValue.ten:
        return "10";
      case CardValue.nine:
        return "9";
      case CardValue.eight:
        return "8";
      case CardValue.seven:
        return "7";
    }
  }

  List<double> getXs() {
    switch (this) {
      case CardValue.ten:
        return tenX;
      case CardValue.nine:
        return nineX;
      case CardValue.eight:
        return eightX;
      case CardValue.seven:
        return sevenX;
      case CardValue.ace:
        return aceX;
      case CardValue.king:
      case CardValue.queen:
      case CardValue.jack:
        return royaltyX;
    }
  }

  List<double> getYs() {
    switch (this) {
      case CardValue.ten:
        return tenY;
      case CardValue.nine:
        return nineY;
      case CardValue.eight:
        return eightY;
      case CardValue.seven:
        return sevenY;
      case CardValue.ace:
        return aceY;
      case CardValue.king:
      case CardValue.queen:
      case CardValue.jack:
        return royaltyY;
    }
  }

  List<bool> getRotations() {
    switch (this) {
      case CardValue.ten:
        return tenRot;
      case CardValue.nine:
        return nineRot;
      case CardValue.eight:
        return eightRot;
      case CardValue.seven:
        return sevenRot;
      case CardValue.ace:
        return aceRot;
      case CardValue.king:
      case CardValue.queen:
      case CardValue.jack:
        return royaltyRot;
    }
  }
}
