import 'package:flutter/material.dart';

class TextFieldRound extends StatelessWidget {
  final String hint;
  final IconData iconData;
  final String? Function(String? value) validator;
  final TextEditingController? controller;
  final bool hidden;
  final IconData? suffixIcon;
  final void Function()? onPressedSuffix;
  final void Function()? onEditingComplete;

  final TextInputType? textInputType;

  final List<String>? autofillHints;

  const TextFieldRound(
      {Key? key,
      required this.hint,
      required this.iconData,
      required this.validator,
      this.controller,
      this.hidden = false,
      this.suffixIcon,
      this.onPressedSuffix,
      this.onEditingComplete,
      this.textInputType,
      this.autofillHints})
      : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          color: Colors.white24, borderRadius: BorderRadius.circular(50)),
      padding: const EdgeInsets.only(left: 20, right: 4, top: 3, bottom: 3),
      child: TextFormField(
        validator: validator,
        obscureText: hidden,
        keyboardType: textInputType,
        autofillHints: autofillHints,
        onEditingComplete: onEditingComplete,
        decoration: InputDecoration(
          border: InputBorder.none,
          isCollapsed: true,
          isDense: true,
          hintText: hint,
          icon: Icon(
            iconData,
            size: 24,
          ),
          suffix: Padding(
            padding: const EdgeInsets.only(right: 10, top: 2),
            child: InkWell(
              child: Icon(
                suffixIcon,
                size: 25,
              ),
              onTap: onPressedSuffix,
            ),
          ),
        ),
        controller: controller,
      ),
    );
  }
}
