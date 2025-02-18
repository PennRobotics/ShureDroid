package com.github.pennrobotics.shuredroid.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.test.rule.ActivityTestRule;

import com.github.pennrobotics.shuredroid.ShureDroid;

import org.junit.Test;

public class UITest extends ActivityTestRule<ShureDroid> {

	public UITest(){
		super(ShureDroid.class);
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
		assertNull("ReceiverActivity is null", null);
	}

}
