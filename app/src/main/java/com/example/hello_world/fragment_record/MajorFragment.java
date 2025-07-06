package com.example.hello_world.fragment_record;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.TypeIn;

import java.util.ArrayList;
import java.util.List;

public class MajorFragment extends BaseFragment {

    @Override
    public void loadDataToGrid() {
        super.loadDataToGrid();
        typeInList = new ArrayList<>();
        adapter = new TypeBaseAdapter(getContext(), typeInList);
        typeGrid.setAdapter(adapter);
        List<TypeIn> outList = DBManager.getTypeList(1); // getTypeList 不受 userId 影响
        typeInList.addAll(outList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void saveAccountToDB() {
        accountIn.setKind(1);
        DBManager.insertItemToTable(accountIn);
    }

    // **实现抽象方法：返回专业课的 kind 值**
    @Override
    public int getFragmentKind() {
        return 1; // 专业课对应 kind 1
    }
}