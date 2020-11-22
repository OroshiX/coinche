import 'package:coinche/domain/dto/game_empty.dart';
import 'package:coinche/service/network/server_communication.dart';
import 'package:coinche/state/game_model.dart';
import 'package:coinche/state/login_model.dart';
import 'package:coinche/theme/colors.dart';
import 'package:coinche/ui/screen/game/game_screen_provided.dart';
import 'package:coinche/util/flush_util.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class DialogNewGame extends StatefulWidget {
  const DialogNewGame({Key? key}) : super(key: key);

  @override
  _DialogNewGameState createState() => _DialogNewGameState();
}

class _DialogNewGameState extends State<DialogNewGame> {
  bool _automated = false;
  final TextEditingController _controller = TextEditingController();

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
                validator: (value) => (value?.isNotEmpty ?? false)
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
              if (formKey.currentState?.validate() ?? false) {
                ServerCommunication.createGame(
                  _controller.text +
                      (_automated ? GameEmpty.automatedString : ""),
                  onSuccess: (GameEmpty gameEmpty) {
                    context.read<GameModel>().changeGame(
                        idGame: gameEmpty.id,
                        userUid: context.read<LoginModel>().user?.uid);
                    Navigator.of(context)?.pop();
                    Navigator.of(context)
                        ?.pushNamed(GameScreenProvided.routeName);
                  },
                  onError: (message) {
                    FlushUtil.showError(context, message);
                  },
                );
              }
            },
            child: Text("OK"))
      ],
    );
  }
}
