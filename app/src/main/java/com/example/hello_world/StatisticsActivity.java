package com.example.hello_world;

import android.graphics.Color; // 导入 Color 类
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_world.Database.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 导入 MPAndroidChart 相关的类
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter; // 用于显示百分比

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    // 累计统计的 TextViews
    private TextView tvTotalFocusCount;
    private TextView tvTotalStudyTime;
    private TextView tvDailyAverageTime;

    // 当日统计的 TextViews 和日期显示
    private TextView tvCurrentDateTitle;
    private TextView tvTodayFocusCount;
    private TextView tvTodayStudyTime;

    // 顶部返回按钮
    private ImageView backButton;

    // 当日统计的日期导航按钮
    private ImageView ivPrevDay;
    private ImageView ivNextDay;

    // 饼图
    private PieChart pieChartDailyDistribution; // 声明饼图变量

    // 当前显示日期的 Calendar 对象
    private Calendar currentDisplayDate;
    // 日期格式化器
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 初始化累计统计的视图
        tvTotalFocusCount = findViewById(R.id.tv_total_focus_count);
        tvTotalStudyTime = findViewById(R.id.tv_total_study_time);
        tvDailyAverageTime = findViewById(R.id.tv_daily_average_time);

        // 初始化当日统计的视图和日期导航按钮
        tvCurrentDateTitle = findViewById(R.id.tv_current_date_title);
        tvTodayFocusCount = findViewById(R.id.tv_today_focus_count);
        tvTodayStudyTime = findViewById(R.id.tv_today_study_time);
        ivPrevDay = findViewById(R.id.iv_prev_day);
        ivNextDay = findViewById(R.id.iv_next_day);

        // 初始化饼图
        pieChartDailyDistribution = findViewById(R.id.pie_chart_daily_distribution); // 初始化饼图变量
        setupPieChart(); // 调用饼图初始化方法

        // 初始化返回按钮
        backButton = findViewById(R.id.statistics_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 关闭当前Activity
            }
        });

        // 初始化当前显示日期为今天
        currentDisplayDate = Calendar.getInstance();
        // 初始化日期格式化器
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 设置日期导航按钮的点击监听器
        ivPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDisplayDate.add(Calendar.DAY_OF_MONTH, -1); // 日期减一天
                loadDailyStatistics(); // 重新加载当日数据 (包括饼图)
            }
        });

        ivNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDisplayDate.add(Calendar.DAY_OF_MONTH, 1); // 日期加一天
                loadDailyStatistics(); // 重新加载当日数据 (包括饼图)
            }
        });

        // 加载并显示所有数据
        loadStatisticsData();
    }

    private void loadStatisticsData() {
        // 加载累计统计数据
        loadCumulativeStatistics();
        // 加载当日统计数据 (默认为今天，包括饼图)
        loadDailyStatistics();
    }

    /**
     * 加载并显示累计统计数据
     */
    private void loadCumulativeStatistics() {
        int totalFocusCount = DBManager.getTotalFocusCount();
        tvTotalFocusCount.setText(String.valueOf(totalFocusCount));

        float totalStudyTime = DBManager.getTotalStudyTime();
        tvTotalStudyTime.setText(String.format(Locale.getDefault(), "%.0f", totalStudyTime));

        float dailyAverageTime = 0.0f;
        Calendar firstRecordDate = DBManager.getFirstRecordDate();
        if (firstRecordDate != null) {
            Calendar today = Calendar.getInstance();
            // 确保只计算日期的天数差，忽略时间
            firstRecordDate.set(Calendar.HOUR_OF_DAY, 0);
            firstRecordDate.set(Calendar.MINUTE, 0);
            firstRecordDate.set(Calendar.SECOND, 0);
            firstRecordDate.set(Calendar.MILLISECOND, 0);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diffInMillis = today.getTimeInMillis() - firstRecordDate.getTimeInMillis();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1; // +1 包含当天

            if (diffInDays > 0) {
                dailyAverageTime = totalStudyTime / diffInDays;
            }
        }
        tvDailyAverageTime.setText(String.format(Locale.getDefault(), "%.0f", dailyAverageTime));
    }

    /**
     * 加载并显示当前显示日期的当日统计数据 (包括饼图)
     */
    private void loadDailyStatistics() {
        int year = currentDisplayDate.get(Calendar.YEAR);
        int month = currentDisplayDate.get(Calendar.MONTH) + 1; // Calendar 月份从 0 开始
        int day = currentDisplayDate.get(Calendar.DAY_OF_MONTH);

        // 更新日期标题
        String formattedDate = dateFormat.format(currentDisplayDate.getTime());
        tvCurrentDateTitle.setText("当日专注 " + formattedDate);

        // 获取当日统计数据
        int todayFocusCount = DBManager.getDailyFocusCount(year, month, day);
        tvTodayFocusCount.setText(String.valueOf(todayFocusCount));

        float todayStudyTime = DBManager.getDailyStudyTime(year, month, day);
        tvTodayStudyTime.setText(String.format(Locale.getDefault(), "%.0f", todayStudyTime));

        // **加载饼图数据**
        loadPieChartData(year, month, day);
    }


    /**
     * 初始化饼图的基本设置
     */
    private void setupPieChart() {
        pieChartDailyDistribution.setUsePercentValues(true); // 显示百分比
        pieChartDailyDistribution.getDescription().setEnabled(false); // 不显示描述文本
        pieChartDailyDistribution.setExtraOffsets(5, 10, 5, 5); // 设置偏移，防止标签被裁剪

        pieChartDailyDistribution.setDragDecelerationFrictionCoef(0.95f); // 拖拽摩擦系数

        pieChartDailyDistribution.setDrawHoleEnabled(true); // 绘制中心圆孔
        pieChartDailyDistribution.setHoleColor(Color.WHITE); // 中心圆孔颜色

        pieChartDailyDistribution.setTransparentCircleColor(Color.WHITE); // 透明圆颜色
        pieChartDailyDistribution.setTransparentCircleAlpha(110); // 透明圆透明度

        pieChartDailyDistribution.setHoleRadius(58f); // 中心圆孔半径
        pieChartDailyDistribution.setTransparentCircleRadius(61f); // 透明圆半径

        pieChartDailyDistribution.setDrawCenterText(true); // 绘制中心文本
        pieChartDailyDistribution.setCenterText("时间分布"); // 中心文本

        pieChartDailyDistribution.setRotationAngle(0); // 初始旋转角度
        // enable rotation of the chart by touch
        pieChartDailyDistribution.setRotationEnabled(true); // 允许旋转
        pieChartDailyDistribution.setHighlightPerTapEnabled(true); // 允许点击高亮

        // entry label styling
        pieChartDailyDistribution.setEntryLabelColor(Color.BLACK); // 扇区标签颜色
        pieChartDailyDistribution.setEntryLabelTextSize(12f); // 扇区标签文字大小
    }

    /**
     * 根据传入日期加载饼图数据
     * @param year 年
     * @param month 月 (1-12)
     * @param day 日
     */
    private void loadPieChartData(int year, int month, int day) {
        java.util.Map<String, Float> distributionData = DBManager.getDailyStudyTimeDistribution(year, month, day);

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (distributionData.isEmpty()) {
            // 如果当天没有数据，显示一个“无数据”的扇区
            entries.add(new PieEntry(100f, "无数据"));
            colors.add(Color.LTGRAY); // 浅灰色表示无数据
            pieChartDailyDistribution.setCenterText("当日无数据");
        } else {
            pieChartDailyDistribution.setCenterText("时间分布");
            // 定义一组颜色，你可以根据需要添加更多或使用 ColorTemplate
            int[] defaultColors = new int[]{
                    Color.rgb(255, 102, 0), // Orange
                    Color.rgb(245, 199, 0), // Yellow
                    Color.rgb(106, 150, 31), // Green
                    Color.rgb(179, 100, 53), // Brown
                    Color.rgb(193, 37, 82),  // Red
                    Color.rgb(74, 189, 131), // Light Green
                    Color.rgb(148, 84, 235), // Purple
                    Color.rgb(59, 93, 203),  // Blue
                    Color.rgb(255, 159, 64)  // Orange pastel
            };
            int colorIndex = 0;

            for (Map.Entry<String, Float> entry : distributionData.entrySet()) {
                // MPAndroidChart 的 PieEntry 需要一个值和对应的标签
                // 值是浮点型，标签是 String (例如：专注类型名称)
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                colors.add(defaultColors[colorIndex % defaultColors.length]); // 循环使用颜色
                colorIndex++;
            }
        }


        // 创建 PieDataSet
        PieDataSet dataSet = new PieDataSet(entries, "专注类型");
        dataSet.setSliceSpace(2f); // 扇区之间的间隔
        dataSet.setSelectionShift(5f); // 选中时扇区的突出距离

        // 添加颜色
        dataSet.setColors(colors);

        // 设置值文本属性
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // 值显示在扇区外部
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // 标签显示在扇区外部

        // 创建 PieData 对象
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter()); // 将值格式化为百分比
        data.setValueTextSize(14f); // 百分比文字大小
        data.setValueTextColor(Color.BLACK); // 百分比文字颜色

        // 设置数据到饼图
        pieChartDailyDistribution.setData(data);
        pieChartDailyDistribution.invalidate(); // 刷新饼图
        pieChartDailyDistribution.animateY(1400); // 添加Y轴动画
    }

    // 为 XML 中 ImageView 的 onClick 属性定义方法（如果使用）
    public void onBackClick(View view) {
        finish();
    }
    public void onPrevDayClick(View view) {
        // 交给 ivPrevDay 的 OnClickListener 处理
    }
    public void onNextDayClick(View view) {
        // 交给 ivNextDay 的 OnClickListener 处理
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当Activity重新激活时，重新加载所有数据以确保最新
        loadStatisticsData();
    }
}