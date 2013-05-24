package com.quickbird.speedtestengine;

import com.quickbird.enums.SpeedTestType;


public class TestParameters
{
  private float[] mProgress;
  private boolean mSuccess = false;
  private int mThreadCount = 0;
  private SpeedTestType mType;

  public TestParameters(SpeedTestType paramSpeedTestType)
  {
    this(paramSpeedTestType, 1);
  }

  public TestParameters(SpeedTestType paramSpeedTestType, int paramInt)
  {
    this.mThreadCount = paramInt;
    this.mType = paramSpeedTestType;
    this.mProgress = new float[paramInt];
    for (int i = 0; i < paramInt; i++)
      this.mProgress[i] = 0.0F;
  }

  public void clearProgress()
  {
    for (int i = 0; i < this.mThreadCount; i++)
      this.mProgress[i] = 0.0F;
  }

  public float getProgress()
  {
    float f = 0.0F;
    if (this.mThreadCount > f)
    {
      f = 0.0F;
      for (int i = 0; i < this.mThreadCount; i++)
        f += this.mProgress[i];
      f /= this.mThreadCount;
    }
    return f;
  }

  public SpeedTestType getType()
  {
    return this.mType;
  }

  public boolean isSuccess()
  {
    return this.mSuccess;
  }

  public void setProgress(float paramFloat)
  {
    if (this.mThreadCount > 0)
      setProgress(0, paramFloat);
  }

  public void setProgress(int paramInt, float paramFloat)
  {
    this.mProgress[paramInt] = paramFloat;
  }

  public void setSuccess(boolean paramBoolean)
  {
    this.mSuccess = paramBoolean;
  }
}
