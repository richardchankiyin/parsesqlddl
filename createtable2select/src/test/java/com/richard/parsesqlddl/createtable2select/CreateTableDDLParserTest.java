package com.richard.parsesqlddl.createtable2select;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class CreateTableDDLParserTest {

	@Test(expected=IllegalArgumentException.class)
	public void testIsStringBlankTrue() {
		CreateTableDDLParser.isStringBlank("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsStringBlankTrue2() {
		CreateTableDDLParser.isStringBlank(null);
	}
	
	@Test
	public void testIsStringBlankFalse() {
		CreateTableDDLParser.isStringBlank(" a ");
	}

	@Test
	public void testReplaceNewLineAndTag() {
		assertEquals("CREATE TABLE ABC",CreateTableDDLParser.replaceNewLineAndTag("create table abc"));
		assertEquals("CREATE TABLE ABC",CreateTableDDLParser.replaceNewLineAndTag("create\ntable\tabc"));
	}

	@Test
	public void testTokenize() {
		assertEquals(Arrays.asList("CREATE","TABLE","ABC"),CreateTableDDLParser.tokenize("CREATE TABLE ABC"));
		assertEquals(Arrays.asList("CREATE","TABLE","ABC"),CreateTableDDLParser.tokenize("CREATE TABLE  ABC"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsCreateTableDDLLessThan2Tokens() {
		CreateTableDDLParser.isCreateTableDDL(Arrays.asList("CREATE","TABLE"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsCreateTableDDLNoCreate() {
		CreateTableDDLParser.isCreateTableDDL(Arrays.asList("ALTER","TABLE", "ABC"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsCreateTableDDLNoTable() {
		CreateTableDDLParser.isCreateTableDDL(Arrays.asList("CREATE","SEQUENCE", "ABC"));
	}
	
	@Test
	public void testIsCreateTableDDL() {
		CreateTableDDLParser.isCreateTableDDL(Arrays.asList("CREATE","TABLE","ABC"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsContainsBlacketFalse1() {
		CreateTableDDLParser.ddlContainsBrackets("(");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsContainsBlacketFalse2() {
		CreateTableDDLParser.ddlContainsBrackets(")");
	}
	
	@Test
	public void testIsContainsBlacket() {
		CreateTableDDLParser.ddlContainsBrackets("()");
	}
	
	@Test
	public void testGetTableNameSuccess() {
		assertEquals("ABC",CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC", "(")));
		assertEquals("ABC",CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC(")));
		assertEquals("ABC",CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC(BCD")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetTableNameFail1() {
		CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC)", "("));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetTableNameFail2() {
		CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC)("));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetTableNameFail3() {
		CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC", "DEF"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetTableNameFail4() {
		CreateTableDDLParser.getTableName(Arrays.asList("CREATE","TABLE", "ABC", "DEF("));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetSubStringStartFromOpenBracketFail() {
		CreateTableDDLParser.getSubStringStartFromOpenBracket("abc");
	}
	
	@Test
	public void testGetSubStringStartFromOpenBracketSuccess() {
		assertEquals("(def",CreateTableDDLParser.getSubStringStartFromOpenBracket("abc(def"));
	}
	
	@Test
	public void testGetContentFromOpenAndCloseBrackets() {
		assertEquals("abc def", CreateTableDDLParser.getContentFromOpenAndCloseBrackets("(abc def)"));
		assertEquals("abcdef", CreateTableDDLParser.getContentFromOpenAndCloseBrackets("(abcdef)"));
		assertEquals("abcdef", CreateTableDDLParser.getContentFromOpenAndCloseBrackets("(abcdef)()"));
		assertEquals("a(bc)def", CreateTableDDLParser.getContentFromOpenAndCloseBrackets("(a(bc)def)"));
		assertEquals("a(bc)def", CreateTableDDLParser.getContentFromOpenAndCloseBrackets("(a(bc)def)()"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetContentFromOpenAndCloseBracketsFail() {
		CreateTableDDLParser.getContentFromOpenAndCloseBrackets("(abc def))");
	}
	
	@Test
	public void testGetFieldNames() {
		assertEquals(Arrays.asList("abc"),CreateTableDDLParser.getFieldNames("abc varchar(200)"));
		assertEquals(Arrays.asList("abc","def"),CreateTableDDLParser.getFieldNames("abc varchar(200), def int"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetFieldNamesFail() {
		CreateTableDDLParser.getFieldNames("abc, def varchar(100)");
	}
	
	@Test
	public void testParse1() throws Exception{
		String content = FileUtils.readFileToString(new File("src\\test\\resources\\scripts\\script1"), Charset.defaultCharset());
		CreateTableDDLParser parser = new CreateTableDDLParser(content);
		parser.parse();
		assertEquals("ABC",parser.getTableName());
		assertEquals(Arrays.asList("FIELD1","FIELD2","FIELD3"), parser.getFieldNames());
	}
	
	@Test
	public void testParse2() throws Exception{
		String content = FileUtils.readFileToString(new File("src\\test\\resources\\scripts\\script2"), Charset.defaultCharset());
		CreateTableDDLParser parser = new CreateTableDDLParser(content);
		parser.parse();
		assertEquals("ABC",parser.getTableName());
		assertEquals(Arrays.asList("FIELD1","FIELD2","FIELD3"), parser.getFieldNames());
	}
}
