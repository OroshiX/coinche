import 'package:flutter/material.dart';

class LoginScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final height = MediaQuery.of(context).size.height;
    return Scaffold(
      backgroundColor: Color.fromRGBO(193, 214, 233, 1),
      body: SafeArea(
        child: Column(
          children: <Widget>[
            SizedBox(
              height: height * 0.54,
              child: ClipRRect(
                borderRadius:
                    BorderRadius.only(bottomRight: Radius.circular(150)),
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 25),
                  decoration: BoxDecoration(
                      gradient: LinearGradient(
                          begin: Alignment(-1, -1),
                          end: Alignment(1, 1),
                          colors: [
                        Color.fromRGBO(255, 237, 167, 1),
                        Color.fromRGBO(249, 177, 125, 1),
                      ])),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: <Widget>[
                            Padding(
                              padding: const EdgeInsets.symmetric(
                                  vertical: 25, horizontal: 5),
                              child: Center(
                                child: Text(
                                  "Sign In",
                                  style: TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontStyle: FontStyle.italic),
                                ),
                              ),
                            ),
                            Text("User name"),
                            SizedBox(
                              height: 10,
                            ),
                            TextField(
                              onChanged: (value) {
                                print("$value has changed");
                              },
                            ),
                            SizedBox(
                              height: 20,
                            ),
                            Text("Password"),
                            SizedBox(
                              height: 10,
                            ),
                            Stack(children: [
                              TextField(
                                style: TextStyle(),
                                onChanged: (value) {
                                  print("$value has changed");
                                },
                              ),
                              Positioned(
                                right: 2,
                                child: IconButton(
                                  onPressed: () {
                                    print("pressed help");
                                  },
                                  icon: Icon(
                                    Icons.help_outline,
                                    color: Colors.black54,
                                  ),
                                ),
                              )
                            ]),
                            SizedBox(
                              height: 45,
                            ),
                            Center(
                              child: FlatButton(
                                onPressed: () {},
                                child: Container(
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    color: Color.fromRGBO(42, 48, 57, 1),
                                  ),
                                  child: Padding(
                                    padding: const EdgeInsets.all(20.0),
                                    child: Icon(
                                      Icons.arrow_forward,
                                      color: Colors.white,
                                    ),
                                  ),
                                ),
                              ),
                            )
                          ],
                        ),
                      )
                    ],
                  ),
                ),
              ),
            ),
            Spacer(),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(
                "Continue as",
                style: TextStyle(color: Colors.blueGrey),
              ),
            ),
            SizedBox(
                height: height * 0.3,
                child: Column(
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 45),
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: <Widget>[
                            Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              children: <Widget>[
                                FlatButton(
                                  child: Text("F"),
                                  onPressed: () => print("Pressed"),
                                ),
                                Text("Facebook")
                              ],
                            ),
                            Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              children: <Widget>[
                                FlatButton(
                                  child: Text("G"),
                                  onPressed: () => print("Pressed"),
                                ),
                                Text("Google")
                              ],
                            )
                          ]),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 25),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: <Widget>[
                          Text("Forgot your password?"),
                          FlatButton(
                            child: Text(
                              "We got you",
                              style: TextStyle(fontWeight: FontWeight.bold),
                            ),
                            onPressed: () {},
                          )
                        ],
                      ),
                    )
                  ],
                )),
          ],
        ),
      ),
    );
  }
}
