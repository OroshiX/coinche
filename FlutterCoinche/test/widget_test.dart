// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:FlutterCoinche/domain/dto/card.dart';
import 'package:FlutterCoinche/ui/widget/card_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/container.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('Counter increments smoke test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(MaterialApp(
      home: Scaffold(
        body: Container(
          width: 50,
          height: 60,
          child: CardWidget(
            card: CardModel(
                color: CardColor.CLUB, playable: true, value: CardValue.KING),
            width: 40,
            height: 60,
            displayPlayable: true,
          ),
        ),
      ),
    ));

    // Verify that our counter starts at 0.
    expect(find.text('K'), findsNWidgets(2));
    expect(find.text('1'), findsNothing);

    // Tap the '+' icon and trigger a frame.
//    await tester.tap(find.byIcon(Icons.add));
//    await tester.pump();

    // Verify that our counter has incremented.
//    expect(find.text('0'), findsNothing);
//    expect(find.text('1'), findsOneWidget);
  });
}
