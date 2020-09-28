#define CLIENT
#define GET
#define DYNAMIC

using System;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using System.Timers;
using System.Net.Http;
using OxyPlot;
using OxyPlot.Axes;
using OxyPlot.Series;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace datex.ViewModel
{

    using datex.ViewModel;
    using Model;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using System.Windows.Media;


    /** 
      * @brief View model for MainWindow.xaml 
      */
    public class MainViewModel : INotifyPropertyChanged
    {
        #region Properties
        private string ipAddress;

        public ObservableCollection<TablesViewModel> Data_tables { get; set; }

        /**
        * @brief Set gradient method.
        * @param R , G, B color data.
        */

        public string IpAddress
        {
            get
            {
                return ipAddress;
            }
            set
            {
                if (ipAddress != value)
                {
                    ipAddress = value;
                    OnPropertyChanged("IpAddress");
                }
            }
        }
        private int sampleTime;
        public string SampleTime
        {
            get
            {
                return sampleTime.ToString();
            }
            set
            {
                if (Int32.TryParse(value, out int st))
                {
                    if (sampleTime != st)
                    {
                        sampleTime = st;
                        OnPropertyChanged("SampleTime");
                    }
                }
            }
        }
        private string x = "0";
        public string X
        {
            get
            {
                return x;
            }
            set
            {
                if (x != value)
                {
                    x = value;
                    OnPropertyChanged("X");
                }
            }
        }
        private string y = "0";
        public string Y
        {
            get
            {
                return y;
            }
            set
            {
                if (y != value)
                {
                    y = value;
                    OnPropertyChanged("Y");
                }
            }
        }

        private string r = "0";
        public string R
        {
            get
            {
                return r;
            }
            set
            {
                if (r != value)
                {
                    r = value;
                    OnPropertyChanged("R");
                }
            }
        }
        private string g = "0";
        public string G
        {
            get
            {
                return g;
            }
            set
            {
                if (g != value)
                {
                    g = value;
                    OnPropertyChanged("G");
                }
            }
        }
        private string b = "0";
        public string B
        {
            get
            {
                return b;
            }
            set
            {
                if (b != value)
                {
                    b = value;
                    OnPropertyChanged("B");
                }
            }
        }


        //public string _path = $"..\\config.json";
        public string _path = $"D:\\Studia\\6semestr\\AMlab\\Nowy folder\\datex_c#_2709\\config.json";

        public class Configjson
        {
            public string ip_conf { get; set; }
            public int sample_conf { get; set; }
        }


        public PlotModel DataPlotModel { get; set; }
        public PlotModel DataPlotModel2 { get; set; }
        public PlotModel DataPlotModel3 { get; set; }

        public PlotModel RPYPlotModel { get; set; }
        public PlotModel RPYPlotModel2 { get; set; }
        public PlotModel RPYPlotModel3 { get; set; }

        public PlotModel JOYPlotModel { get; set; }


        public ButtonCommand LED_SEND { get; set; }
        public ButtonCommand LED_CLEAR { get; set; }
        public ButtonCommand StartButton { get; set; }
        public ButtonCommand StopButton { get; set; }
        public ButtonCommand UpdateConfigButton { get; set; }
        public ButtonCommand DefaultConfigButton { get; set; }
        #endregion

        #region Fields
        private int timeStamp = 0;
        private ConfigParams config = new ConfigParams();
        private Timer RequestTimer;
        private IoTServer Server;
        #endregion


        public MainViewModel()
        {
            Data_tables = new ObservableCollection<TablesViewModel>();


            RPYPlotModel = new PlotModel { Title = "Roll" };

            RPYPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = config.XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            RPYPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "deg"
            });

            RPYPlotModel2 = new PlotModel { Title = "Pitch" };

            RPYPlotModel2.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = config.XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            RPYPlotModel2.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "deg",
            });

            RPYPlotModel3 = new PlotModel { Title = "Yaw" };

            RPYPlotModel3.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = config.XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            RPYPlotModel3.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "deg",
            });

            DataPlotModel = new PlotModel { Title = "Temperature" };

            DataPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = config.XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            DataPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "deg C",
            });

            DataPlotModel2 = new PlotModel { Title = "Pressure" };

            DataPlotModel2.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = config.XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            DataPlotModel2.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "mbar",
            });

            DataPlotModel3 = new PlotModel { Title = "Humidity" };

            DataPlotModel3.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = config.XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            DataPlotModel3.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "%",
            });


            JOYPlotModel = new PlotModel { Title = "Joystick" };

            JOYPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                MajorGridlineStyle = OxyPlot.LineStyle.Solid,
                MinorGridlineStyle = OxyPlot.LineStyle.Solid,
                // Minimum = 0,
                //Maximum = config.XAxisMax,
                // PositionAtZeroCrossing = true,
                Key = "Horizontal",
                Title = "X"
            });
            JOYPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                MajorGridlineStyle = OxyPlot.LineStyle.Solid,
                MinorGridlineStyle = OxyPlot.LineStyle.Solid,
                //PositionAtZeroCrossing = true,
                Key = "Vertical",
                Title = "Y"
            });



            DataPlotModel.Series.Add(new LineSeries() {Color = OxyColor.Parse("255,0,0") });
            DataPlotModel2.Series.Add(new LineSeries() {Color = OxyColor.Parse("0,255,0") });
            DataPlotModel3.Series.Add(new LineSeries() {Color = OxyColor.Parse("0,0,255") });


            RPYPlotModel.Series.Add(new LineSeries() {Color = OxyColor.Parse("255,0,0")});
            RPYPlotModel2.Series.Add(new LineSeries() {Color = OxyColor.Parse("0,255,0")});
            RPYPlotModel3.Series.Add(new LineSeries() {Color = OxyColor.Parse("0,0,255")}); 
            JOYPlotModel.Series.Add(new LineSeries() { 
                Color = OxyColor.Parse("#FFFF0000"), 
                MarkerType = OxyPlot.MarkerType.Circle, 
                MarkerSize = 4 
            });


            StartButton = new ButtonCommand(StartTimer);
            StopButton = new ButtonCommand(StopTimer);
            LED_SEND = new ButtonCommand(Send_Led_Data);
            LED_CLEAR= new ButtonCommand(Send_Led_Clear);
            UpdateConfigButton = new ButtonCommand(UpdateConfig);
            DefaultConfigButton = new ButtonCommand(DefaultConfig);



            ipAddress = config.IpAddress;
            sampleTime = config.SampleTime;

            Server = new IoTServer(IpAddress);
        }

        /**
          * @brief Time series plot update procedure.
          * @param t X axis data: Time stamp [ms].
          * @param d Y axis data: Real-time measurement [-].
          */
        private void UpdatePlot(double t, double d)
        {
            LineSeries lineSeries = DataPlotModel.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > config.MaxSampleNumber)
                lineSeries.Points.RemoveAt(0);

            if (t >= config.XAxisMax)
            {
                DataPlotModel.Axes[0].Minimum = (t - config.XAxisMax);
                DataPlotModel.Axes[0].Maximum = t + config.SampleTime / 1000.0; ;
            }

            DataPlotModel.InvalidatePlot(true);
        }

        private void UpdatePlot2(double t, double d)
        {
            LineSeries lineSeries = DataPlotModel2.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > config.MaxSampleNumber)
                lineSeries.Points.RemoveAt(0);

            if (t >= config.XAxisMax)
            {
                DataPlotModel2.Axes[0].Minimum = (t - config.XAxisMax);
                DataPlotModel2.Axes[0].Maximum = t + config.SampleTime / 1000.0; ;
            }

            DataPlotModel2.InvalidatePlot(true);
        }

        private void UpdatePlot3(double t, double d)
        {
            LineSeries lineSeries = DataPlotModel3.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > config.MaxSampleNumber)
                lineSeries.Points.RemoveAt(0);

            if (t >= config.XAxisMax)
            {
                DataPlotModel3.Axes[0].Minimum = (t - config.XAxisMax);
                DataPlotModel3.Axes[0].Maximum = t + config.SampleTime / 1000.0; ;
            }

            DataPlotModel3.InvalidatePlot(true);
        }

        private void UpdatePlot4(double t, double d)
        {
            LineSeries lineSeries = RPYPlotModel.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > config.MaxSampleNumber)
                lineSeries.Points.RemoveAt(0);

            if (t >= config.XAxisMax)
            {
                RPYPlotModel.Axes[0].Minimum = (t - config.XAxisMax);
                RPYPlotModel.Axes[0].Maximum = t + config.SampleTime / 1000.0; ;
            }

            RPYPlotModel.InvalidatePlot(true);
        }

        private void UpdatePlot5(double t, double d)
        {
            LineSeries lineSeries = RPYPlotModel2.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > config.MaxSampleNumber)
                lineSeries.Points.RemoveAt(0);

            if (t >= config.XAxisMax)
            {
                RPYPlotModel2.Axes[0].Minimum = (t - config.XAxisMax);
                RPYPlotModel2.Axes[0].Maximum = t + config.SampleTime / 1000.0; ;
            }

            RPYPlotModel2.InvalidatePlot(true);
        }

        private void UpdatePlot6(double t, double d)
        {
            LineSeries lineSeries = RPYPlotModel3.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > config.MaxSampleNumber)
                lineSeries.Points.RemoveAt(0);

            if (t >= config.XAxisMax)
            {
                RPYPlotModel3.Axes[0].Minimum = (t - config.XAxisMax);
                RPYPlotModel3.Axes[0].Maximum = t + config.SampleTime / 1000.0; ;
            }

            RPYPlotModel3.InvalidatePlot(true);
        }

        private void UpdatePlotJoy(double horizontal, double vertical)
        {
            LineSeries lineSeries = JOYPlotModel.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(horizontal, vertical));

            if (lineSeries.Points.Count > 1)
            lineSeries.Points.RemoveAt(0);


            JOYPlotModel.InvalidatePlot(true);
        }



        /**
          * @brief Asynchronous led update procedure with single LED address
          //* @param X, Y - position of LED, R,G,B - color set
          */
        private async void Send_Led_Data()
        {
            string R_s = R.ToString();
            string G_s = G.ToString();
            string B_s = B.ToString();
            await Server.POSTwithClient_send_led(X, Y, R_s, G_s, B_s);
        }

        private async void Send_Led_Clear()
        {

            int iteration = 0;
            string[] led_val_string = new string[64];
            string[] x_val_string = new string[64];
            string[] y_val_string = new string[64];
            string[] rgb_clear_val_string = new string[64];

            for (int i = 0; i < 8; i++)
            {
                for (int u = 0; u < 8; u++)
                {
                    led_val_string[iteration] = i.ToString() + u.ToString();
                    x_val_string[iteration] = i.ToString();
                    y_val_string[iteration] = u.ToString();
                    rgb_clear_val_string[iteration] = "0";
                    iteration++;
                }
            }
            await Server.POSTwithClient_send_led_clear(led_val_string, x_val_string, y_val_string, rgb_clear_val_string, rgb_clear_val_string, rgb_clear_val_string);

        }


        /**
          * @brief Asynchronous chart update procedure with
          *        data obtained from IoT server responses.
          * @param ip IoT server IP address.
          */
        private async void UpdatePlotWithServerResponse()
        {
#if CLIENT
#if GET
            string responseText = await Server.GETwithClient();
            string responseText_Array = await Server.GETwithClient_Array();
#else
            string responseText = await Server.POSTwithClient();
#endif
#else
#if GET
            string responseText = await Server.GETwithRequest();
#else
            string responseText = await Server.POSTwithRequest();
#endif
#endif
            try
            {

                App.Current.Dispatcher.Invoke((System.Action)delegate
                {
                    if (responseText_Array != null)
                    {
                        JArray DataJsonArray = JArray.Parse(responseText_Array);

                        var Data_Tables_List = DataJsonArray.ToObject<List<TablesModel>>();
                        Data_tables.Clear();

                        if (Data_tables.Count < Data_Tables_List.Count)
                        {
                            foreach (var d in Data_Tables_List)
                            {
                                Data_tables.Add(new TablesViewModel(d));
                            }
                        }
                    }
                });

#if DYNAMIC
                dynamic resposneJson = JObject.Parse(responseText);
                UpdatePlot(timeStamp / 1000.0, (double)resposneJson.temp);
                UpdatePlot2(timeStamp / 1000.0, (double)resposneJson.press);
                UpdatePlot3(timeStamp / 1000.0, (double)resposneJson.humi);

                UpdatePlot4(timeStamp / 1000.0, (double)resposneJson.roll);
                UpdatePlot5(timeStamp / 1000.0, (double)resposneJson.pitch);
                UpdatePlot6(timeStamp / 1000.0, (double)resposneJson.yaw);

                UpdatePlotJoy((double)resposneJson.x, (double)resposneJson.y);

#else
                ServerData resposneJson = JsonConvert.DeserializeObject<ServerData>(responseText);
                UpdatePlot(timeStamp / 1000.0, resposneJson.data);
#endif
            }
            catch (Exception e)
            {
                Debug.WriteLine("JSON DATA ERROR");
                Debug.WriteLine(responseText);
                Debug.WriteLine(e);
            }

            timeStamp += config.SampleTime;
        }

        /**
          * @brief Synchronous procedure for request queries to the IoT server.
          * @param sender Source of the event: RequestTimer.
          * @param e An System.Timers.ElapsedEventArgs object that contains the event data.
          */
        private void RequestTimerElapsed(object sender, ElapsedEventArgs e)
        {
            UpdatePlotWithServerResponse();
        }

        #region ButtonCommands

        /**
         * @brief RequestTimer start procedure.
         */
        private void StartTimer()
        {
            if (RequestTimer == null)
            {
                RequestTimer = new Timer(config.SampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;

                DataPlotModel.ResetAllAxes();
                DataPlotModel2.ResetAllAxes();
                DataPlotModel3.ResetAllAxes();

                RPYPlotModel.ResetAllAxes();
                RPYPlotModel2.ResetAllAxes();
                RPYPlotModel3.ResetAllAxes();
            }
        }

        /**
         * @brief RequestTimer stop procedure.
         */
        private void StopTimer()
        {
            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
            }
        }

        /**
         * @brief Configuration parameters update
         */
        private void UpdateConfig()
        {
            bool restartTimer = (RequestTimer != null);

            if (restartTimer)
                StopTimer();


            Configjson configjson = new Configjson { ip_conf = ipAddress, sample_conf = sampleTime };
            var result = JsonConvert.SerializeObject(configjson, Formatting.Indented);
            using (var writer = new StreamWriter(_path))
            {
                writer.Write(result);
            }

            config = new ConfigParams(_path);

            Server = new IoTServer(IpAddress);

            if (restartTimer)
                StartTimer();
        }

        /**
          * @brief Configuration parameters defualt values
          */
        private void DefaultConfig()
        {
            bool restartTimer = (RequestTimer != null);

            if (restartTimer)
                StopTimer();

            config = new ConfigParams();
            IpAddress = config.IpAddress;
            SampleTime = config.SampleTime.ToString();
            Server = new IoTServer(IpAddress);

            if (restartTimer)
                StartTimer();
        }

        #endregion

        #region PropertyChanged

        public event PropertyChangedEventHandler PropertyChanged;

        /**
         * @brief Simple function to trigger event handler
         * @params propertyName Name of ViewModel property as string
         */
        protected void OnPropertyChanged(string propertyName)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null) handler(this, new PropertyChangedEventArgs(propertyName));
        }

        #endregion
    }
}
