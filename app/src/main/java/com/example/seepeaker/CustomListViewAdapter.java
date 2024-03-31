package com.example.seepeaker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

// 사용자 리스트뷰 (글자 색이나 폰트 바꾸기 등의 용도 - 편의를 위해 따로 class로 빼둠)
public class CustomListViewAdapter extends ArrayAdapter<String> {
    private final Context listViewContext;
    private final String[] listViewRowTitle;
    private final String[] listViewRowData;

    public CustomListViewAdapter(Context listViewContext, String[] listviewRowTitle, String[] listViewRowData) {
        super(listViewContext, 0, listviewRowTitle);
        this.listViewContext = listViewContext;
        this.listViewRowTitle = listviewRowTitle;
        this.listViewRowData = listViewRowData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(listViewContext).inflate(R.layout.item_convert_view, parent, false);
        }

        TextView rowTitle = convertView.findViewById(R.id.listview_row_title);
        TextView rowData = convertView.findViewById(R.id.listview_row_data);

        rowTitle.setText(listViewRowTitle[position]);
        rowData.setText(listViewRowData[position]);

        if (listViewRowTitle[position].equals("오답 노트 보기")) {
            // 값이 오답 노트 인경우 폰트 변경
            rowTitle.setTextColor(listViewContext.getResources().getColor(R.color.blue));
            rowTitle.setTypeface(null, Typeface.BOLD);
        }
        else if (listViewRowData[position].equals("오답")) {
            // 값이 오답 인경우 폰트 변경
            rowData.setTextColor(listViewContext.getResources().getColor(R.color.red));
            rowData.setTypeface(null, Typeface.BOLD);
        }
        else if (listViewRowData[position].equals("정답")) {
            // 값이 정답 인경우 폰트 변경
            rowData.setTextColor(listViewContext.getResources().getColor(R.color.blue));
            rowData.setTypeface(null, Typeface.BOLD);
        } else {
            // 오답 노트가 아닌 경우
            rowTitle.setTextColor(listViewContext.getResources().getColor(android.R.color.black));
            rowTitle.setTypeface(null, Typeface.NORMAL);
            rowData.setTextColor(listViewContext.getResources().getColor(android.R.color.black));
            rowData.setTypeface(null, Typeface.NORMAL);
        }

        return convertView;
    }
}
