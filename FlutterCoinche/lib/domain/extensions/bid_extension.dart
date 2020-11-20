import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/logic/calculus.dart';

extension BidExtension on List<Bid> {
  bool canCoinche(PlayerPosition me) =>
      _coinche == null && _lastBidCapotGeneralOpposite(me) != null;

  bool canSurcoinche(PlayerPosition me) {
    final c = _coinche;
    return c != null && !c.surcoinche && myTeam(me).contains(c.getTaker());
  }

  Coinche? get _coinche {
    var i = lastIndexWhere((element) => element is Coinche);
    if (i == -1) return null;
    return this[i] as Coinche;
  }

  Bid? lastBidCapotGeneral() {
    var i = lastIndexWhere((element) =>
        element is SimpleBid || element is Capot || element is General);
    if (i == -1) return null;
    return this[i];
  }

  Bid? _lastBidCapotGeneralOpposite(PlayerPosition me) {
    var i = lastIndexWhere((element) =>
        element is SimpleBid || element is Capot || element is General);
    if (i == -1) return null;
    var lastBid = this[i];
    if (!oppositeTeam(me).contains(lastBid.position)) {
      return null;
    }
    return lastBid;
  }
}
