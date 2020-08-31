class AppData {
  Map<String, Chore> choreMap = {};
  Map<String, Roommate> roommateMap = {};

  List<Chore> getChoresList(String roommateId) {
    List<Chore> chores = [];
    choreMap.forEach((key, chore) {
      if (chore.turn == roommateId) {
        chores.add(chore);
      }
    });
    chores.sort((a, b) => b.priority.compareTo(a.priority));
    return chores;
  }

  void incrementPriority(String choreUid) {
    var current_priority = choreMap[choreUid].priority;
    choreMap[choreUid].priority = (current_priority + 1) % 3;
  }

  void moveTurn(String choreUid) {
    var sortedRoommateIds = roommateMap.keys.toList()..sort();
    var current_idx = sortedRoommateIds.indexOf(choreMap[choreUid].turn);
    var next_idx = (current_idx + 1) % sortedRoommateIds.length;
    while (!roommateMap[sortedRoommateIds[next_idx]].present) {
      next_idx = (next_idx + 1) % sortedRoommateIds.length;
    }
    choreMap[choreUid].turn = sortedRoommateIds[next_idx];
    choreMap[choreUid].priority = 0;
  }

  List<String> getAbsentRoommates() {
    List<String> absentRoommates = [];
    roommateMap.forEach((key, val) {
      if (!val.present) {
        absentRoommates.add(key);
      }
    });
    absentRoommates.sort();
    return absentRoommates;
  }
}

class Chore {
  final String name;
  final String uid;
  int priority;
  String turn;

  Chore(String name, String uid, int priority, String turn)
      : this.name = name,
        this.uid = uid {
    this.priority = priority;
    this.turn = turn;
  }

  Chore.fromDict(Map<dynamic, dynamic> dict, String uid)
      : this.name = dict['name'],
        this.uid = uid,
        this.priority = dict['priority'],
        this.turn = dict['turn'];

  Map<String, dynamic> toMap() {
    return {'name': name, 'priority': priority, 'turn': turn};
  }
}

class Roommate {
  final String name;
  final String uid;
  bool present;

  Map<Chore, int> creditMap;

  Roommate(String name, String uid, bool present)
      : this.name = name,
        this.uid = uid {
    this.present = present;
  }

  Roommate.fromDict(Map<dynamic, dynamic> dict, String uid)
      : this.name = dict['name'],
        this.uid = uid,
        this.present = dict['present'];

  Map<String, dynamic> toMap() {
    return {'name': name, 'present': present};
  }
}
