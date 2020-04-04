import 'package:FlutterCoinche/.env.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:http/http.dart' as http;

class ServerCommunication {
  static sendToken(IdTokenResult idTokenResult) async {
    final String _baseUrl = environment["baseUrl"];

//    var url = 'http://192.168.1.11:8080/loginToken';
    var url = '$_baseUrl/loginToken';
    print("connecting to Server $url");

    var response = await http.post(url, body: idTokenResult.token);
    if (response.statusCode == 200) {
      print("success sending token");
      return true;
    }
    print("problem sending token");
    return false;
  }
}
