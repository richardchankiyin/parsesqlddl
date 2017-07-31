package com.richard.parsesqlddl.createtable2select;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class SelectStatementGeneratorTest {

	@Test
	public void testGenerate() {
		assertEquals("select field1,field2 from table1",new SelectStatementGenerator("table1", Arrays.asList("field1","field2")).generate());
	}

}
