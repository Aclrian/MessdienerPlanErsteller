function getData(){
	var url;
	var alternative = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest";
	//document.write('<a href="./wiki.html">H</a>');
	$.getJSON("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest", function( data ) {
		url = data.assets[0].browser_download_url;
		if(url == undefined || url == null){
			window.location = alternative;
		} else{
			console.log(url);
			console.log(url.search("github.com/Aclrian/MessdienerPlanErsteller/"));
			if(url.search("github.com/Aclrian/MessdienerPlanErsteller/") != -1){
				window.location = url;
				//return;
			} else{
				window.location = alternative;
				//return;
			}
		}
		//do what you want with data
	});
	if(url == undefined || url == null){
	//window.location = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest";
	}};
