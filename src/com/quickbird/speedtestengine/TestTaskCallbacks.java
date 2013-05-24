package com.quickbird.speedtestengine;

import com.quickbird.enums.SpeedTestError;

public abstract interface TestTaskCallbacks
{
  public abstract void onBeginTest();

  public abstract void onTestComplete(TestParameters paramTestParameters);

  public abstract void onTestFailed(SpeedTestError paramSpeedTestError, TestParameters paramTestParameters);

  public abstract void onTestUpdate(TestParameters[] paramArrayOfTestParameters);
}
