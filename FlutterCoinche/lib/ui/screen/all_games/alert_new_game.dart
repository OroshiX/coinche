import 'package:FlutterCoinche/domain/dto/game_empty.dart';
import 'package:FlutterCoinche/service/network/server_communication.dart';
import 'package:FlutterCoinche/state/game_model.dart';
import 'package:FlutterCoinche/ui/resources/colors.dart';
import 'package:FlutterCoinche/ui/screen/game/game_screen_provided.dart';
import 'package:flushbar/flushbar.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class DialogNewGame extends StatefulWidget {
  const DialogNewGame({Key key}) : super(key: key);

  @override
  _DialogNewGameState createState() => _DialogNewGameState();
}

class _DialogNewGameState extends State<DialogNewGame> {
  bool _automated;
  final TextEditingController _controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    _automated = false;
  }

  @override
  Widget build(BuildContext context) {
    var formKey = GlobalKey<FormState>();
    return AlertDialog(
      title: Text("Name of the game"),
      content: Form(
          key: formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: _controller,
                validator: (value) => value.isNotEmpty
                    ? null
                    : "Please submit a name for the game",
              ),
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    "Allow bots?",
                    style: TextStyle(color: colorTextDark),
                  ),
                  SizedBox(
                    width: 10,
                  ),
                  Switch(
                    value: _automated,
                    onChanged: (bool value) {
                      setState(() {
                        _automated = value;
                        print("automated: $_automated");
                      });
                    },
                  ),
                ],
              )
            ],
          )),
      actions: <Widget>[
        FlatButton(
            onPressed: () {
              if (formKey.currentState.validate()) {
                ServerCommunication.createGame(_controller.text +
                        (_automated ? GameEmpty.automatedString : ""))
                    .then((value) {
                  context.read<GameModel>().changeGame(value.id);
                  Navigator.of(context).pop();
                  Navigator.of(context).pushNamed(GameScreenProvided.routeName);
                },
                        onError: (error) => Flushbar(
                              message: "Oh no! Please check your connection",
                            ).show(context));
              }
            },
            child: Text("OK"))
      ],
    );
  }
}
