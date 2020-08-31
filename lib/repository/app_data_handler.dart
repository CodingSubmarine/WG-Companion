import 'dart:async';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:wg_companion/repository/data/app_data.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:wg_companion/repository/preferences.dart';

class AppDataHandler {
  static final AppDataHandler _instance = AppDataHandler._internal();
  String user;

  factory AppDataHandler() => _instance;
  var database = FirebaseDatabase.instance.reference();
  StreamSubscription<Event> databaseSubscription;
  StreamController<void> updateStreamController;
  AppData appData = AppData();
  final dbKey = "Database1";

  AppDataHandler._internal() {
    updateStreamController = StreamController<void>.broadcast();
    databaseCallback();
    getPreference("userkey", "").then((value) {
      user = value;
      if (user == "") {
        user = appData.roommateMap.keys.toList()[0];
      }
    });
  }

  void setUser(String userKey) {
    user = userKey;
    savePreference("userkey", userKey);
    updateStreamController.add(null);
  }

  Future<List<Chore>> changePresent(String roommateUid) async {
    appData.roommateMap[roommateUid].present =
        !appData.roommateMap[roommateUid].present;
    List<Chore> choresChanged = [];
    if (!appData.roommateMap[roommateUid].present) {
      var backupKey = appData
          .getAbsentRoommates()
          .toString()
          .replaceAll("[", "")
          .replaceAll("]", "");
      await swapBackup(backupKey);
      List<String> otherRoommates = appData.roommateMap.keys.toList();
      otherRoommates.remove(roommateUid);
      int i = 0;
      var choresToDistribute = appData.getChoresList(roommateUid);
      choresToDistribute.forEach((chore) {
        chore.turn = otherRoommates[i];
        choresChanged.add(chore);
        i = (i + 1) % otherRoommates.length;
      });
    } else {
      List<String> backupKeyList = appData.getAbsentRoommates();
      backupKeyList.add(roommateUid);
      backupKeyList.sort();
      var backupKey =
          backupKeyList.toString().replaceAll("[", "").replaceAll("]", "");
      swapBackup(backupKey);
    }
    return choresChanged;
  }

  Future<void> swapBackup(String backupKey) async {
    var backupRef = database.child(dbKey).child("backup").child(backupKey);
    var backup = (await backupRef.once()).value;
    if (backup == null) {
      await createBackup(backupKey);
      backup = (await backupRef.once()).value;
    }
    var choreRef = database.child(dbKey).child('chores');
    var chores = (await choreRef.once()).value;
    choreRef.set(backup);
    backupRef.set(chores);
  }

  Future<void> createBackup(String backupKey) async {
    var ref = database.child(dbKey + "/backup/" + backupKey);
    var backup = (await database.child(dbKey + "/chores").once()).value;
    ref.set(backup);
  }

  void loadBackup(String backupKey) async {
    var ref = database.child(dbKey + "/chores");
    var backup =
        (await database.child(dbKey + "/backup/" + backupKey).once()).value;
    ref.set(backup);
  }

  void writeRoommate(String roommateUid) {
    var roommate = appData.roommateMap[roommateUid];
    var ref = database.child(dbKey + '/roommates/' + roommate.uid);
    ref.set(roommate.toMap());
  }

  void writeChore(String choreUid) {
    var chore = appData.choreMap[choreUid];
    var ref = database.child(dbKey + '/chores/' + chore.uid);
    ref.set(chore.toMap());
  }

  void dispose() {
    databaseSubscription.cancel();
  }

  void databaseCallback() {
    databaseSubscription = database.child(dbKey).onValue.listen((Event event) {
      Map<dynamic, dynamic> choresDict = event.snapshot.value['chores'];
      choresDict.forEach((key, value) {
        this.appData.choreMap[key] = Chore.fromDict(value, key);
      });
      Map<dynamic, dynamic> roommateDict = event.snapshot.value['roommates'];
      roommateDict.forEach((key, value) {
        this.appData.roommateMap[key] = Roommate.fromDict(value, key);
      });
      updateStreamController.add(null);
    });
  }
}
