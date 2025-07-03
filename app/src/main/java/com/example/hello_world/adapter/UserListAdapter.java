package com.example.hello_world.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.UserInfo;
import com.example.hello_world.R;
import com.example.hello_world.AdminActivity; // 导入 AdminActivity 以便在主线程更新 UI

import java.util.List;
import java.util.Locale;

/**
 * UserListAdapter 是一个自定义的 BaseAdapter，用于在管理员页面 (AdminActivity) 中显示普通用户列表。
 * 每个列表项显示用户的用户名、总学习时长，并提供一个删除按钮来删除该用户及其所有学习记录。
 */
public class UserListAdapter extends BaseAdapter {

    private Context context;
    private List<UserDisplayData> mDatas; // 存储用于显示的数据，包含 UserInfo 和总学习时长
    private LayoutInflater inflater;

    /**
     * 定义一个接口，用于在用户删除后通知外部（AdminActivity）刷新列表。
     */
    public interface OnUserDeleteListener {
        void onUserDeleted(int userId); // 当用户被删除时回调，传入被删除用户的ID
    }

    private OnUserDeleteListener deleteListener;

    /**
     * 设置删除监听器的方法。
     * @param listener 实现 OnUserDeleteListener 接口的监听器实例。
     */
    public void setOnUserDeleteListener(OnUserDeleteListener listener) {
        this.deleteListener = listener;
    }

    /**
     * 构造函数。
     * @param context 当前上下文。
     * @param mDatas 初始的用户显示数据列表。
     */
    public UserListAdapter(Context context, List<UserDisplayData> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * 更新适配器数据的方法。
     * 调用此方法后，适配器会清空旧数据，添加新数据，并通知 ListView 刷新。
     * @param newDatas 新的用户显示数据列表。
     */
    public void setUsersDisplayData(List<UserDisplayData> newDatas) {
        this.mDatas.clear(); // 清空当前数据
        this.mDatas.addAll(newDatas); // 添加所有新数据
        notifyDataSetChanged(); // 通知数据已更改，刷新视图
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // 使用用户ID作为getItemId，这在处理列表项的唯一性时很有用
        return mDatas.get(position).getUserInfo().get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 如果 convertView 为空，则加载布局并创建新的 ViewHolder
            convertView = inflater.inflate(R.layout.item_admin_user, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder); // 将 ViewHolder 存储在 View 的 Tag 中
        } else {
            // 如果 convertView 不为空，则重用现有的 ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前位置的用户显示数据
        UserDisplayData data = mDatas.get(position);
        UserInfo user = data.getUserInfo();
        float totalStudyTime = data.getTotalStudyTime();

        // 设置用户名
        holder.usernameTv.setText(user.getUsername());

        // 格式化学习总时长为 "xx h yy min"
        int totalMinutes = (int) totalStudyTime; // 将浮点数分钟转换为整数分钟
        int hours = totalMinutes / 60; // 计算小时数
        int minutes = totalMinutes % 60; // 计算剩余分钟数
        String timeDisplay = String.format(Locale.getDefault(), "%d h %d min", hours, minutes);
        holder.studyTimeTv.setText(timeDisplay);

        // 设置删除按钮的点击事件监听器
        holder.deleteButton.setOnClickListener(v -> {
            // 当点击删除按钮时，显示确认对话框
            showDeleteConfirmDialog(user.get_id(), user.getUsername());
        });

        return convertView;
    }

    /**
     * 显示删除用户确认对话框。
     * @param userIdToDelete 要删除的用户的ID。
     * @param username 要删除的用户名（用于对话框提示信息）。
     */
    private void showDeleteConfirmDialog(final int userIdToDelete, String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除用户")
                .setMessage("确定要删除用户“" + username + "”及其所有学习记录吗？此操作不可撤销。")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 在后台线程执行删除操作，避免阻塞UI线程
                        new Thread(() -> {
                            // 调用 DBManager 删除用户及其所有相关记录
                            boolean success = DBManager.deleteUserAndTheirRecords(userIdToDelete);
                            // 删除操作完成后，回到UI线程更新UI和通知监听器
                            ((AdminActivity) context).runOnUiThread(() -> {
                                if (success) {
                                    // 如果删除成功，通知监听器，以便 AdminActivity 刷新列表
                                    if (deleteListener != null) {
                                        deleteListener.onUserDeleted(userIdToDelete);
                                    }
                                } else {
                                    // 如果删除失败，显示Toast提示用户
                                    Toast.makeText(context, "删除用户失败，请重试。", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start(); // 启动新线程
                    }
                })
                .setNegativeButton("取消", null) // 点击取消按钮不执行任何操作
                .show(); // 显示对话框
    }

    /**
     * ViewHolder 类用于缓存视图，优化 ListView 的滚动性能。
     */
    static class ViewHolder {
        TextView usernameTv;    // 显示用户名的 TextView
        TextView studyTimeTv;   // 显示总学习时长的 TextView
        ImageView deleteButton; // 删除用户的按钮

        /**
         * ViewHolder 的构造函数。
         * @param view 列表项的根视图。
         */
        public ViewHolder(View view) {
            usernameTv = view.findViewById(R.id.item_admin_user_username);
            studyTimeTv = view.findViewById(R.id.item_admin_user_study_time);
            deleteButton = view.findViewById(R.id.item_admin_user_delete_btn);
        }
    }

    /**
     * 辅助类 UserDisplayData，用于封装在 UserListAdapter 中显示的用户信息。
     * 包含 UserInfo 对象和该用户的总学习时长。
     */
    public static class UserDisplayData {
        private UserInfo userInfo;
        private float totalStudyTime;

        /**
         * 构造函数。
         * @param userInfo 用户的基本信息。
         * @param totalStudyTime 用户的总学习时长（分钟）。
         */
        public UserDisplayData(UserInfo userInfo, float totalStudyTime) {
            this.userInfo = userInfo;
            this.totalStudyTime = totalStudyTime;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public float getTotalStudyTime() {
            return totalStudyTime;
        }
    }
}
