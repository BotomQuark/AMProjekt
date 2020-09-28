var sampleTimeSec = 0.1; 					
var sampleTimeMsec = 1000*sampleTimeSec;		

var jData;

var lastTimeStamp; 

var jChartContext;

var jChart;

var timer; 

var url = 'http://192.168.56.101/joy.json';

var baseUrl = '';

var debugHelp;

/**
* @brief Updates the values of imputs in HTML
* @param objectJSON JSON object with the configuration data
*/
function updateConfig(jsonObject){
	url="http://"+jsonObject.ip+"/joy.json"
	
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
		startTimer();
		$("#sampletime").text(sampleTimeMsec.toString());
		$("#zLevel").text('0');
		chartInit();
	});
	
}


/**
* @brief Add new value to next data point.
* @param y New y-axis value 
*/
function updateData(_x, _y, _z){
	
	jData=[{x:_x, y:_y, z:10}];
	jChart.data={
			datasets: [{
				label: 'Joystick Position chart',
				backgroundColor: "rgba(0,0,0,0.2)",
				borderColor: "#000",
				data:jData
			}]
		}
	jChart.update();
	
	$("#zLevel").text(_z.toString());
}

/**
* @brief Start request timer
*/
function startTimer(){
	timer = setInterval(sendGetRequest, sampleTimeMsec);
}


/**
* @brief Send HTTP GET request to IoT server
*/
function sendGetRequest() {
	$.ajax(url, {
		type: 'GET', 
		dataType: 'text',
		crossDomain: true,
		success: function(responseTEXT, status, xhr) {
			var responseJSON = JSON.parse(responseTEXT);
			debugHelp=responseJSON;
			updateData(+responseJSON.x, +responseJSON.y, +responseJSON.z);
			console.log("Ajax success:\nX: "+responseJSON.x+"\nY: "+responseJSON.y+"\nZ: "+responseJSON.z);
		},
		error: function (ajaxContext) {
			console.log("Ajax error:"+ajaxContext);
		},
		cache: false
	});
}

/**
* @brief Chart initialization
*/
function chartInit()
{

	// empty array
	jData = [{x:0,y:0,r:10}];

	// get chart context from 'canvas' element
	jChartContext = $("#jChart")[0].getContext('2d');

	jChart = new Chart(jChartContext, {
		// The type of chart: bubble plot
		type: 'bubble',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			datasets: [{
				label: 'Joystick Position chart',
				backgroundColor: "rgba(0,0,0,0.2)",
				borderColor: "#000",
				data:jData
			}]
		},

		// Configuration options
		options: {
			legend: {
				display: false
			},
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
				yAxes: [{
					scaleLabel: {
						display: true
					},
				ticks: {
                    min: -10,
                    max: 10
                }
				}],
				xAxes: [{
					scaleLabel: {
						display: true
					},
				ticks: {
                    min: -10,
                    max: 10
                }
				}]
			}
		}
	});
	
	
	jData = jChart.data.datasets[0].data;
}

/**
* @brief Function called when the document loads
*/
$(document).ready(() => { 
	$.ajaxSetup({
		cache: false
	});
	baseUrl=window.location.href;
	baseUrl=baseUrl.slice(0, baseUrl.length-12);
	getConfigData();
});