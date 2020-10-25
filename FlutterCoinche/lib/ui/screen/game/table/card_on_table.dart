import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/ui/widget/card_widget.dart';
import 'package:flutter/material.dart';

class CardOnTable extends StatefulWidget {
  final double cardHeight, cardWidth;
  final CardModel card;

  const CardOnTable({
    Key key,
    @required this.cardHeight,
    @required this.cardWidth,
    @required this.card,
  }) : super(key: key);

  @override
  CardOnTableState createState() => CardOnTableState();
}

class CardOnTableState extends State<CardOnTable>
    with SingleTickerProviderStateMixin {
  Animation<double> _rotation;
  Animation<Offset> _offset;
  AnimationController _controller;
  int lastRequestTimestamp;

  CardModel _cardModel;

  @override
  void initState() {
    super.initState();
    lastRequestTimestamp = DateTime.now().millisecondsSinceEpoch;
    _controller = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 500));
    _cardModel = widget.card;
  }

  @override
  Widget build(BuildContext context) {
    return SlideTransition(
      position: _offset,
      child: RotationTransition(
        turns: _rotation,
        child: Center(
          child: CardWidget(
              card: _cardModel,
              width: widget.cardWidth,
              height: widget.cardHeight),
        ),
      ),
    );
  }
}
