import 'dart:math';

import 'package:flutter/cupertino.dart';

const threshold = 600;
const golden = 1.6180339887;
const bigCardWidth = 70;

double getCardWidth(Size screenSize) => isLargeScreen(screenSize) ? 100 : 50;

double getMarginCardsPosition(Size screenSize) =>
    isLargeScreen(screenSize) ? 16 : 8;

double getHeightBidding(Size screenSize) =>
    isLargeScreen(screenSize) ? 200 : 100;

double getPaddingHeightCard(Size screenSize) =>
    isLargeScreen(screenSize) ? 10 : 5;

double getBorderRadiusBidding(Size screenSize) =>
    isLargeScreen(screenSize) ? 30 : 15;

EdgeInsets getPaddingButtonTypeBid(Size screenSize) => isLargeScreen(screenSize)
    ? EdgeInsets.all(8)
    : EdgeInsets.symmetric(horizontal: 6, vertical: 4);

EdgeInsets getPaddingBetweenButtonsTypeBid(Size screenSize) =>
    isLargeScreen(screenSize)
        ? EdgeInsets.all(8)
        : EdgeInsets.symmetric(horizontal: 3, vertical: 4);

EdgeInsets getPaddingCapot(Size screenSize) => isLargeScreen(screenSize)
    ? EdgeInsets.all(8)
    : EdgeInsets.symmetric(horizontal: 4, vertical: 2);

double getBottomOfBiddingBar(Size screenSize) =>
    isLargeScreen(screenSize) ? 20 : 10;

bool isLargeScreen(Size screenSize) =>
    min(screenSize.height, screenSize.width) >= threshold;

double getSizeSuitIcon({@required double cardWidth}) => cardWidth / 1.5;

bool isBigCardWidth({@required double cardWidth}) => cardWidth > bigCardWidth;

double getTextSizeCard({@required double cardWidth}) => cardWidth / 5;

double getSizeSuitMiniIcon({@required double cardWidth}) => cardWidth / 8.33;

double getPaddingInCard({@required double cardWidth}) =>
    cardWidth > bigCardWidth ? 5 : 2.5;
