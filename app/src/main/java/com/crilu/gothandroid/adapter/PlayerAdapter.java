package com.crilu.gothandroid.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crilu.opengotha.Player;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.model.TableColumnModel;
import timber.log.Timber;

public class PlayerAdapter<R> extends TableDataAdapter<Player> {
    private int paddingLeft = 20;
    private int paddingTop = 15;
    private int paddingRight = 20;
    private int paddingBottom = 15;
    private int textSize = 14;
    private int typeface = Typeface.NORMAL;
    private int textColor = 0x99000000;

    public PlayerAdapter(Context context, Player[] data) {
        super(context, data);
    }

    public PlayerAdapter(Context context, List<Player> data) {
        super(context, data);
    }

    protected PlayerAdapter(Context context, int columnCount, List<Player> data) {
        super(context, columnCount, data);
    }

    protected PlayerAdapter(Context context, TableColumnModel columnModel, List<Player> data) {
        super(context, columnModel, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Player player = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderCell(player.getName());
                break;
            case 1:
                renderedView = renderCell(player.getFirstName());
                break;
            case 2:
                renderedView = renderCell(player.getCountry());
                break;
            case 3:
                renderedView = renderCell(player.getClub());
                break;
            case 4:
                renderedView = renderCell(player.getRatingOrigin());
                break;
            case 5:
                renderedView = renderCell(player.getRatingOrigin());
                break;
            case 6:
                renderedView = renderCell(player.getStrGrade());
                break;
        }

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
