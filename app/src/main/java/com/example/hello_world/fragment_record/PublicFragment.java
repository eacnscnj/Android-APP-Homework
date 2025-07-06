package com.example.hello_world.fragment_record;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.TypeIn;

import java.util.ArrayList;
import java.util.List;

public class PublicFragment extends BaseFragment {

    @Override
    public void loadDataToGrid() {
        super.loadDataToGrid();
        typeInList = new ArrayList<>();
        adapter = new TypeBaseAdapter(getContext(), typeInList);
        typeGrid.setAdapter(adapter);
        List<TypeIn> outList = DBManager.getTypeList(0); // 获取所有公共课
        typeInList.addAll(outList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void saveAccountToDB() {
        accountIn.setKind(0);
        DBManager.insertItemToTable(accountIn);
    }

    // **实现抽象方法：返回公共课的 kind 值**
    @Override
    public int getFragmentKind() {
        return 0; // 公共课对应 kind 0
    }
}