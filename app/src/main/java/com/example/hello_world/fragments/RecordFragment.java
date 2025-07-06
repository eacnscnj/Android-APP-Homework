package com.example.hello_world.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.MainActivity;
import com.example.hello_world.R;
import com.example.hello_world.adapter.AccountAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment implements AccountAdapter.OnItemDeleteListener {

    private ListView listView;
    private AccountAdapter adapter;
    private List<AccountIn> dataList = new ArrayList<>();
    private int currentUserId = -1;

    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * 静态工厂方法传入用户ID（可选）
     */
    public static RecordFragment newInstance(int userId) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取传入的用户ID
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("USER_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        listView = root.findViewById(R.id.record_list_view);

        loadData();
        adapter = new AccountAdapter(getContext(), dataList, currentUserId);

        // ✅ 设置删除监听器（通过接口实现）
        adapter.setOnItemDeleteListener(this);

        // ✅ 设置分享监听器（使用 Lambda 表达式）
        adapter.setOnItemSharedListener(() -> {
            loadData();
            adapter.notifyDataSetChanged();
            ((MainActivity) requireActivity()).notifyRecordChanged();
        });

        listView.setAdapter(adapter);

        return root;
    }

    /**
     * 从数据库加载数据并刷新列表
     */
    private void loadData() {
        if (currentUserId == -1) {
            currentUserId = DBManager.getCurrentUserId();
        }
        List<AccountIn> list = DBManager.getAllAccountList(currentUserId);
        dataList.clear();
        if (list != null) {
            dataList.addAll(list);
        }
    }

    /**
     * 监听适配器中删除操作，刷新列表并更新界面
     */
    @Override
    public void onItemDeleted() {
        loadData();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        ((MainActivity) requireActivity()).notifyRecordChanged();
    }

    /**
     * 外部调用可刷新数据
     */
    public void refreshData() {
        loadData();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
