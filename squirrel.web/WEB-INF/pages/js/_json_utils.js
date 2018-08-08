function JSONElementToString(obj) {
  if (obj.length == 0) {
    return "Nothing to show...";
  }
  if (obj instanceof Array) {
    return JSONArrayToString(obj);
  }
  if (obj instanceof Object) {
    var ret = "";
    Object.keys(obj).forEach(function (value) {
      ret += "<h6>" + value + "</h6>";
      ret += JSONElementToString(obj[value]);
    });
    return ret;
  }

  return "Failed to convert " + obj;
}

function JSONArrayToString(obj) {
  var ret = "<ul>";
  obj.forEach(function (value) {
    if (value instanceof Array) {
      ret += JSONArrayToString(value);
    } else {
      ret += "<li>" + value + "</li>";
    }
  });
  return ret + "</ul>";
}

function IntToTime(seconds) {
  if (seconds <= 99) {
    return seconds + " s";
  }
  if (seconds <= 9900) {
    return "~" + Math.round(seconds / 60.0) + " min";
  }
  var days = Math.floor(seconds / 86400.0);
  var hours = Math.round((seconds - days * 86400) / 3600.0);
  return days + " days and ~" + hours + " h"
}
