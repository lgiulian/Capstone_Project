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
import timber.log.Timber;

public class PairablePlayerAdapter<P> extends TableDataAdapter<Vector<String>> {

    private int paddingLeft = 20;
    private int paddingTop = 15;
    private int paddingRight = 20;
    private int paddingBottom = 15;
    private int textSize = 14;
    private int typeface = Typeface.NORMAL;
    private int textColor = 0x99000000;

    public PairablePlayerAdapter(Context context, List<Vector<String>> data) {
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
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        textView.setTypeface(textView.getTypeface(), typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);

        textView.setText(textToShow);

        return textView;
    }
}
