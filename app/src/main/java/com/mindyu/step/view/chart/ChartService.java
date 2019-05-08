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
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        mSeries = new TimeSeries(curveTitle);
        multipleSeriesDataset.addSeries(mSeries);
    }

    /**
     * 获取渲染器
     *
     * @param xTitle     x轴标题
     * @param yTitle     y轴标题
     * @param axeColor   坐标轴颜色
     * @param curveColor 曲线颜色
     */
    public void setXYMultipleSeriesRenderer( String chartTitle, String xTitle, String yTitle, int axeColor,
            int curveColor) {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        multipleSeriesRenderer.setChartTitle(chartTitle);
        multipleSeriesRenderer.setChartTitleTextSize(50);
        multipleSeriesRenderer.setXTitle(xTitle);
        multipleSeriesRenderer.setYTitle(yTitle);
        // multipleSeriesRenderer.setRange(new double[] { 0, maxX, 0, maxY });//xy轴的范围
        multipleSeriesRenderer.setXLabels(0);
        multipleSeriesRenderer.setYLabels(10);
        multipleSeriesRenderer.setXLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setAntialiasing(true);
        multipleSeriesRenderer.setAxisTitleTextSize(30);
        multipleSeriesRenderer.setLabelsTextSize(30);
        multipleSeriesRenderer.setLegendTextSize(30);
        multipleSeriesRenderer.setPointSize(5f);        // 曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);      // 适应屏幕
        multipleSeriesRenderer.setMargins(new int[]{50, 105, 40, 30}); // 上左下右
        multipleSeriesRenderer.setShowGrid(false);
        multipleSeriesRenderer.setZoomEnabled(true, true);
        multipleSeriesRenderer.setAxesColor(axeColor);
        multipleSeriesRenderer.setBackgroundColor(Color.WHITE); // 背景色
        multipleSeriesRenderer.setMarginsColor(Color.WHITE);    // 边距背景色，默认背景色为黑色，这里修改为白色
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(curveColor);
        mRenderer.setLineWidth(4);
        // mRenderer.setDisplayChartValues(true);
        mRenderer.setPointStyle(PointStyle.TRIANGLE);             // 描点风格，可以为圆点，方形点等等
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
        int size = Math.min(xList.size(), yList.size());
        for (int i = 0; i < size; i++) {
            Date date = xList.get(i);
            try {
                date = sdf2.parse(sdf2.format(xList.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mSeries.add(date, yList.get(i));      // 对日期格式化，去掉时间的影响 ，使得图表横坐标间隔相同
            multipleSeriesRenderer.addXTextLabel((double)xList.get(i).getTime(), sdf.format(xList.get(i)));
        }
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }
}
