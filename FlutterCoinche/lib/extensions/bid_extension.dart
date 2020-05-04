import 'package:FlutterCoinche/business/calculus.dart';
import 'package:FlutterCoinche/dto/bid.dart';
import 'package:FlutterCoinche/dto/player_position.dart';

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
    return lastWhere(
        (element) =>
            oppositeTeam(me).contains(element.position) &&
            (element is SimpleBid || element is Capot || element is General),
        orElse: () => null);
  }
}
