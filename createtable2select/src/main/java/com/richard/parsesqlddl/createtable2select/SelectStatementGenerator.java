package com.richard.parsesqlddl.createtable2select;

import java.util.List;

public class SelectStatementGenerator {
	private static final String TEMPLATE = "select %s from %s";
	private String tableName;
	private List<String> fields;
	public SelectStatementGenerator(String tableName, List<String> fields) {
		if (tableName == null || "".equals(tableName.trim()))
			throw new IllegalArgumentException("tableName blank!");
		
		if (fields == null || fields.isEmpty())
			throw new IllegalArgumentException("no fields!");
		
		this.tableName = tableName;
		this.fields = fields;
	}
	
	public String generate() {
		return String.format(TEMPLATE, linkUpFields(fields), tableName);
	}
	
	protected static String linkUpFields(List<String> fields) {
		int length = fields.size();
		StringBuilder strB = new StringBuilder(fields.get(0));
		for (int i = 1; i < length; ++i) {
			strB.append(",").append(fields.get(i));
		}
		
		return strB.toString();
		
	}
}
