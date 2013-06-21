package com.quickbird.speedtestengine;

public class TestWebSite {
	private int ping;
	private int speed;

	public TestWebSite() {
		reset();
	}

	private void reset() {
		ping = -1;
		speed = -1;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
