function append(){
	var ele = document.getElementsByClassName("versteckt")[0].innerText;
	console.log("do"+ele);
	var neu = " :📧";
	var m = document.createElement("span");
	m.innerText = neu;
	document.getElementsByClassName("versteckt")[0].appendChild(m);
}
