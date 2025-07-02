package com.example.hello_world.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // 引入 Toast 用于提示用户

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager; // 引入 DBManager
import com.example.hello_world.R;

import java.util.List;

public class AccountAdapter extends BaseAdapter {

    private static final String TAG = "AccountAdapter"; // 用于Logcat调试
    private Context context;
    private List<AccountIn> mDatas;
    private LayoutInflater inflater;

    // 定义一个接口，用于在数据删除后通知外部（例如MainActivity）
    public interface OnItemDeleteListener {
        void onItemDeleted(); // 当条目被删除时回调
    }

    private OnItemDeleteListener deleteListener;

    // 设置监听器的方法
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public AccountAdapter(Context context, List<AccountIn> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        inflater = LayoutInflater.from(context);
    }

    // 更新数据的方法，用于外部刷新列表时调用
    public void setDatas(List<AccountIn> newDatas) {
        this.mDatas = newDatas;
        notifyDataSetChanged(); // 通知适配器数据已更改
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
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_mainlv, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // 获取当前位置的 AccountIn 对象
        AccountIn accountIn = mDatas.get(i);

        // 设置数据到 Views
        // 确保这里的 getFocusImageID() 返回的是有效的图片资源ID
        holder.typeIv.setImageResource(accountIn.getFocusImageID());
        holder.typeTv.setText(accountIn.getTypename());

        String note = accountIn.getNote();
        if (note == null || note.isEmpty() || note.equals("添加备注...") || note.equals("无备注")) { // 增加"无备注"判断
            holder.noteTv.setVisibility(View.GONE);
        } else {
            holder.noteTv.setVisibility(View.VISIBLE);
            holder.noteTv.setText(note);
        }

        float studyMinutes = accountIn.getStudyTime(); // 获取以分钟为单位的学习时长
        float totalHours = (float) ((float)studyMinutes / 60); // 将分钟转换为总小时数
        String displayTime;
        if (studyMinutes < 60) {
            displayTime = studyMinutes + " min";
        } else {
            displayTime = String.format("%.1fh", totalHours);
        }
        holder.studyTv.setText(displayTime);

        // 提取并设置开始时间
        String fullTimeStr = accountIn.getTime();
        String startTime = "";
        if (fullTimeStr != null && fullTimeStr.length() >= 5) {
            // 假设时间格式始终为 "yyyy年MM月dd日 HH:mm"
            // 我们需要提取最后的 HH:mm
            int lastSpaceIndex = fullTimeStr.lastIndexOf(" ");
            if (lastSpaceIndex != -1 && fullTimeStr.length() - lastSpaceIndex - 1 >= 5) {
                startTime = fullTimeStr.substring(lastSpaceIndex + 1);
            } else {
                startTime = fullTimeStr;
            }
        }
        holder.tvStartTime.setText(startTime);

        // 设置完整的日期和时间字符串
        holder.timeTv.setText(fullTimeStr);

        // 设置删除按钮点击监听器
        holder.deleteBtn.setOnClickListener(v -> {
            // 获取要删除的条目的 ID
            int itemIdToDelete = accountIn.getId();
            final int clickedPosition = i; // 获取点击时的正确位置

            // 调用 DBManager 删除数据库中的记录
            int rowsDeleted = DBManager.deleteItemFromStudyTimeTableById(itemIdToDelete);

            if (rowsDeleted > 0) {
                if (clickedPosition < mDatas.size() && mDatas.get(clickedPosition).getId() == itemIdToDelete) {
                    mDatas.remove(clickedPosition);
                } else {
                    Log.w(TAG, "Position mismatch during delete, consider full reload.");
                }

                // 通知 Adapter 数据已更改，刷新列表
                notifyDataSetChanged();
                Toast.makeText(context, "记录删除成功！", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Item deleted from DB and adapter for ID: " + itemIdToDelete);

                // 接口回调通知
                if (deleteListener != null) {
                    deleteListener.onItemDeleted();
                }
            } else {
                Toast.makeText(context, "删除失败，请重试。", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to delete item from DB for ID: " + itemIdToDelete);
            }
        });

        return view;
    }

    static class ViewHolder {
        ImageView typeIv;
        TextView typeTv, noteTv, timeTv, studyTv, tvStartTime;
        ImageView deleteBtn; // 删除按钮

        public ViewHolder(View view) {
            typeIv = view.findViewById(R.id.item_mainlv_iv);
            typeTv = view.findViewById(R.id.item_mainlv_tv_title);
            noteTv = view.findViewById(R.id.item_mainlv_tv_beizhu);
            studyTv = view.findViewById(R.id.item_mainlv_tv_money);
            timeTv = view.findViewById(R.id.item_mainlv_tv_time);
            tvStartTime = view.findViewById(R.id.item_mainlv_tv_startTime);
            deleteBtn = view.findViewById(R.id.item_mainlv_btn_delete); // 找到删除按钮
        }
    }
}