package com.example.hello_world.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.R;

import java.util.List;

// 负责将数据库提取的数据转换成视图可用的形式
public class AccountAdapter extends BaseAdapter {

    private static final String TAG = "AccountAdapter";
    private Context context; // 获取对应包含Activity信息的上下文
    private List<AccountIn> mDatas; // 数据库信息
    private LayoutInflater inflater; // 转换XML文件到View
    private int currentUserId;

    public interface OnItemDeleteListener {
        void onItemDeleted();
    }

    private OnItemDeleteListener deleteListener; // 为Fragment和Activity之间提供通信, 当删除数据后用于刷新页面

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener; // 设置删除监听器
    }

    // 初始化
    public AccountAdapter(Context context, List<AccountIn> mDatas, int currentUserId) {
        this.context = context;
        this.mDatas = mDatas;
        this.inflater = LayoutInflater.from(context);
        this.currentUserId = currentUserId;
        Log.d(TAG, "AccountAdapter initialized with User ID: " + currentUserId);
    }

    // 设置数据库数据
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

    // 为每一个数据创建一个视图
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
            displayTime = String.format("%.0f", studyMinutes) + " min";
        } else {
            float totalHours = studyMinutes / 60.0f;
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
                startTime = fullTimeStr;
            }
        }
        holder.tvStartTime.setText(startTime);

        holder.timeTv.setText(fullTimeStr);

        holder.deleteBtn.setOnClickListener(v -> {
            showDeleteConfirmDialog(accountIn.getId(), accountIn.getTypename());
        });

        // 新增：设置长按监听，弹出分享输入弹窗
        view.setOnLongClickListener(v -> {
            showShareDialog(accountIn);
            return true; // 消费事件，防止触发点击等其他事件
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
                        int rowsAffected = DBManager.deleteItemFromStudyTimeTableById(itemId, currentUserId);

                        if (rowsAffected > 0) {
                            Toast.makeText(context, "记录删除成功！", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Item deleted from DB for ID: " + itemId + " by User ID: " + currentUserId);

                            if (deleteListener != null) {
                                deleteListener.onItemDeleted(); // 通知监听器删除事件发生
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

    // 视图整体，存储数据库提取条目的信息，并提前找到所有视图组件
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


    public interface OnItemSharedListener {
        void onItemShared(); // 分享成功后的回调
    }

    private OnItemSharedListener shareListener;

    public void setOnItemSharedListener(OnItemSharedListener listener) {
        this.shareListener = listener;
    }


    //分享功能实现
    private void showShareDialog(AccountIn accountIn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("发表分享");

        //DBManager.initDB(context);
        final EditText input = new EditText(context);
        input.setHint("写点你的学习心得...");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(3);
        input.setMaxLines(5);
        input.setVerticalScrollBarEnabled(true);
        input.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        int padding = (int) (16 * context.getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String shareContent = input.getText().toString().trim();
            if (!shareContent.isEmpty()) {
                // 保存分享到数据库，示例调用DBManager（需你实现）
                boolean success = DBManager.insertShareRecord(
                        currentUserId,
                        accountIn.getId(),
                        shareContent
                );

                if (success) {
                    Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                    if (shareListener != null) {
                        shareListener.onItemShared(); // 通知Fragment刷新
                    }
                } else {
                    Toast.makeText(context, "分享失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "分享内容不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}