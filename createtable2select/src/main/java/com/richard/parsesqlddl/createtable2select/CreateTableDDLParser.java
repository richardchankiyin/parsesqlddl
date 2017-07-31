package com.richard.parsesqlddl.createtable2select;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTableDDLParser {
	private static Logger logger = LoggerFactory.getLogger(CreateTableDDLParser.class);
	
	
	private String ddl = null;
	private String tableName = null;
	private List<String> fieldNames = null;
	
	public CreateTableDDLParser(String ddl) {
		this.ddl = ddl;
		this.fieldNames = new ArrayList<String>(); 
		logger.debug("ddl: [{}]", ddl);
	}

	public void parse() {
		isStringBlank(ddl);
		
		String ddlReplaceNewLineAndTag = replaceNewLineAndTag(ddl);
		
		logger.debug("ddlReplaceNewLineAndTag: [{}]", ddlReplaceNewLineAndTag);
	
		List<String> ddlTokens = tokenize(ddlReplaceNewLineAndTag);
		
		isCreateTableDDL(ddlTokens);
		
		ddlContainsBrackets(ddlReplaceNewLineAndTag);
		
		tableName = getTableName(ddlTokens);
		
		String ddlSubStringOpenBracket = getSubStringStartFromOpenBracket(ddlReplaceNewLineAndTag);
		
		logger.debug("ddlSubStringOpenBracket: [{}]", ddlSubStringOpenBracket);
		
		fieldNames = getFieldNames(getContentFromOpenAndCloseBrackets(ddlSubStringOpenBracket));
	}
	
	public String getTableName() { return this.tableName; }
	public List<String> getFieldNames() { return this.fieldNames; }
	
	protected static void isStringBlank(String input) {
		if (input == null || "".equals(input.trim()))
			throw new IllegalArgumentException("blank string");
	}
	
	protected static String replaceNewLineAndTag(String ddl) {
		String ddlReplaceNewLine = ddl.replace('\n', ' ');
		ddlReplaceNewLine = ddlReplaceNewLine.replace('\r', ' ');
		String ddlReplaceNewLineAndTag = ddlReplaceNewLine.replace('\t', ' ');
		return ddlReplaceNewLineAndTag.trim().toUpperCase();
	}
	
	protected static List<String> tokenize(String input) {
		List<String> res = new ArrayList<String>();
		String[] tokens = input.split(" ");
		for (String token: tokens) {
			if (!"".equals(token)) {
				res.add(token);
			}
		}
		
		return res;
	}
	
	protected static void isCreateTableDDL(List<String> ddltokens) {
		if (ddltokens.size() >= 3 && "CREATE".equals(ddltokens.get(0)) && "TABLE".equals(ddltokens.get(1))) {
			logger.debug("valid");
		} else {
			throw new IllegalArgumentException("not a create table ddl");
		}
	}
	
	protected static void ddlContainsBrackets(String input) {
		if (input.contains("(") && input.contains(")")) {
			logger.debug("contains ( and )");
		} else {
			throw new IllegalArgumentException("contains no ( or )");
		}
	}
	
	protected static String getTableName(List<String> ddltokens) {
		if (ddltokens.get(2).contains("(")) {
			String res = ddltokens.get(2).split("\\(")[0];
			if (res.contains(")")) {
				throw new IllegalArgumentException("table name contains ) not accepted");
			} else {
				return res;
			}
		} else {
			if (ddltokens.get(3).startsWith("(")) {
				String res = ddltokens.get(2);
				if (res.contains(")")) {
					throw new IllegalArgumentException("table name contains ) not accepted");
				} else {
					return res;
				}
			} else {
				throw new IllegalArgumentException("fail to get table name");
			}
		}
	}
	
	protected static String getSubStringStartFromOpenBracket(String input) {
		logger.debug("input: {}", input);
		int pos = charPos(input, '(');
		logger.debug("input: {} pos: {}", input, pos);
		if (pos == -1)
			throw new IllegalArgumentException("( not found!");
		return input.substring(pos);
	}
	
	protected static int charPos(String input, char c) {
		int length = input.length();
		for (int i = 0; i < length; ++i) {
			if (input.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}
	
	protected static String getContentFromOpenAndCloseBrackets(String input) {
		logger.debug("getContentFromOpenAndCloseBrackets input: [{}]", input);
		final Character CHAR = 'C';
		Stack<Character> s = new Stack<Character>();
		int beginPos = -1;
		int endPos = -1;
		
		int length = input.length();
		
		for (int i = 0; i < length; ++i) {
			char c = input.charAt(i);
			if (c == '(') {
				if (beginPos == -1) {
					beginPos = i;
				}
				s.add(CHAR);
			}
			else if (c == ')') {
				try {
					s.pop();
					if (s.isEmpty()) {
						if (endPos == -1) {
							endPos = i;
						}
					}
				}
				catch (Exception e) {
					logger.error("error caught", e);
					throw new IllegalArgumentException("ddl malformat");
				}
				
			}
		}
		
		return input.substring(beginPos + 1, endPos);
	}
	
	protected static List<String> getFieldNames(String input) {
		String[] fieldNameAndDefinitions = input.split(",");
		List<String> res = new ArrayList<String>();
		for (String fieldNameAndDefinition: fieldNameAndDefinitions) {
			String s = fieldNameAndDefinition.trim();
			List<String> tokens = tokenize(s);
			if (tokens.size() <= 1) {
				throw new IllegalArgumentException("field definition malformat");
			} else {
				res.add(tokens.get(0));
			}
		}
		
		return res;
	}
}
