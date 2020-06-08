var sampleTimeSec = 0.1; 					
var sampleTimeMsec = 1000*sampleTimeSec;	
var maxSamplesNumber = 100;			

var xData;

var ryData;
var pyData;
var yyData; 

var lastTimeStamp; 

var rChartContext;
var pChartContext;
var yChartContext;

var rChart; 
var pChart;
var yChart;

var timer; 

var url = 'http://192.168.56.101/rpy.json';

var baseUrl = '';

var debugHelp;

/**
* @brief Updates the values of imputs in HTML
* @param objectJSON JSON object with the configuration data
*/
function updateConfig(jsonObject){
	debugHelp=jsonObject;
	url="http://"+jsonObject.ip+"/rpy.json"
	
	sampleTimeSec=jsonObject.sampleTime;	
	sampleTimeMsec=1000*sampleTimeSec;
	maxSamplesNumber=jsonObject.sampleQuantity;
	$("#sampletime").text(sampleTimeMsec.toString());
	$("#samplenumber").text(maxSamplesNumber.toString());
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
			console.log("Ajax Config Request success");
			updateConfig(responseJSON);
		},
		error: function (ajaxContext) {
			console.log("Ajax Config Request error");
		},
		cache: false
	});
}

/**
* @brief Add new value to next data point.
* @param y New y-axis value 
*/
function addData(r, p, y){
	
	if(r>180) r=r-360;
	if(p>180) p=p-360;
	if(y>180) y=y-360;
	if(ryData.length > maxSamplesNumber)
	{
		xData.splice(0,1);
		
		ryData.splice(0,1);
		pyData.splice(0,1);
		yyData.splice(0,1);
		
		lastTimeStamp += sampleTimeSec;
		
		xData.push(lastTimeStamp.toFixed(4));
	}
	
	ryData.push(r);
	pyData.push(p);
	yyData.push(y);
	
	rChart.update();
	pChart.update();
	yChart.update();

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
			addData(+responseJSON.roll, +responseJSON.pitch, +responseJSON.yaw);
			console.log("Ajax success:\nRoll: "+responseJSON.roll+"\nPitch: "+responseJSON.pitch+"\nYaw: "+responseJSON.yaw);
		},
		error: function (ajaxContext) {
			console.log("Ajax Data Request Error:"+ajaxContext.responseText);
		},
		cache: false
	});
}

/**
* @brief Chart initialization
*/
function chartInit()
{
	// array with consecutive integers: <0, maxSamplesNumber-1>
	xData = [...Array(maxSamplesNumber).keys()];  
	// scaling all values ​​times the sample time 
	xData.forEach(function(p, i) {this[i] = (this[i]*sampleTimeSec).toFixed(4);}, xData);

	// last value of 'xdata'
	lastTimeStamp = +xData[xData.length-1];

	// empty array
	ryData = []; 
	pyData = []; 
	yyData = []; 

	// get chart context from 'canvas' element
	rChartContext = $("#rChart")[0].getContext('2d');
	pChartContext = $("#pChart")[0].getContext('2d');
	yChartContext = $("#yChart")[0].getContext('2d');

	rChart = new Chart(rChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			labels: xData,
			datasets: [{
				fill: false,
				label: 'Roll Chart',
				backgroundColor: 'rgb(0, 0, 255)',
				borderColor: 'rgb(0, 0, 255)',
				data: ryData,
				lineTension: 0
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
						display: true,
						labelString: '[degrees]'
					},
				ticks: {
                    suggestedMin: -180,
                    suggestedMax: 180
                }
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});
	
	pChart = new Chart(pChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			labels: xData,
			datasets: [{
				fill: false,
				label: 'Pitch Chart',
				backgroundColor: 'rgb(0, 0, 255)',
				borderColor: 'rgb(0, 0, 255)',
				data: pyData,
				lineTension: 0
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
						display: true,
						labelString: '[degrees]'
					},
				ticks: {
                    suggestedMin: 180,
                    suggestedMax: -180
                }
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});
	
	yChart = new Chart(yChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			labels: xData,
			datasets: [{
				fill: false,
				label: 'Yaw Chart',
				backgroundColor: 'rgb(0, 0, 255)',
				borderColor: 'rgb(0, 0, 255)',
				data: yyData,
				lineTension: 0
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
						display: true,
						labelString: '[degrees]'
					},
				ticks: {
                    suggestedMin: -180,
                    suggestedMax: 180
                }
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});
	
	ryData = rChart.data.datasets[0].data;
	xData = rChart.data.labels;
	pyData = pChart.data.datasets[0].data;
	xData = pChart.data.labels;
	yyData = yChart.data.datasets[0].data;
	xData = yChart.data.labels;
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
	startTimer();
	chartInit();
});