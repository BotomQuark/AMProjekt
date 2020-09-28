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
using static datex.ViewModel.MainViewModel;

namespace datex.Model
{
    public class ConfigParams
    {
        static readonly string ipAddressDefault = "10.10.0.125";
        public string IpAddress;
        static readonly int sampleTimeDefault = 500;

        public int SampleTime;
        public readonly int MaxSampleNumber = 100;
        public double XAxisMax
        {
            get
            {
                return MaxSampleNumber * SampleTime / 1000.0;
            }
            private set { }
        }

        public ConfigParams()
        {
            IpAddress = ipAddressDefault;
            SampleTime = sampleTimeDefault;
        }

        public ConfigParams(string path)
        {
            string jsonFromFile;
            using (var reader = new StreamReader(path))
            {
                jsonFromFile = reader.ReadToEnd();
            }
            var configFromJson = JsonConvert.DeserializeObject<Configjson>(jsonFromFile);

            IpAddress = configFromJson.ip_conf;
            SampleTime = configFromJson.sample_conf;

        }
    }
}
