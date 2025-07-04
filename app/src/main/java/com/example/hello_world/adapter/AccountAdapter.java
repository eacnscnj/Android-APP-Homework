package com.example.hello_world.adapter;

import android.app.AlertDialog; // 引入 AlertDialog
import android.content.Context;
import android.content.DialogInterface; // 引入 DialogInterface
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.R;

import java.util.List;

public class AccountAdapter extends BaseAdapter {

    private static final String TAG = "AccountAdapter";
    private Context context;
    private List<AccountIn> mDatas;
    private LayoutInflater inflater;
    private int currentUserId; // **新增：存储当前登录用户的ID**

    public interface OnItemDeleteListener {
        void onItemDeleted();
    }

    private OnItemDeleteListener deleteListener;

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    // **修改构造函数：现在接受 currentUserId 参数**
    public AccountAdapter(Context context, List<AccountIn> mDatas, int currentUserId) {
        this.context = context;
        this.mDatas = mDatas;
        this.inflater = LayoutInflater.from(context);
        this.currentUserId = currentUserId; // **初始化 currentUserId**
        Log.d(TAG, "AccountAdapter initialized with User ID: " + currentUserId);
    }

    public void setDatas(List<AccountIn> newDatas) {
        this.mDatas = newDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_mainlv, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AccountIn accountIn = mDatas.get(i);

        holder.typeIv.setImageResource(accountIn.getFocusImageID());
        holder.typeTv.setText(accountIn.getTypename());

        String note = accountIn.getNote();
        if (note == null || note.isEmpty() || note.equals("添加备注...") || note.equals("无备注")) {
            holder.noteTv.setVisibility(View.GONE);
        } else {
            holder.noteTv.setVisibility(View.VISIBLE);
            holder.noteTv.setText(note);
        }

        float studyMinutes = accountIn.getStudyTime();
        String displayTime;
        if (studyMinutes < 60) {
            displayTime = String.format("%.0f", studyMinutes) + " min"; // 使用 %.0f 格式化，移除小数点
        } else {
            float totalHours = studyMinutes / 60.0f; // 确保是浮点数除法
            displayTime = String.format("%.1fh", totalHours);
        }
        holder.studyTv.setText(displayTime);

        String fullTimeStr = accountIn.getTime();
        String startTime = "";
        if (fullTimeStr != null && fullTimeStr.length() >= 5) {
            int lastSpaceIndex = fullTimeStr.lastIndexOf(" ");
            if (lastSpaceIndex != -1 && fullTimeStr.length() - lastSpaceIndex - 1 >= 5) {
                startTime = fullTimeStr.substring(lastSpaceIndex + 1);
            } else {
                startTime = fullTimeStr; // Fallback if format is not as expected
            }
        }
        holder.tvStartTime.setText(startTime);

        holder.timeTv.setText(fullTimeStr);

        holder.deleteBtn.setOnClickListener(v -> {
            showDeleteConfirmDialog(accountIn.getId(), accountIn.getTypename());
        });

        return view;
    }

    /**
     * 显示删除确认对话框
     * @param itemId 要删除的记录的ID
     * @param itemName 记录的名称（用于对话框提示）
     */
    private void showDeleteConfirmDialog(final int itemId, String itemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除记录")
                .setMessage("确定要删除这条关于“" + itemName + "”的记录吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // **调用 DBManager 的删除方法，并传入 itemId 和 currentUserId**
                        int rowsAffected = DBManager.deleteItemFromStudyTimeTableById(itemId, currentUserId);

                        if (rowsAffected > 0) {
                            Toast.makeText(context, "记录删除成功！", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Item deleted from DB for ID: " + itemId + " by User ID: " + currentUserId);

                            // 通知监听器数据已删除，以便外部刷新列表
                            if (deleteListener != null) {
                                deleteListener.onItemDeleted();
                            }
                        } else {
                            Toast.makeText(context, "删除失败或记录不属于您。", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to delete item for ID: " + itemId + " by User ID: " + currentUserId + ". Rows affected: " + rowsAffected);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    static class ViewHolder {
        ImageView typeIv;
        TextView typeTv, noteTv, timeTv, studyTv, tvStartTime;
        ImageView deleteBtn;

        public ViewHolder(View view) {
            typeIv = view.findViewById(R.id.item_mainlv_iv);
            typeTv = view.findViewById(R.id.item_mainlv_tv_title);
            noteTv = view.findViewById(R.id.item_mainlv_tv_beizhu);
            studyTv = view.findViewById(R.id.item_mainlv_tv_money);
            timeTv = view.findViewById(R.id.item_mainlv_tv_time);
            tvStartTime = view.findViewById(R.id.item_mainlv_tv_startTime);
            deleteBtn = view.findViewById(R.id.item_mainlv_btn_delete);
        }
    }
}