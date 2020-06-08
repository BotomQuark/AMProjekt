var key="all";

var rpyUrl = 'http://192.168.56.101/rpy.json';
var tphUrl = 'http://192.168.56.101/tph.json';
var joyUrl = 'http://192.168.56.101/joy.json';

var baseUrl='';

var timer;

var sampleTimeSec = 0.1; 					
var sampleTimeMsec = 1000*sampleTimeSec;	

var tempIndex=0;
var pressIndex=0;
var humiIndex=0;
var rollIndex=0;
var pitchIndex=0;
var yawIndex=0;
var xIndex=0;
var yIndex=0;
var zIndex=0;

/**
* @brief Updates the values of imputs in HTML
* @param objectJSON JSON object with the configuration data
*/
function updateConfig(jsonObject){
	tphUrl="http://"+jsonObject.ip+"/tph.json"
	rpyUrl="http://"+jsonObject.ip+"/rpy.json"
	joyUrl="http://"+jsonObject.ip+"/joy.json"
	
	sampleTimeSec=jsonObject.sampleTime;	
	sampleTimeMsec = 1000*sampleTimeSec;
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
			console.log("Ajax success");
			updateConfig(responseJSON);
		},
		error: function (ajaxContext) {
			console.log("Ajax error");
		},
		cache: false
	}).done(function(html){
		generateTable();	
		startTimer();
	});
}

/**
* @brief Code generating dynamic table, the size depends solely on the chosen key
*/
function generateTable() {
        //Build an array containing Customer records.
        var data = new Array();
		//Header
        data.push(["Name", "Value", "Unit"]);
		
		switch(key){
			case "all":{
				data.push(["Temperature", "---", 'C']);		tempIndex=1;
				data.push(["Pressure", "---", 'mbar']);		pressIndex=2;
				data.push(["Humidity", "---", '%']); 		humiIndex=3;
				data.push(["Roll", "---", 'degrees']);		rollIndex=4;
				data.push(["Pitch", "---", 'degrees']);		pitchIndex=5;
				data.push(["Yaw", "---", 'degrees']);		yawIndex=6;
				data.push(["Joystick X", "---", '[-]']);	xIndex=7;
				data.push(["Joystick Y", "---", '[-]']);	yIndex=8;
				data.push(["Joystick Z", "---", '[-]']);	zIndex=9;
				break;
			}
			case "tph":{
				data.push(["Temperature", "---", 'C']);		tempIndex=1;
				data.push(["Pressure", "---", 'mbar']);		pressIndex=2;
				data.push(["Humidity", "---", '%']); 		humiIndex=3;
				break;
			}
			case "rpy":{
				data.push(["Roll", "---", 'degrees']);		rollIndex=1;
				data.push(["Pitch", "---", 'degrees']);		pitchIndex=2;
				data.push(["Yaw", "---", 'degrees']);		yawIndex=3;
				break;
			}
			case "joy":{
				data.push(["Joystick X", "---", '[-]']);	xIndex=1;
				data.push(["Joystick Y", "---", '[-]']);	yIndex=2;
				data.push(["Joystick Z", "---", '[-]']);	zIndex=3;
				break;
			}
			case "tph+rpy":{
				data.push(["Temperature", "---", 'C']);		tempIndex=1;
				data.push(["Pressure", "---", 'mbar']);		pressIndex=2;
				data.push(["Humidity", "---", '%']); 		humiIndex=3;
				data.push(["Roll", "---", 'degrees']);		rollIndex=4;
				data.push(["Pitch", "---", 'degrees']);		pitchIndex=5;
				data.push(["Yaw", "---", 'degrees']);		yawIndex=6;
				break;
			}
			default:{}
		}
 
        //Create a HTML Table element.
        var table = document.createElement("TABLE");
        table.border = "1";
 
        //Get the count of columns.
        var columnCount = data[0].length;
 
        //Add the header row.
        var row = table.insertRow(-1);
        for (var i = 0; i < columnCount; i++) {
            var headerCell = document.createElement("TH");
            headerCell.innerHTML = data[0][i];
            row.appendChild(headerCell);
        }
 
        //Add the data rows.
        for (var i = 1; i < data.length; i++) {
            row = table.insertRow(-1);
            for (var j = 0; j < columnCount; j++) {
                var cell = row.insertCell(-1);
                cell.innerHTML = data[i][j];
            }
        }
 
		debugHelp=table;
		
		table.id="table";
        var divTable = document.getElementById("divTable");
        divTable.innerHTML = "";
        divTable.appendChild(table);
		
}

/**
* @brief Fuction updating the global variable key
*/
function updateKey(){
	key=$("#tableDataSelect").val();
}

/**
* @brief Function that handles the change of table size
*/
function updateTable(){
	updateKey();
	generateTable();
}

/**
* @brief Start request timer
*/
function startTimer(){
	timer = setInterval(sendGetRequest, sampleTimeMsec);
}

/**
* @brief Changes the number in given cell in table 
* @param key Value containing chosen current key
* @param objectJSON JSON object containing data for updating
*/
function changeDataValue(key, objectJSON){
	var table=document.getElementById("table");
	switch(key){
		case "tph":{
			table.rows[tempIndex].cells[1].innerHTML = objectJSON.temp;
			table.rows[pressIndex].cells[1].innerHTML = objectJSON.press;
			table.rows[humiIndex].cells[1].innerHTML = objectJSON.humi;
			break;
		}
		case "rpy":{
			table.rows[rollIndex].cells[1].innerHTML = objectJSON.roll;
			table.rows[pitchIndex].cells[1].innerHTML = objectJSON.pitch;
			table.rows[yawIndex].cells[1].innerHTML = objectJSON.yaw;
			
			break;
		}
		case "joy":{
			table.rows[xIndex].cells[1].innerHTML = objectJSON.x;
			table.rows[yIndex].cells[1].innerHTML = objectJSON.y;
			table.rows[zIndex].cells[1].innerHTML = objectJSON.z;
			
			break;
		}
		default:{}
	}
}

/**
* @brief Send HTTP GET request to IoT server for data depending on the key
*/
function sendGetRequest() {
	switch(key){
		case "all":{
			$.ajax(tphUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("tph", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			$.ajax(rpyUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("rpy", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			$.ajax(joyUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("joy", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			break;
		}
		case "tph":{
			$.ajax(tphUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("tph", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			break;
		}
		case "rpy":{
			$.ajax(rpyUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("rpy", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			break;
		}
		case "joy":{
			$.ajax(joyUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("joy", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			break;
		}
		case "tph+rpy":{
			$.ajax(tphUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("tph", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			$.ajax(rpyUrl, {
				type: 'GET', 
				dataType: 'text',
				crossDomain: true,
				success: function(responseTEXT, status, xhr) {
					var responseJSON = JSON.parse(responseTEXT);
					changeDataValue("rpy", responseJSON);
				},
				error: function (ajaxContext) {
					console.log("Ajax error:"+ajaxContext.responseText);
				},
				cache: false
			});
			break;
		}
		default:{}
	}
}

/**
* @brief Function called when the document loads
*/
$(document).ready(function(){
	$.ajaxSetup({
		cache: false
	});
	baseUrl=window.location.href;
	baseUrl=baseUrl.slice(0, baseUrl.length-13);
	getConfigData();
})
	