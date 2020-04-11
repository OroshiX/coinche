//This file is in `./tool` folder

import 'dart:convert';
import 'dart:io';

// To compile you need to run the following command to generate .env.dart file in lib
// -->   dart tool/env.dart
// command to be run in FlutterCoinche Directory

Future<void> main() async {
  final config = {
    'baseUrl': Platform.environment['APP_BASE_URL'],
    'credentials': Platform.environment['APP_CREDENTIALS'],
  };

  final filename = 'lib/.env.dart';
  File(filename).writeAsString('final Map<String, String> environment = ${json.encode(config)};');
}
