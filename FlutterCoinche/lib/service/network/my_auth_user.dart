import 'package:flutter/material.dart';

class MyAuthUser {
  final String uid;
  final String displayName;
  final String email;
  final bool isEmailVerified;
  final bool isAnonymous;

  MyAuthUser({
    @required this.uid,
    @required this.displayName,
    @required this.email,
    @required this.isEmailVerified,
    @required this.isAnonymous,
  });

  @override
  String toString() {
    return '$displayName';
  }
}
