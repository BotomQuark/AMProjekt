var sampleTimeSec = 0.5; 					
var sampleTimeMsec = 1000*sampleTimeSec;	
var maxSamplesNumber = 100;			

var xData;

var tyData;
var pyData;
var hyData; 

var lastTimeStamp; 

var tChartContext;
var pChartContext;
var hChartContext;

var tChart; 
var pChart;
var hChart;

var timer; 

var url = 'http://192.168.56.101/tph.json';

var baseUrl = '';

/**
* @brief Updates the values of imputs in HTML
* @param objectJSON JSON object with the configuration data
*/
function updateConfig(jsonObject){
	debugHelp=jsonObject;
	url="http://"+jsonObject.ip+"/tph.json"
	
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
			
			console.log("Ajax Config Request success\nip="+responseJSON.ip+
			"\nport="+responseJSON.port+
			"\nsample time="+responseJSON.sampleTime+
			"\nsample quantity="+responseJSON.sampleQuantity);
			
			updateConfig(responseJSON);
		},
		error: function (ajaxContext) {
			console.log("Ajax Config Request error");
		},
		cache: false
	}).done(function(html){
		chartInit();
		startTimer();
	});
}

/**
* @brief Add new value to next data point.
* @param y New y-axis value 
*/
function addData(t, p, h){
	if(tyData.length > maxSamplesNumber)
	{
		xData.splice(0,1);
		
		tyData.splice(0,1);
		pyData.splice(0,1);
		hyData.splice(0,1);
		
		lastTimeStamp += sampleTimeSec;
		
		xData.push(lastTimeStamp.toFixed(4));
	}
	
	tyData.push(t);
	pyData.push(p);
	hyData.push(h);
	
	tChart.update();
	pChart.update();
	hChart.update();
	

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
			addData(+responseJSON.temp, +responseJSON.press, +responseJSON.humi);
			console.log("Ajax success:\nTemp: "+responseJSON.temp+"\nPress: "+responseJSON.press+"\nHumi: "+responseJSON.humi);
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
	tyData = []; 
	pyData = []; 
	hyData = []; 

	// get chart context from 'canvas' element
	tChartContext = $("#tChart")[0].getContext('2d');
	pChartContext = $("#pChart")[0].getContext('2d');
	hChartContext = $("#hChart")[0].getContext('2d');

	tChart = new Chart(tChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			labels: xData,
			datasets: [{
				fill: false,
				label: 'Temperature Chart',
				backgroundColor: 'rgb(0, 0, 255)',
				borderColor: 'rgb(0, 0, 255)',
				data: tyData,
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
						labelString: 'Temperature[C]'
					},
				ticks: {
                    suggestedMin: -30,
                    suggestedMax: 105
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
				label: 'Pressure Chart',
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
						labelString: 'Pressure[mbar]'
					},
				ticks: {
                    suggestedMin: 260,
                    suggestedMax: 1260
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
	
	hChart = new Chart(hChartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			labels: xData,
			datasets: [{
				fill: false,
				label: 'Humidity Chart',
				backgroundColor: 'rgb(0, 0, 255)',
				borderColor: 'rgb(0, 0, 255)',
				data: hyData,
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
						labelString: 'Humidity[%]'
					},
				ticks: {
                    suggestedMin: 0,
                    suggestedMax: 100
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
	
	tyData = tChart.data.datasets[0].data;
	xData = tChart.data.labels;
	pyData = pChart.data.datasets[0].data;
	xData = pChart.data.labels;
	hyData = hChart.data.datasets[0].data;
	xData = hChart.data.labels;
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