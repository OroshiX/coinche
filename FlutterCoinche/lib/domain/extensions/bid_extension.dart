import 'package:coinche/domain/dto/bid.dart';
import 'package:coinche/domain/dto/player_position.dart';
import 'package:coinche/domain/logic/calculus.dart';
import 'package:coinche/util/list_util.dart';

extension BidExtension on List<Bid> {
  bool canCoinche(PlayerPosition me) =>
      _coinche == null && _lastBidCapotGeneralOpposite(me) != null;

  bool canSurcoinche(PlayerPosition me) {
    final c = _coinche;
    return c != null && !c.surcoinche && myTeam(me).contains(c.getTaker());
  }

  Coinche? get _coinche {
    return lastWhereOrNull((element) => element is Coinche) as Coinche?;
  }

  Bid? lastBidCapotGeneral() {
    return lastWhereOrNull((element) =>
        element is SimpleBid || element is Capot || element is General);
  }

  Bid? _lastBidCapotGeneralOpposite(PlayerPosition me) {
    var lastBid = lastWhereOrNull((element) =>
        element is SimpleBid || element is Capot || element is General);
    if (lastBid == null) return null;
    if (!oppositeTeam(me).contains(lastBid.position)) {
      return null;
    }
    return lastBid;
  }
}
