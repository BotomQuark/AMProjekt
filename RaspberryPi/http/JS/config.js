var baseUrl;

/**
* @brief Updates the values of imputs in HTML
* @param objectJSON JSON object with the configuration data
*/
function updateInputValues(objectJSON){
	document.getElementById("ipAdress").value = objectJSON.ip;
	document.getElementById("port").value = objectJSON.port;
	document.getElementById("sampleTime").value = objectJSON.sampleTime;
	document.getElementById("sampleQuantity").value = objectJSON.sampleQuantity;
}

/**
* @brief GET request to .json file with configuration data
*/
function getConfigData() {
	$.ajax(baseUrl+"config.json", {
		type: 'GET', 
		dataType: 'text',
		crossDomain: true,
		success: function(responseTEXT, status, xhr) {
			var responseJSON = JSON.parse(responseTEXT);
			debugHelp1=responseJSON;
			console.log("Ajax success");
			updateInputValues(responseJSON);
		},
		error: function (ajaxContext) {
			console.log("Ajax error");
		},
		cache: false
	});
}

/**
* @brief Gets string text appended to the url for the sake of POST request
* @return dataString string with data in the POST format
*/
function getConfigDataForPostRequest(){
	var ip=document.getElementById("ipAdress").value;
	var port=document.getElementById("port").value;
	var sampleTime=document.getElementById("sampleTime").value;
	var sampleQuantity=document.getElementById("sampleQuantity").value;
	
	var dataString='';
	dataString="ip="+ip+"&&port="+port+"&&sampleTime="+sampleTime+"&&sampleQuantity="+sampleQuantity;
	return dataString
}

/**
* @brief POST request to setConfig.php in order to update the configuration file on the server
*/
function updateConfig(){
	//Format for POST method: ip=...&&port=...&&sampleTime=...&&sampleQuantity=...
	var dataText=getConfigDataForPostRequest();
	console.log("Updating Config");
	
	$.ajax(baseUrl+"setConfig.php", {
			type: "POST",
			data:dataText,
			dataType: "text",
			crossDomain: true,
			beforeSend: function(x) {
				console.log("AJAX POST REQUEST: BEGIN SENDING");
			},
			success: function(result) {
				console.log("AJAX POST REQUEST: SUCCESFULL CODE: " + result);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("AJAX POST REQUEST: FAILURE");
				console.log("STATUS: " + textStatus);
				console.log("ERROR: " + errorThrown);
			},
			cache: false
	});
	
	getConfigData();
}

/**
* @brief Empty GET Request intializing setting default configuration options
*/
function resetConfig(){
	console.log("Reseting Config");
	
	$.ajax(baseUrl+"setConfig.php", {
			type: "GET",
			crossDomain: true,
			beforeSend: function(x) {
				console.log("AJAX POST REQUEST: BEGIN SENDING");
			},
			success: function(result) {
				console.log("AJAX POST REQUEST: SUCCESFULL CODE: " + result);
				getConfigData();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("AJAX POST REQUEST: FAILURE");
				console.log("STATUS: " + textStatus);
				console.log("ERROR: " + errorThrown);
			},
			cache: false
	});
}

/**
* @brief Function called when the document loads
*/
$(document).ready(function(){
	$.ajaxSetup({
		cache: false
	});
	baseUrl=window.location.href;
	baseUrl=baseUrl.slice(0, baseUrl.length-10);
	getConfigData();
});