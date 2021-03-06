import 'package:coinche/domain/dto/card.dart';
import 'package:coinche/ui/screen/game/table/animated_card.dart';
import 'package:coinche/ui/widget/card_widget.dart';
import 'package:coinche/ui/widget/translated_transition.dart';
import 'package:flutter/material.dart';

class MoveCard extends StatefulWidget {
  final double cardWidth, cardHeight;
  final CardModel? card;
  final String offsetAndRotationName;

  const MoveCard(
      {Key? key,
      required this.cardWidth,
      required this.cardHeight,
      required this.card,
      required this.offsetAndRotationName})
      : super(key: key);

  @override
  MoveCardState createState() => MoveCardState();
}

class MoveCardState extends State<MoveCard>
    with SingleTickerProviderStateMixin {
  Animation<double>? _rotation;
  Animation<Offset>? _offset;
  late AnimationController _controller;
  late CardModel? _cardModel;
  late int lastRequestTimestamp;

  set cardModel(CardModel? value) {
    setState(() {
      _cardModel = value;
    });
  }

  @override
  void initState() {
    super.initState();
    lastRequestTimestamp = DateTime.now().millisecondsSinceEpoch;
    _controller = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 500));
    _initAnim(OffsetAndRotation.zero(lastRequestTimestamp));
    _cardModel = widget.card;
  }

  void _initAnim(
    OffsetAndRotation destination,
  ) {
    print("init anim to $destination");
    setAnim(destination);
  }

  void setAnim(OffsetAndRotation destination,
      {OffsetAndRotation? origin, bool shouldAnim = true}) {
//    assert(!shouldAnim || origin != null)
    if (destination.timestamp < lastRequestTimestamp && shouldAnim) {
      print(
          "============= We won't do that, it's too late! ($destination) ==========");
      return;
    }
    lastRequestTimestamp = destination.timestamp;
    print("time: $destination");
    if (!shouldAnim) {
      origin = destination;
    }

    origin ??= _offset != null && _rotation != null
        ? OffsetAndRotation(
            _offset!.value, _rotation!.value, lastRequestTimestamp)
        : destination;

    setState(() {
      _rotation =
          Tween<double>(begin: origin!.rotation, end: destination.rotation)
              .animate(_controller);
      _offset = Tween<Offset>(begin: origin.offset, end: destination.offset)
          .animate(_controller);
      _controller.fling();
    });
    _controller.forward(from: 0);
  }

  @override
  Widget build(BuildContext context) {
    var child = Center(
        child: _cardModel != null
            ? CardWidget(
                card: _cardModel!,
                width: widget.cardWidth,
                height: widget.cardHeight)
            : SizedBox());
    if (_offset == null || _rotation == null) {
      // todo get real offset and rotation
      return Transform.translate(
        offset: Offset(0, 0),
        child: Transform.rotate(
          angle: 0,
          child: child,
        ),
      );
    }
    return TranslatedTransition(
      animation: _offset!,
      child: RotatedTransition(
        animation: _rotation!,
        child: child,
      ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
