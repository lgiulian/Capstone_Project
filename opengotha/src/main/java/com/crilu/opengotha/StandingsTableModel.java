package com.crilu.opengotha;

import java.util.ArrayList;
import java.util.List;

public class StandingsTableModel {

	private List<String> columnHeaders = new ArrayList<>();
	private List<List<Object>> tableValues = new ArrayList<>();
	
	public List<String> getColumnHeaders() {
		return columnHeaders;
	}
	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}
	public List<List<Object>> getTableValues() {
		return tableValues;
	}
	public void setTableValues(List<List<Object>> tableValues) {
		this.tableValues = tableValues;
	}
}
