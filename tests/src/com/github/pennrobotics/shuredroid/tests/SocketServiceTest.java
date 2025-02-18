package com.github.pennrobotics.shuredroid.tests;

import static org.junit.Assert.assertNotNull;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.github.pennrobotics.shuredroid.ShureDroid;

import org.junit.Test;

public class SocketServiceTest extends ActivityTestRule<ShureDroid> {

	public SocketServiceTest(){
		super(ShureDroid.class);
	}

	@Test
	public void test() {
		System.out.println("Start tst");
		Intent socketService = new Intent(super.getActivity(), SocketService.class);
		socketService.setAction("ABCD");
		super.getActivity().startService(socketService);
		for (int i = 0; i < 1; i++) {
			//send("ABCD " + i);
		}
		assertNotNull("Socket service is null", socketService);
	}

	private void send(String data){
		Intent socketService = new Intent(super.getActivity(), SocketService.class);
		socketService.setAction(data);
		super.getActivity().startService(socketService);
	}

}
