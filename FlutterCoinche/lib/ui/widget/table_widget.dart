import 'package:FlutterCoinche/ui/resources/dimens.dart';
import 'package:FlutterCoinche/ui/widget/animated_bidding_bar.dart';
import 'package:FlutterCoinche/ui/widget/bids_widget.dart';
import 'package:FlutterCoinche/ui/widget/button_last_trick.dart';
import 'package:FlutterCoinche/ui/widget/cards_hand_widget.dart';
import 'package:FlutterCoinche/ui/widget/landscape/landscape_score_widget.dart';
import 'package:FlutterCoinche/ui/widget/middle_area.dart';
import 'package:FlutterCoinche/ui/widget/player_avatar.dart';
import 'package:FlutterCoinche/ui/widget/portrait/portrait_score_widget.dart';
import 'package:FlutterCoinche/ui/widget/waiting_players.dart';
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class TableWidget extends StatefulWidget {
  final Function quit;

  TableWidget({@required this.quit});

  @override
  _TableWidgetState createState() => _TableWidgetState();
}

class _TableWidgetState extends State<TableWidget> {
  final AutoSizeGroup autoSizeGroup = AutoSizeGroup();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final screenSize = MediaQuery.of(context).size;
    final portrait = MediaQuery.of(context).orientation == Orientation.portrait;
    const double widthContainerName = 65;
    const double heightContainer = 104;

    final double cardWidth = getCardWidth(screenSize);
    final double cardHeight = cardWidth * golden;
    final double marginCardsPosition = getMarginCardsPosition(screenSize);
//    final double heightBiddingBar = getHeightBidding(screenSize);
    final double paddingHeightCards = getPaddingHeightCard(screenSize);

    return Column(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.start,
      children: <Widget>[
        Expanded(
          child: Stack(
            children: <Widget>[
              Align(
                  alignment: Alignment.topLeft,
                  child: portrait
                      ? PortraitScoreWidget(
                          onTapMessages: () {
                            // TODO Messages
                            print("TODO");
                          },
                          onTapExit: () async {
                            var quit = (await widget.quit()) ?? false;
                            if (quit) Navigator.of(context).pop();
                          },
                        )
                      : LandscapeScoreWidget(
                          onTapExit: () async {
                            var quit = (await widget.quit()) ?? false;
                            if (quit) Navigator.of(context).pop();
                          },
                          onTapMessages: () {
                            // TODO messages
                            print("TODO messages");
                          },
                        )),
              Align(
                alignment: Alignment.topCenter,
                child: PlayerAvatar(
                  posTable: AxisDirection.up,
                  autoSizeGroup: autoSizeGroup,
                  height: heightContainer,
                  width: widthContainerName,
                ),
              ),
              Align(
                alignment: Alignment.centerRight,
                child: PlayerAvatar(
                  autoSizeGroup: autoSizeGroup,
                  posTable: AxisDirection.right,
                  width: widthContainerName,
                  height: heightContainer,
                ),
              ),
              Align(
                alignment: Alignment.centerLeft,
                child: PlayerAvatar(
                  autoSizeGroup: autoSizeGroup,
                  posTable: AxisDirection.left,
                  width: widthContainerName,
                  height: heightContainer,
                ),
              ),
              // The bids of the players
              const BidsWidget(
                posTable: AxisDirection.left,
                widthAvatar: widthContainerName,
                heightAvatar: heightContainer,
              ),
              const BidsWidget(
                posTable: AxisDirection.right,
                widthAvatar: widthContainerName,
                heightAvatar: heightContainer,
              ),
              const BidsWidget(
                posTable: AxisDirection.up,
                widthAvatar: widthContainerName,
                heightAvatar: heightContainer,
              ),
              const BidsWidget(
                posTable: AxisDirection.down,
                widthAvatar: widthContainerName,
                heightAvatar: heightContainer,
              ),

              Padding(
                padding: EdgeInsets.only(
                    top: widthContainerName,
                    bottom: marginCardsPosition,
                    left: widthContainerName,
                    right: widthContainerName),
                child: SizedBox.expand(
                  child: MiddleArea(
                    cardHeight: cardHeight,
                    cardWidth: cardWidth,
                    screenSize: screenSize,
                  ),
                ),
              ),
              AnimatedBiddingBar(
                screenSize: screenSize,
                widthContainerName: widthContainerName,
              ),
              if (portrait)
                Align(
                  alignment: Alignment.bottomRight,
                  child: Padding(
                    padding: const EdgeInsets.only(right: 3.0),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        const ButtonLastTrick(),
                        PlayerAvatar(
                            autoSizeGroup: autoSizeGroup,
                            width: widthContainerName,
                            posTable: AxisDirection.down,
                            height: heightContainer),
                      ],
                    ),
                  ),
                ),
              const WaitingPlayersWidget(),
            ],
          ),
        ),
        // My cards
        Row(
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              if (!portrait)
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: const ButtonLastTrick(),
                ),
              Expanded(
                child: CardsInHandWidget(
                  cardHeight: cardHeight,
                  cardWidth: cardWidth,
                  screenWidth: screenSize.width,
                  paddingVertical: paddingHeightCards,
                ),
              ),
              if (!portrait)
                PlayerAvatar(
                  autoSizeGroup: autoSizeGroup,
                  width: widthContainerName,
                  posTable: AxisDirection.down,
                  height: heightContainer,
                )
            ]),
      ],
    );
  }
}
