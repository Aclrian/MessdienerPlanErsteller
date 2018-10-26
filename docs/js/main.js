function getData(){
	var url;
	var alternative = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest";
	$.getJSON("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest", function( data ) {
		url = data.assets[0].browser_download_url;
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
