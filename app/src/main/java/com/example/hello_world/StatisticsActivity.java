package com.example.hello_world;

import android.graphics.Color;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private TextView tvTotalFocusCount;
    private TextView tvTotalStudyTime;
    private TextView tvDailyAverageTime;

    private TextView tvCurrentDateTitle;
    private TextView tvTodayFocusCount;
    private TextView tvTodayStudyTime;

    private ImageView backButton;
    private ImageView ivPrevDay;
    private ImageView ivNextDay;

    private PieChart pieChartDailyDistribution;

    private Calendar currentDisplayDate;
    private SimpleDateFormat dateFormat;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Log.e(TAG, "Error: No user ID received. Redirecting or showing error.");
            finish();
            return;
        } else {
            Log.d(TAG, "StatisticsActivity initialized for user ID: " + currentUserId);
        }

        tvTotalFocusCount = findViewById(R.id.tv_total_focus_count);
        tvTotalStudyTime = findViewById(R.id.tv_total_study_time);
        tvDailyAverageTime = findViewById(R.id.tv_daily_average_time);

        tvCurrentDateTitle = findViewById(R.id.tv_current_date_title);
        tvTodayFocusCount = findViewById(R.id.tv_today_focus_count);
        tvTodayStudyTime = findViewById(R.id.tv_today_study_time);
        ivPrevDay = findViewById(R.id.iv_prev_day);
        ivNextDay = findViewById(R.id.iv_next_day);

        pieChartDailyDistribution = findViewById(R.id.pie_chart_daily_distribution);
        setupPieChart();

        backButton = findViewById(R.id.statistics_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        currentDisplayDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        ivPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDisplayDate.add(Calendar.DAY_OF_MONTH, -1);
                loadDailyStatistics();
            }
        });

        ivNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDisplayDate.add(Calendar.DAY_OF_MONTH, 1);
                loadDailyStatistics();
            }
        });

        loadStatisticsData();
    }

    private void loadStatisticsData() {
        loadCumulativeStatistics();
        loadDailyStatistics();
    }

    // 计算累计专注数据(次数,总长度,平均每天时长)
    /**
     * 加载并显示累计统计数据
     */
    private void loadCumulativeStatistics() {
        int totalFocusCount = DBManager.getTotalFocusCount(currentUserId);
        tvTotalFocusCount.setText(String.valueOf(totalFocusCount));

        float totalStudyTime = DBManager.getTotalStudyTime(currentUserId);
        tvTotalStudyTime.setText(String.format(Locale.getDefault(), "%.0f", totalStudyTime));

        float dailyAverageTime = 0.0f;
        Calendar firstRecordDate = DBManager.getFirstRecordDate(currentUserId);
        if (firstRecordDate != null) {
            Calendar today = Calendar.getInstance();
            firstRecordDate.set(Calendar.HOUR_OF_DAY, 0);
            firstRecordDate.set(Calendar.MINUTE, 0);
            firstRecordDate.set(Calendar.SECOND, 0);
            firstRecordDate.set(Calendar.MILLISECOND, 0);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diffInMillis = today.getTimeInMillis() - firstRecordDate.getTimeInMillis();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;

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
        int month = currentDisplayDate.get(Calendar.MONTH) + 1;
        int day = currentDisplayDate.get(Calendar.DAY_OF_MONTH);

        String formattedDate = dateFormat.format(currentDisplayDate.getTime());
        tvCurrentDateTitle.setText("当日专注 " + formattedDate);

        int todayFocusCount = DBManager.getDailyFocusCount(year, month, day, currentUserId);
        tvTodayFocusCount.setText(String.valueOf(todayFocusCount));

        float todayStudyTime = DBManager.getDailyStudyTime(year, month, day, currentUserId);
        tvTodayStudyTime.setText(String.format(Locale.getDefault(), "%.0f", todayStudyTime));

        // **加载饼图数据时传入 currentUserId**
        loadPieChartData(year, month, day);
    }

    /**
     * 初始化饼图的基本设置
     */
    private void setupPieChart() {
        pieChartDailyDistribution.setUsePercentValues(true);
        pieChartDailyDistribution.getDescription().setEnabled(false);
        pieChartDailyDistribution.setExtraOffsets(5, 10, 5, 5);

        pieChartDailyDistribution.setDragDecelerationFrictionCoef(0.95f);

        pieChartDailyDistribution.setDrawHoleEnabled(true);
        pieChartDailyDistribution.setHoleColor(Color.WHITE);

        pieChartDailyDistribution.setTransparentCircleColor(Color.WHITE);
        pieChartDailyDistribution.setTransparentCircleAlpha(110);

        pieChartDailyDistribution.setHoleRadius(58f);
        pieChartDailyDistribution.setTransparentCircleRadius(61f);

        pieChartDailyDistribution.setDrawCenterText(true);
        pieChartDailyDistribution.setCenterText("时间分布");

        pieChartDailyDistribution.setRotationAngle(0);
        pieChartDailyDistribution.setRotationEnabled(true);
        pieChartDailyDistribution.setHighlightPerTapEnabled(true);

        pieChartDailyDistribution.setEntryLabelColor(Color.BLACK);
        pieChartDailyDistribution.setEntryLabelTextSize(12f);
    }

    /**
     * 根据传入日期加载饼图数据
     * @param year 年
     * @param month 月 (1-12)
     * @param day 日
     */
    private void loadPieChartData(int year, int month, int day) {
        java.util.Map<String, Float> distributionData = DBManager.getDailyStudyTimeDistribution(year, month, day, currentUserId);

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (distributionData.isEmpty()) {
            entries.add(new PieEntry(100f, "无数据"));
            colors.add(Color.LTGRAY);
            pieChartDailyDistribution.setCenterText("当日无数据");
        } else {
            pieChartDailyDistribution.setCenterText("时间分布");
            int[] defaultColors = new int[]{
                    Color.rgb(255, 102, 0),
                    Color.rgb(245, 199, 0),
                    Color.rgb(106, 150, 31),
                    Color.rgb(179, 100, 53),
                    Color.rgb(193, 37, 82),
                    Color.rgb(74, 189, 131),
                    Color.rgb(148, 84, 235),
                    Color.rgb(59, 93, 203),
                    Color.rgb(255, 159, 64)
            };
            int colorIndex = 0;

            for (Map.Entry<String, Float> entry : distributionData.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                colors.add(defaultColors[colorIndex % defaultColors.length]);
                colorIndex++;
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "专注类型");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChartDailyDistribution.setData(data);
        pieChartDailyDistribution.invalidate();
        pieChartDailyDistribution.animateY(1400);
    }

    public void onBackClick(View view) {
        finish();
    }
    public void onPrevDayClick(View view) {
        // Handled by ivPrevDay's OnClickListener
    }
    public void onNextDayClick(View view) {
        // Handled by ivNextDay's OnClickListener
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1 in onResume.");
            // 再次处理未登录情况
        }
        loadStatisticsData();
    }
}