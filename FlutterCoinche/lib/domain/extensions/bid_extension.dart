import 'package:FlutterCoinche/domain/dto/bid.dart';
import 'package:FlutterCoinche/domain/dto/player_position.dart';
import 'package:FlutterCoinche/domain/logic/calculus.dart';

extension BidExtension on List<Bid> {
  bool canCoinche(PlayerPosition me) =>
      (_coinche == null && this._lastBidCapotGeneralOpposite(me) != null);

  bool canSurcoinche(PlayerPosition me) {
    final c = _coinche;
    return c != null && !c.surcoinche && myTeam(me).contains(c.getTaker());
  }

  Coinche get _coinche => lastWhere(
        (element) => element is Coinche,
        orElse: () => null,
      );

  Bid lastBidCapotGeneral() {
    return lastWhere(
      (element) =>
          element is SimpleBid || element is Capot || element is General,
      orElse: () => null,
    );
  }

  Bid _lastBidCapotGeneralOpposite(PlayerPosition me) {
    var lastBid = lastWhere(
        (element) =>
            element is SimpleBid || element is Capot || element is General,
        orElse: () => null,);
    if (lastBid == null || !oppositeTeam(me).contains(lastBid.position)) {
      return null;
    }
    return lastBid;
  }
}
