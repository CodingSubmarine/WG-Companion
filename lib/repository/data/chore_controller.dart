import 'dart:async';
import 'package:firebase_database/firebase_database.dart';

class ChoreController {
  var database = FirebaseDatabase.instance.reference();
  StreamSubscription<Event> databaseSubscription;
  var counter = 0;

  void dispose() {
    databaseSubscription.cancel();
  }

  Future<void> readDatabase() async {
    var test = (await database.child('test').once()).value;
    print(test);
  }

  void databaseCallback() {
    databaseSubscription = database.child('test').onValue.listen((Event event) {
      print(event.snapshot.value ?? "Nüschd");
      counter += 1;
      print(counter);
    });
  }
}
