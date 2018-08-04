package com.crilu.gothandroid.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

import de.codecrafters.tableview.TableDataAdapter;

public class TableAdapter<P> extends TableDataAdapter<Vector<String>> {

    public TableAdapter(Context context, List<Vector<String>> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Vector<String> player = getRowData(rowIndex);
        View renderedView;

        renderedView = renderCell(player.get(columnIndex));
        return renderedView;
    }

    private View renderCell(String textToShow) {
        //Timber.d("textToShow = %s", textToShow);
        final TextView textView = new TextView(getContext());
        int paddingBottom = 15;
        int paddingRight = 20;
        int paddingTop = 15;
        int paddingLeft = 20;
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        int typeface = Typeface.NORMAL;
        textView.setTypeface(textView.getTypeface(), typeface);
        int textSize = 14;
        textView.setTextSize(textSize);
        int textColor = 0x99000000;
        textView.setTextColor(textColor);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);

        textView.setText(textToShow);

        return textView;
    }
}
