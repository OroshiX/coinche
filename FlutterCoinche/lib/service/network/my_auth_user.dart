class MyAuthUser {
  final String uid;
  final String? displayName;
  final String? email;
  final bool isEmailVerified;
  final bool isAnonymous;

  MyAuthUser({
    required this.uid,
    required this.displayName,
    this.email,
    this.isEmailVerified = false,
    this.isAnonymous = false,
  });

  @override
  String toString() {
    return '$displayName';
  }
}
