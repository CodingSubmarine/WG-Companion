import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wg_companion/repository/app_data_handler.dart';
import 'package:wg_companion/repository/data/app_data.dart';

class ChoreItem extends StatelessWidget {
  Chore chore;

  ChoreItem(Chore chore) : this.chore = chore;
  var appDataHandler = AppDataHandler();

  Color getColor(int priority) {
    switch (priority) {
      case 0:
        {
          return Colors.white;
        }
      case 1:
        {
          return Colors.orangeAccent;
        }
      case 2:
        {
          return Colors.redAccent;
        }
    }
    return Colors.white;
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onLongPress: () {
        print(chore.priority);
        appDataHandler.appData.incrementPriority(chore.uid);
        appDataHandler.writeChore(chore.uid);
      },
      onTap: () {
        print('tapped');
        appDataHandler.appData.moveTurn(chore.uid);
        appDataHandler.writeChore(chore.uid);
      },
      child: Container(
        height: 50,
        child: Padding(
          padding: const EdgeInsets.all(2.5),
          child: Container(
            decoration: BoxDecoration(
                color: getColor(chore.priority),
                border: Border.all(color: Colors.black),
                borderRadius: BorderRadius.circular(5.0)),
            child: Center(
                child: AutoSizeText(
              chore.name,
              style: TextStyle(fontSize: 20),
              maxLines: 3,
            )),
          ),
        ),
      ),
    );
  }
}
