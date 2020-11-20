import 'package:coinche/theme/colors.dart';
import 'package:coinche/theme/text_styles.dart';
import 'package:flash/flash.dart';
import 'package:flutter/material.dart';

class FlushUtil {
  static void showError(BuildContext context, String message,
      {void Function()? onClickAction, String? action}) {
    showFlash(
      context: context,
      persistent: true,
      duration: Duration(seconds: 3),
      builder: (context, controller) {
        return Flash(
          controller: controller,
          style: FlashStyle.floating,
          boxShadows: kElevationToShadow[4],
          horizontalDismissDirection: HorizontalDismissDirection.horizontal,
          backgroundColor: kColorDark,
          child: FlashBar(
            message: Text(
              message,
              style: textStyleFlashbar,
            ),
            icon: Icon(
              Icons.error_outline,
              color: Colors.pink,
            ),
            shouldIconPulse: false,
            leftBarIndicatorColor: Colors.pink,
            primaryAction: onClickAction != null && action != null
                ? FlatButton(
                    onPressed: onClickAction,
                    child: Text(action),
                  )
                : null,
          ),
        );
      },
    );
  }

  static void showSuccess(BuildContext context, String message) {
    showFlash(
      context: context,
      persistent: true,
      duration: Duration(milliseconds: 300),
      builder: (context, controller) {
        return Flash(
            controller: controller,
            style: FlashStyle.floating,
            backgroundColor: kColorDark,
            child: FlashBar(
              message: Text(
                message,
                style: textStyleFlashbar,
              ),
              shouldIconPulse: false,
              icon: Icon(
                Icons.info_outline,
                color: Colors.green,
              ),
              leftBarIndicatorColor: Colors.green,
            ));
      },
    );
  }
}
