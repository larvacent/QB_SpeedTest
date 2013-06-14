package com.quickbird.speedtestengine;

import com.quickbird.enums.SpeedTestType;

public class TestParametersLatency extends TestParameters {
	private int mMaxPing = -1;
	private int mMinPing = -1;
	private int mPing = -1;
	private int speed = -1;

	public TestParametersLatency(SpeedTestType paramSpeedTestType) {
		super(paramSpeedTestType);
	}

	public int getMaxPing() {
		return this.mMaxPing;
	}

	public int getMinPing() {
		return this.mMinPing;
	}

	public int getPing() {
		return this.mPing;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setPing(int paramInt) {
		this.mPing = paramInt;
		if ((this.mMaxPing == -1) || (this.mMaxPing < paramInt))
			this.mMaxPing = paramInt;
		if ((this.mMinPing == -1) || (this.mMinPing > paramInt))
			this.mMinPing = paramInt;
	}
}
