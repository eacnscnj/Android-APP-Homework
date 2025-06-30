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
        List<TypeIn> outList = DBManager.getTypeList(1);
        typeInList.addAll(outList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void saveAccountToDB() {
        accountIn.setKind(1);
        DBManager.insertItemToTable(accountIn);
    }
}