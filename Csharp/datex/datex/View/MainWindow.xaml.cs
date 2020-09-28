using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace datex.View
{
    /** 
     * @brief Interaction logic for MainWindow.xaml 
     */

    public partial class MainWindow : Window
    {
        bool isMenuVisible = true;
        private double R;
        private double G;
        private double B;



        public MainWindow()
        {
            InitializeComponent();
        }

        private void MenuBtn_Click(object sender, RoutedEventArgs e)
        {
            isMenuVisible = !isMenuVisible;

            if (isMenuVisible)
                this.Menu.Visibility = Visibility.Visible;
            else
                this.Menu.Visibility = Visibility.Collapsed;

        }


        private void LEDBtn_Click(object sender, RoutedEventArgs e)
        {

            this.DataPlotView_temp.Visibility = Visibility.Collapsed;
            this.DataPlotView_press.Visibility = Visibility.Collapsed;
            this.DataPlotView_humi.Visibility = Visibility.Collapsed;
            this.Data_table.Visibility = Visibility.Collapsed;

            if (this.LED1.Visibility == Visibility.Visible && this.LED2.Visibility == Visibility.Visible)
            {
                this.LED1.Visibility = Visibility.Collapsed;
                this.LED2.Visibility = Visibility.Collapsed;
            }
            else
            {
                this.LED1.Visibility = Visibility.Visible;
                this.LED2.Visibility = Visibility.Visible;
            }

        }

        private void CHARTSBtn_Click(object sender, RoutedEventArgs e)
        {

            this.LED1.Visibility = Visibility.Collapsed;
            this.LED2.Visibility = Visibility.Collapsed;
            this.Data_table.Visibility = Visibility.Collapsed;
            this.RPYPlotView_roll.Visibility = Visibility.Collapsed;
            this.RPYPlotView_pitch.Visibility = Visibility.Collapsed;
            this.RPYPlotView_yaw.Visibility = Visibility.Collapsed;
            this.JOYPlotView.Visibility = Visibility.Collapsed;



            if (this.DataPlotView_temp.Visibility == Visibility.Visible && this.DataPlotView_press.Visibility == Visibility.Visible && this.DataPlotView_humi.Visibility == Visibility.Visible)
            {
                this.DataPlotView_temp.Visibility = Visibility.Collapsed;
                this.DataPlotView_press.Visibility = Visibility.Collapsed;
                this.DataPlotView_humi.Visibility = Visibility.Collapsed;

            }

            else
            {
                this.DataPlotView_temp.Visibility = Visibility.Visible;
                this.DataPlotView_press.Visibility = Visibility.Visible;
                this.DataPlotView_humi.Visibility = Visibility.Visible;
            }
        }



        private void RPYBtn_Click(object sender, RoutedEventArgs e)
        {

            this.LED1.Visibility = Visibility.Collapsed;
            this.LED2.Visibility = Visibility.Collapsed;
            this.DataPlotView_temp.Visibility = Visibility.Collapsed;
            this.DataPlotView_press.Visibility = Visibility.Collapsed;
            this.DataPlotView_humi.Visibility = Visibility.Collapsed;
            this.Data_table.Visibility = Visibility.Collapsed;
            this.JOYPlotView.Visibility = Visibility.Collapsed;


            if (this.RPYPlotView_roll.Visibility == Visibility.Visible && this.RPYPlotView_pitch.Visibility == Visibility.Visible && this.RPYPlotView_yaw.Visibility == Visibility.Visible)
            {
                this.RPYPlotView_roll.Visibility = Visibility.Collapsed;
                this.RPYPlotView_pitch.Visibility = Visibility.Collapsed;
                this.RPYPlotView_yaw.Visibility = Visibility.Collapsed;
            }
            else
            {
                this.RPYPlotView_roll.Visibility = Visibility.Visible;
                this.RPYPlotView_pitch.Visibility = Visibility.Visible;
                this.RPYPlotView_yaw.Visibility = Visibility.Visible;
            }
        }



        private void JOYBtn_Click(object sender, RoutedEventArgs e)
        {

            this.LED1.Visibility = Visibility.Collapsed;
            this.LED2.Visibility = Visibility.Collapsed;
            this.DataPlotView_temp.Visibility = Visibility.Collapsed;
            this.DataPlotView_press.Visibility = Visibility.Collapsed;
            this.DataPlotView_humi.Visibility = Visibility.Collapsed;
            this.RPYPlotView_roll.Visibility = Visibility.Collapsed;
            this.RPYPlotView_pitch.Visibility = Visibility.Collapsed;
            this.RPYPlotView_yaw.Visibility = Visibility.Collapsed;




            if (this.JOYPlotView.Visibility == Visibility.Visible)
            {

                this.JOYPlotView.Visibility = Visibility.Collapsed;
            }
            else
            {
                this.JOYPlotView.Visibility = Visibility.Visible;
            }
        }

        private void TablesBtn_Click(object sender, RoutedEventArgs e)
        {
            this.DataPlotView_temp.Visibility = Visibility.Collapsed;
            this.DataPlotView_press.Visibility = Visibility.Collapsed;
            this.DataPlotView_humi.Visibility = Visibility.Collapsed;
            this.LED1.Visibility = Visibility.Collapsed;
            this.LED2.Visibility = Visibility.Collapsed;
            this.RPYPlotView_roll.Visibility = Visibility.Collapsed;
            this.RPYPlotView_pitch.Visibility = Visibility.Collapsed;
            this.RPYPlotView_yaw.Visibility = Visibility.Collapsed;
            this.JOYPlotView.Visibility = Visibility.Collapsed;

            if (this.Data_table.Visibility == Visibility.Visible)
            {
                this.Data_table.Visibility = Visibility.Collapsed;
            }
            else
            {
                this.Data_table.Visibility = Visibility.Visible;
            }
        }


        private LinearGradientBrush Set_color(double R, double G, double B)
        {
            Byte Rbyte = Convert.ToByte(R);
            Byte Gbyte = Convert.ToByte(G);
            Byte Bbyte = Convert.ToByte(B);
            Color color_temp = Color.FromArgb(255, Rbyte, Gbyte, Bbyte);

            LinearGradientBrush color = new LinearGradientBrush();
            color.GradientStops.Add(new GradientStop(color_temp, 1.0));

            return color;

        }
        private void Slider1_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            R = Slider1.Value;
            G = Slider2.Value;
            B = Slider3.Value;
            LinearGradientBrush color = Set_color(R, G, B);
            TextBox1.Background = color;
            int R_i = (int)R;

            R_txt.Text = R_i.ToString();
        }

        private void Slider2_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            R = Slider1.Value;
            G = Slider2.Value;
            B = Slider3.Value;
            LinearGradientBrush color = Set_color(R, G, B);
            TextBox1.Background = color;
            int G_i = (int)G;
            G_txt.Text = G_i.ToString();

        }
        private void Slider3_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {

            R = Slider1.Value;
            G = Slider2.Value;
            B = Slider3.Value;
            LinearGradientBrush color = Set_color(R, G, B);
            TextBox1.Background = color;
            int B_i = (int)B;
            B_txt.Text = B_i.ToString();

        }

        private void TPHBtn_Click(object sender, RoutedEventArgs e)
        {

        }
    }
}