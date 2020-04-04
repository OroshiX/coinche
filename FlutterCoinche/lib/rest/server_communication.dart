import 'package:FlutterCoinche/.env.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:http/http.dart' as http;

class ServerCommunication {
  static final String _baseUrl = environment["baseUrl"];

  static Future<bool> sendToken(IdTokenResult idTokenResult) async {
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

  static Future<bool> logout() async {
    var url = "$_baseUrl/logout";
    var response = await http.post(url);
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }
}
