package com.example.hello_world;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.hello_world.adapter.HelpExpandableListAdapter;

public class HelpActivity extends Activity {

    ExpandableListView expandableListView;
    HelpExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        expandableListView = findViewById(R.id.help_expandable_list);

        // 初始化数据
        prepareListData();

        listAdapter = new HelpExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // 添加问题（标题）
        listDataHeader.add("如何记录一次学习？");
        listDataHeader.add("如何查看统计信息？");
        listDataHeader.add("如何编辑或删除已有记录？");

        // 添加答案（内容）
        List<String> answer1 = new ArrayList<>();
        answer1.add("点击主界面右下角的菜单键，再点击“记”按钮，就可以添加一条新记录，选择对应的科目和时间即可。");

        List<String> answer2 = new ArrayList<>();
        answer2.add("在主界面点击点击右下角的菜单键，再点击观，就可以看到学习至今的统计信息了。");

        List<String> answer3 = new ArrayList<>();
        answer3.add("每条记录的右侧会有删除图标，点击此图标即可删除，部分可能需要管理员账号才可以删除哦。");

        listDataChild.put(listDataHeader.get(0), answer1);
        listDataChild.put(listDataHeader.get(1), answer2);
        listDataChild.put(listDataHeader.get(2), answer3);
    }


    public void onBackClick(View view) {
        finish(); // 关闭当前 Activity，返回上一个界面（MineFragment 所在的 Activity）
    }
}
