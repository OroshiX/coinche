extension ListExtension<T> on List<T> {
  T? firstOrNull() {
    if (isEmpty) return null;
    return first;
  }

  T? lastOrNull() {
    if (isEmpty) return null;
    return last;
  }

  T? firstWhereOrNull(bool Function(T element) condition) {
    var i = indexWhere(condition);
    if (i == -1) return null;
    return this[i];
  }

  T? lastWhereOrNull(bool Function(T element) condition) {
    var i = lastIndexWhere(condition);
    if (i == -1) return null;
    return this[i];
  }

  List<IndexedValue<T>> indexedValues() {
    var res = <IndexedValue<T>>[];
    for (var i = 0; i < length; i++) {
      res.add(IndexedValue(i, this[i]));
    }
    return res;
  }
}

class IndexedValue<T> {
  final int i;
  final T value;

  IndexedValue(this.i, this.value);
}
