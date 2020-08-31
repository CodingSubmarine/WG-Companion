import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:wg_companion/repository/data/app_data.dart';
import 'package:wg_companion/ui/chore_view/chore_item.dart';
import 'package:wg_companion/repository/app_data_handler.dart';

class ChoreColumn extends StatefulWidget {
  var appData = AppDataHandler();
  Roommate roommate;
  List<Chore> choreList = [];

  ChoreColumn(String uid) {
    roommate =
        AppDataHandler().appData.roommateMap[uid] ?? Roommate("", uid, true);
    choreList = AppDataHandler().appData.getChoresList(roommate.uid);
  }

  @override
  _ChoreColumnState createState() => _ChoreColumnState();
}

class _ChoreColumnState extends State<ChoreColumn> {
  StreamSubscription streamSub;

  @override
  void initState() {
    super.initState();
    streamSub = widget.appData.updateStreamController.stream.listen((data) {
      List<Chore> newChoreList =
          widget.appData.appData.getChoresList(widget.roommate.uid);
      if (newChoreList != widget.choreList) {
        setState(() {
          print('set state');
          widget.choreList = newChoreList;
        });
      } else if (widget.roommate !=
          widget.appData.appData.roommateMap[widget.roommate.uid]) {
        setState(() {
          widget.roommate =
              widget.appData.appData.roommateMap[widget.roommate.uid];
        });
      } else {
        setState(() {});
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
    List<ChoreItem> choreItems = [];
    widget.choreList.sort((a, b) => b.priority.compareTo(a.priority));
    widget.choreList.forEach((chore) {
      choreItems.add(ChoreItem(chore));
    });

    List<Widget> columnList = [
      GestureDetector(
        onTap: () async {
          widget.appData.setUser(widget.roommate.uid);
          setState(() {});
        },
        onLongPress: () async {
          var changedChores =
              await widget.appData.changePresent(widget.roommate.uid);
          widget.appData.writeRoommate(widget.roommate.uid);
          changedChores.forEach((chore) {
            widget.appData.writeChore(chore.uid);
          });
        },
        child: Container(
          height: 50,
          decoration: BoxDecoration(
              color: widget.roommate.present
                  ? (widget.appData.user == widget.roommate.uid
                      ? Colors.amber
                      : Colors.blueGrey)
                  : Colors.black45,
              border: Border(
                  bottom: BorderSide(color: Theme.of(context).dividerColor),
                  top: BorderSide(color: Theme.of(context).dividerColor))),
          child: Center(
            child: Text(widget.roommate.name),
          ),
        ),
      )
    ];
    columnList.addAll(choreItems);

    return Flexible(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 2.0),
        child: Container(
            color: widget.roommate.present ? Colors.white : Colors.black45,
            child: Column(children: columnList)),
      ),
      flex: 1,
    );
  }
}
