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
}
