package com.giant.watsonapp.photo;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class WatsonAxisValueFormatter implements IAxisValueFormatter
{

    private List<String> nameList;

    public WatsonAxisValueFormatter(List<String> nameList) {
        this.nameList=nameList;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int index=(int)value/10;
        if(index<nameList.size()) {
            return nameList.get(index);
        }else {
            return "not exist name";
        }
    }
}
