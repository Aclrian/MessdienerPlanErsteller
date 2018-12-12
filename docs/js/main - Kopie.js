function getData(){
	var url;
	var alternative = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest";
	$.getJSON("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest", function( data ) {
		var zahl = -1;
		var pf = navigator.platform;
		for(var i=0;i<data.assets.length;i++){
			if(data.assets[i].name.endsWith(".exe") && isWindows()){
				//document.getElementById("warten").innerHTML = "<p>"+i+"<p>";
				zahl = i;
				break;
			}
			if(!(data.assets[i].name.endsWith(".exe")) && !(isWindows())){
				//document.getElementById("warten").innerHTML = "<p>"+i+"<p>";
				zahl = i;
				break;
			}
		}
		url = data.assets[i].browser_download_url;
		if(url == undefined || url == null){
			window.location = alternative;
		} else{
			console.log(url);
			if(url.search("github.com/Aclrian/MessdienerPlanErsteller/") != -1){
				window.location = url;
			} else{
				window.location = alternative;
			}
		}
	});
};
function isWindows() {
  return navigator.platform.indexOf('Win') > -1
}