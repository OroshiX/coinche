import 'package:flutter/material.dart';

class TextFieldRound extends StatelessWidget {
  final String hint;
  final IconData iconData;
  final String? Function(String? value) validator;
  final TextEditingController? controller;
  final bool hidden;
  final Widget? suffix;

  const TextFieldRound(
      {Key? key,
      required this.hint,
      required this.iconData,
      required this.validator,
      this.controller,
      this.hidden = false,
      this.suffix})
      : super(key: key);
  @override
  Widget build(BuildContext context) {
    return TextFormField(
      validator: validator,
      obscureText: hidden,
      decoration: InputDecoration(
        border: InputBorder.none,
        hintText: hint,
        prefixIcon: Icon(iconData),
        suffix: suffix,
      ),
      controller: controller,
    );
  }
}
