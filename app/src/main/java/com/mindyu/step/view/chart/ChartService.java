package com.mindyu.step.view.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartService {

    private GraphicalView mGraphicalView;
    private XYMultipleSeriesDataset multipleSeriesDataset;      // 数据集容器
    private XYMultipleSeriesRenderer multipleSeriesRenderer;    // 渲染器容器
    private TimeSeries mSeries;             // 单条曲线数据集
    private XYSeriesRenderer mRenderer;     // 单条曲线渲染器
    private Context context;

    public ChartService(Context context) {
        this.context = context;
    }

    /**
     * 获取图表
     *
     * @return
     */
    public GraphicalView getGraphicalView() {
        mGraphicalView = ChartFactory.getLineChartView(context,
                multipleSeriesDataset, multipleSeriesRenderer);
        return mGraphicalView;
    }

    /**
     * 获取数据集，及xy坐标的集合
     *
     * @param curveTitle
     */
    public void setXYMultipleSeriesDataset(String curveTitle) {
        multipleSeriesDataset = new XYMultipleSeriesDataset();   //创建图表数据集
        mSeries = new TimeSeries(curveTitle);                    //带日期的单条曲线数据
        multipleSeriesDataset.addSeries(mSeries);                //将单条曲线数据设置给曲线数据集
    }

    /**
     * 获取渲染器
     *
     * @param xTitle     x轴标题
     * @param yTitle     y轴标题
     * @param axeColor   坐标轴颜色
     * @param curveColor 曲线颜色
     */
    public void setXYMultipleSeriesRenderer(String chartTitle, String xTitle, String yTitle, int axeColor,
                                            int curveColor) {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();            // 创建曲线图图表渲染器
        multipleSeriesRenderer.setChartTitle(chartTitle);                   // 设置曲线标题
        multipleSeriesRenderer.setChartTitleTextSize(50);                   // 设置曲线标题文字大小
        multipleSeriesRenderer.setXTitle(xTitle);                           // 设置X轴标题
        multipleSeriesRenderer.setYTitle(yTitle);                           // 设置Y轴标题
        multipleSeriesRenderer.setXLabels(0);                               // 设置X轴上的标签数量
        multipleSeriesRenderer.setYLabels(10);                              // 设置Y轴上的标签数量
        multipleSeriesRenderer.setXLabelsAlign(Paint.Align.CENTER);         // 设置X轴标签的对齐方式
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);          // 设置Y轴标签的对齐方式
        multipleSeriesRenderer.setAntialiasing(true);                       // 消除锯齿
        multipleSeriesRenderer.setAxisTitleTextSize(30);                    // 设置坐标轴标题文本大小
        multipleSeriesRenderer.setLabelsTextSize(30);                       // 设置轴标签文本大小
        multipleSeriesRenderer.setLegendTextSize(30);                       // 设置左下角表注文本大小
        multipleSeriesRenderer.setPointSize(5f);                            // 曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);                          // 适应屏幕
        multipleSeriesRenderer.setMargins(new int[]{50, 105, 40, 30});      // 上左下右
        multipleSeriesRenderer.setShowGrid(false);                          // 是否显示网格线
        multipleSeriesRenderer.setZoomEnabled(true, true);// 确定可以缩放的坐标轴
        multipleSeriesRenderer.setAxesColor(axeColor);                      // 设置坐标轴的颜色
        multipleSeriesRenderer.setBackgroundColor(Color.WHITE);             // 背景色
        multipleSeriesRenderer.setMarginsColor(Color.WHITE);                // 边距背景色，默认背景色为黑色，这里修改为白色
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(curveColor);
        mRenderer.setLineWidth(4);
        mRenderer.setPointStyle(PointStyle.TRIANGLE);                       //描点风格，可以为圆点，方形点等等
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }

    /**
     * 根据新加的数据，更新曲线，只能运行在主线程
     *
     * @param x 新加点的x坐标
     * @param y 新加点的y坐标
     */
    public void updateChart(Date x, double y) {
        mSeries.add(x, y);
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    /**
     * 添加新的数据，多组，更新曲线，只能运行在主线程
     *
     * @param xList
     * @param yList
     */
    public void updateChart(List<Date> xList, List<Double> yList) {
        mSeries.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.CHINA);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        int size = Math.min(xList.size(), yList.size()), distance = size / 5;
        if (distance == 0) distance = 1;
        for (int i = 0; i < size; i++) {
            Date date = xList.get(i);
            try {
                date = sdf2.parse(sdf2.format(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSeries.add(date, yList.get(i));      // 对日期格式化，去掉时间的影响 ，使得图表横坐标间隔相同
            if (i % distance == 0)                // 对月视图的横坐标重叠问题进行动态设置间距
                multipleSeriesRenderer.addXTextLabel((double) date.getTime(), sdf.format(xList.get(i)));
            else
                multipleSeriesRenderer.addXTextLabel((double) date.getTime(), "");
        }
        mGraphicalView.repaint();   // 此处也可以调用invalidate()
    }
}
