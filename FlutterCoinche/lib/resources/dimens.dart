import 'package:flutter/cupertino.dart';

const threshold = 800;
const golden = 1.6180339887;
const bigCardWidth = 70;

double getCardWidth(Size screenSize) =>
    screenSize.height < threshold ? 50 : 100;

double getMarginCardsPosition(Size screenSize) =>
    screenSize.height < threshold ? 8 : 16;

double getHeightBidding(Size screenSize) =>
    screenSize.height < threshold ? 100 : 200;

double getPaddingHeightCard(Size screenSize) =>
    screenSize.height < threshold ? 5 : 10;

double getBorderRadiusBidding(Size screenSize) =>
    screenSize.height < threshold ? 15 : 30;

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

bool isLargeScreen(Size screenSize) => screenSize.height >= threshold;

double getSizeSuitIcon({@required double cardWidth}) => cardWidth / 1.5;

bool isBigCardWidth({@required double cardWidth}) => cardWidth > bigCardWidth;

double getTextSizeCard({@required double cardWidth}) => cardWidth / 5;

double getSizeSuitMiniIcon({@required double cardWidth}) => cardWidth / 8.33;

double getPaddingInCard({@required double cardWidth}) =>
    cardWidth > bigCardWidth ? 5 : 2.5;
