var red = "0";
var green = "0";
var blue = "0";
var color = "gray";
var baseUrl = '';
var url = 'http://192.168.56.101/setled.php';

/**
* @brief Updates the values of imputs in HTML
* @param objectJSON JSON object with the configuration data
*/
function updateConfig(jsonObject){
	url="http://"+jsonObject.ip+"/setled.php"
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
	});
}


/**
* @brief Send HTTP GET request to IoT server
*/
function sendGetRequest() {
	$.ajax(url, {
		type: 'POST', 
		dataType: 'text',
		crossDomain: true,
		success: function(responseTEXT, status, xhr) {
			var responseJSON = JSON.parse(responseTEXT);
			addData(+responseJSON.temperature, +responseJSON.pressure, +responseJSON.humidity);
		},
		cache: false
	});
}

/**
* @brief Generates the string code in POST form, and then sends POST request to setled.php
*/
function clearColors(){
	var ledId;
	var element;
	var postRequestText = "";
	for(var i=0; i<8; i++){
		for(var j=0; j<8; j++){
			ledId="led"+i+j;
			element = document.getElementById(ledId);
			
			element.style.backgroundColor='rgb(0, 0, 0)';
			
			postRequestText = postRequestText + 'LED'+i+j+'=['+i+','+j+',0,0,0]&&';
		}
	}
	postRequestText=postRequestText.substring(0,postRequestText.length-2);
	debugHelpVariable=postRequestText;
	$.ajax(url, {
			type: "POST",
			data: postRequestText,
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
		
}

/**
* @brief translates the position and rgb code to array
* @param x X-position of an element in array
* @param y Y-position of an element in array
* @param colorString string containing colour of the cell in "rgb(red, green, blue)" format
* @return String in "[x,y,r,g,b] format
*/
function getJSONArrayElementInString(x, y, colorString){
	//Input Format: "rgb(red, green, blue)"
	//Output Format: "[x,y,r,g,b]"
	var _red;
	var _green;
	var _blue;
	
	
	var _colorString=colorString.substring(4, colorString.length-1);
	_colorString=_colorString.replace(' ','');
	_colorString=_colorString.replace(' ','');
	
	_red=parseInt(_colorString.substring(0, _colorString.indexOf(',')));
	
	_colorString=_colorString.substring(_colorString.indexOf(',')+1, colorString.length-1);
	_green=parseInt(_colorString.substring(0, _colorString.indexOf(',')));
	
	_colorString=_colorString.substring(_colorString.indexOf(',')+1 , colorString.length-1);
	_blue=parseInt(_colorString);
	
	return '['+x+','+y+','+_red+','+_green+','+_blue+']';
}


/**
* @brief Creates string of data needed for POST request
* @return stringData String containing the data in "LEDxy=[x,y,r,g,b]&&..." format or null if no cells are colored
*/
function getStringData(){
	var stringData='';
	var element;
	var color;
	var JSONArrayElement;
	for(var i=0; i<8; i++){
		for(var j=0; j<8; j++){
			element = document.getElementById("led"+i+j);
			color=element.style.backgroundColor;
			if(color.length!=0){
				stringData=stringData+"LED"+i+j+'=';
				JSONArrayElement=getJSONArrayElementInString(i, j, color);	
				
				stringData=stringData.concat(JSONArrayElement, '&&');
			}
		}
	}
	if(stringData.length>1){
		stringData=stringData.substring(0,stringData.length-2);
		return stringData;
	}
	else return null;
}

/**
* @brief Sends POST request to setled.php in order to change colors of the led
*/
function sendRequest(){
	var postRequestText = getStringData();
	
	if(postRequestText!=null){
		$.ajax(url, {
			type: "POST",
			data: postRequestText,
			dataType: "text",
			crossDomain: true,
			beforeSend: function(x) {
				console.log("AJAX POST REQUEST: BEGIN SENDING");
			},
			success: function(result) {
				console.log("AJAX POST REQUEST: SUCCESFULL");
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("AJAX POST REQUEST: FAILURE");
				console.log("STATUS: " + textStatus);
				console.log("ERROR: " + errorThrown);
			},
			cache: false
		});     
		
	}
}

/**
* @brief Converts 3 variabled r g and b into rbg string
* @param r Red color variable in 0-255
* @param g Green color variable in 0-255
* @param b Blue color variable in 0-255
* @return rgbColor rgb code in ##RRGGBB format
*/
function intToStringColorConverter(r, g, b){
	var redString = Number(r).toString(16);
	var greenString = Number(g).toString(16);
	var blueString = Number(b).toString(16);
	if (redString.length < 2) {
		redString = "0" + redString;
	}
	if (greenString.length < 2) {
		greenString = "0" + greenString;
	}
	if (blueString.length < 2) {
		blueString = "0" + blueString;
	}
	return rgbColor = "#"+redString+greenString+blueString;
}


/**
* @brief Updated the preview of a chosen color
*/
function updatePreviewColor(){
	if((red=="0")&&(green=="0")&&(blue=="0")){
		colour="#000000";
	}else {
		color = intToStringColorConverter(red, green, blue);
	}
	var element = document.getElementById("colorPreview");
	element.style.backgroundColor='rgb('+red+', '+green+', '+blue+')';
}

/**
* @brief Updates the given color depending on which slider has been moved
* @param key Key of a given slider
*/
function updateColor(key){
	switch(key){
		case 'r':{
			red=$("#redSlider").val();
			break;
		}
		case 'g':{
			green=$("#greenSlider").val();
			break;
		}
		case 'b':{
			blue=$("#blueSlider").val();
			break;
		}
		default: {}
	}
	updatePreviewColor();
}

/**
* @brief Code handling the logic of clicking on one of the cells
* @param ledIdAdress
*/
function tableOnClickHandler(ledIdAdress){
	var _element = document.getElementById(ledIdAdress);
	_element.style.backgroundColor='rgb('+red+', '+green+', '+blue+')';
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
});