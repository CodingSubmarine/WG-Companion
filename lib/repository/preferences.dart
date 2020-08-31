import 'package:shared_preferences/shared_preferences.dart';

Future<dynamic> getPreference(String key, dynamic defaultValue) async {
  final prefs = await SharedPreferences.getInstance();
  return prefs.get(key) ?? defaultValue;
}

savePreference(String key, dynamic value) async {
  final prefs = await SharedPreferences.getInstance();

  if (value is int) {
    prefs.setInt(key, value);
  } else if (value is String) {
    prefs.setString(key, value);
  } else if (value is bool) {
    prefs.setBool(key, value);
  } else if (value is double) {
    prefs.setDouble(key, value);
  } else if (value is List<String>) {
    prefs.setStringList(key, value);
  } else {
    throw (Exception(
        "${value.runtimeType} cannot be saved using SharedPreferences!"));
  }
}
