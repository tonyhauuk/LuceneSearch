function getObj(id) {
	var Obj = document.getElementById(id).value;
	return Obj;
}

function check() {
	if (getObj("searchId") == "") {
		document.getElementById("searchId").focus;
		return false;
	}
}

function sel() {
	document.thisform.submit();
}

function setDot(id) {
	var a = document.getElementsByName("dot");
	for (var i = 0; i < a.length; i++) {
		var c = a[i].getAttribute("value");
		if (c == id) {
			a[i].setAttribute("checked", "checked");
		}
	}
}

function setDotF(fid) {
	var a = document.getElementsByName("dotF");
	for (var i = 0; i < a.length; i++) {
		var c = a[i].getAttribute("value");
		if (c == fid) {
			a[i].setAttribute("checked", "checked");
		}
	}
}

