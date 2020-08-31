import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wg_companion/ui/chore_view/chore_column.dart';
import 'package:wg_companion/repository/app_data_handler.dart';
import 'package:wg_companion/repository/data/app_data.dart';

class ChorePage extends StatefulWidget {
  var appData = AppDataHandler();

  @override
  _ChorePageState createState() => _ChorePageState();
}

class _ChorePageState extends State<ChorePage> {
  Map<String, Roommate> roommateMap = {};
  List<Widget> columnlist = [];
  StreamSubscription streamSub;

  @override
  void initState() {
    super.initState();
    streamSub = widget.appData.updateStreamController.stream.listen((data) {
      if (AppDataHandler().appData.roommateMap.length != columnlist.length) {
        setState(() {
          roommateMap = AppDataHandler().appData.roommateMap ?? {};
        });
      }
    });
  }

  @override
  void dispose() {
    streamSub.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    columnlist = [];
    roommateMap.forEach((key, val) {
      columnlist.add(ChoreColumn(val.uid));
    });
    return SafeArea(
      child: Scaffold(
        body: Container(
          decoration: BoxDecoration(color: Colors.black54),
          child: Row(
            children: columnlist,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.stretch,
          ),
        ),
        bottomNavigationBar: BottomNavigationBar(
          items: const <BottomNavigationBarItem>[
            BottomNavigationBarItem(
                icon: Icon(Icons.assignment), title: Text("Aufgaben")),
            BottomNavigationBarItem(
                icon: Icon(Icons.control_point_duplicate),
                title: Text("Credits"))
          ],
        ),
      ),
    );
  }
}
