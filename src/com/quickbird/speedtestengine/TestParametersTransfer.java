package com.quickbird.speedtestengine;

import java.util.Arrays;

import android.os.SystemClock;

import com.quickbird.enums.SpeedTestType;
import com.quickbird.speedtestengine.utils.DebugUtil;

public class TestParametersTransfer extends TestParameters {
    private int mSpeed;
    private SpeedTestAlgorithm mSpeedTestAlgorithm = null;
    private boolean mSpeedTestAlgorithmInitialized = false;
    private int[] mThreadBytes;
    private int mThreadCount;
    private int readLength = 1;

    public TestParametersTransfer(SpeedTestType paramSpeedTestType) {
        this(paramSpeedTestType, 1);
    }
    
    public TestParametersTransfer(SpeedTestType paramSpeedTestType, int paramInt) {
        super(paramSpeedTestType, paramInt);
        this.mThreadCount = paramInt;
        this.mSpeed = 0;
        this.mThreadBytes = new int[this.mThreadCount];
        clearBytes();
    }

    private void calculateSpeed() {
        if (!this.mSpeedTestAlgorithmInitialized) {
            this.mSpeedTestAlgorithmInitialized = true;
            this.mSpeedTestAlgorithm = new SpeedTestAlgorithm();
        }
        this.mSpeed = this.mSpeedTestAlgorithm.getSpeed(getProgress(), getBytes());
    }
    
    public void clearBytes() {
        for (int i = 0; i < this.mThreadCount; i++)
            this.mThreadBytes[i] = 0;
    }
    
    public void setReadLength(int readLength)
    {
    	this.readLength = readLength;
    }
    
    public int getReadLength()
    {
    	return this.readLength;
    }

    public int getBytes() {
        int i = 0;
        for (int j = 0; j < this.mThreadCount; j++)
            i += this.mThreadBytes[j];
        return i;
    }

    public int getSpeed() {
        return this.mSpeed;
    }

    public void setBytes(int paramInt) {
        if (this.mThreadCount > 0) {
            setBytes(this.mThreadCount - 1, paramInt);
//            calculateSpeed();
        }
    }

    protected void setBytes(int paramInt1, int paramInt2) {
        this.mThreadBytes[paramInt1] = paramInt2;
    }

    public void setProgressAndBytes(int paramInt1, float paramFloat,
            int paramInt2) {
        setProgress(paramInt1, paramFloat);
        setBytes(paramInt1, paramInt2);
//        calculateSpeed();
    }

    private class SpeedTestAlgorithm {
        private static final String LOGTAG = "SpeedTestAlgorithm";
        private int mCurrentIndex = -1;
        private int[] mSamples = new int[22];
        private TestParametersTransfer.SpeedTestReading[] mSpeeds = new TestParametersTransfer.SpeedTestReading[22];

        public SpeedTestAlgorithm() {
        	
        }
        
//        private int getFirstSpeed(float fProgress, int paramInt) {
//            int j = 0;
//            if (fProgress != 0.0F) {
//                TestParametersTransfer.SpeedTestReading localSpeedTestReading1 = this.mSpeeds[0];
//                long l = SystemClock.uptimeMillis();
//                double d2 = 1000.0D * paramInt
//                        / (1.0D + (l - localSpeedTestReading1.getTime()));
//                int i = 0;
//                if (this.mCurrentIndex < 4) {
//                    i = (int) d2;
//                } else {
//                    TestParametersTransfer.SpeedTestReading localSpeedTestReading2 = this.mSpeeds[2];
//                    double d1 = 1000.0D
//                            * (paramInt - Integer.valueOf(
//                                    localSpeedTestReading2.getRbytes())
//                                    .intValue())
//                            / (1.0D + (i - localSpeedTestReading2.getTime()));
//                    if (fProgress <= 0.5D)
//                        j = 2 * (int) (d2 * (0.5D - fProgress) + d1 * fProgress);
//                    else
//                        j = j;
//                }
//            } else {
//                j = 0;
//            }
//            return j;
//        }

        private int getFirstSpeed(float fProgress, int rByte) {
            int firestSpeed = 0;
            if (fProgress != 0.0F) {
                TestParametersTransfer.SpeedTestReading localSpeedTestReading1 = this.mSpeeds[0];
                long l = SystemClock.uptimeMillis();
                double d2 = 1000.0D * rByte/ ((l - localSpeedTestReading1.getTime()));
                if (this.mCurrentIndex < 3) {
                    firestSpeed = (int) d2;
//                    DebugUtil.d("d2:"+d2);
                } else {
                    TestParametersTransfer.SpeedTestReading localSpeedTestReading2 = this.mSpeeds[2];
                    double d1 = 1000.0D* (rByte - Integer.valueOf(localSpeedTestReading2.getRbytes()).intValue()) / ((l - localSpeedTestReading2.getTime()));
                    DebugUtil.d("d1:"+d1);
//                    if (fProgress <= 0.3D){
                        firestSpeed = 2 * (int) (d2 * (0.5D - fProgress) + d1 * fProgress);
//                    }
//                    else
//                        firestSpeed = firestSpeed;
				}
			} else {
				firestSpeed = 0;
			}
//            DebugUtil.d("firestSpeed:"+firestSpeed);
            return firestSpeed;
        }

        /**
         * 获取第五个值后的最大速度
         * 
         * @return
         */
        private int getSuperSpeed() {
            int i = 0;
            if (this.mCurrentIndex >= 3) {
                int m = 1 + this.mCurrentIndex;
                for (int j = 0; j < m; j++)
                    for (int k = j + 1; k < m; k++) {
                        TestParametersTransfer.SpeedTestReading localSpeedTestReading2 = this.mSpeeds[j];
                        TestParametersTransfer.SpeedTestReading localSpeedTestReading1 = this.mSpeeds[k];
                        float value = Float.valueOf(localSpeedTestReading1.getProgress()).floatValue() - Float.valueOf(localSpeedTestReading2.getProgress()).floatValue();
                        if (value < 0.3D) {
                            continue;
                        }
                        double d = Long.valueOf(localSpeedTestReading1.getTime()).longValue() - Long.valueOf(localSpeedTestReading2.getTime()) .longValue();
                        int n = (int) Math.round(1000.0D *  (Integer.valueOf( localSpeedTestReading1.getRbytes()).intValue() - Integer.valueOf(localSpeedTestReading2.getRbytes()) .intValue()) / d);
                        if (n <= i)
                            continue;
                        i = n;
                    }
            }
            return i;
        }
        
        protected int getSpeed(float progress, int rBytes) {
            int speed = 0;
            float fProgress = Math.min(1.0F, progress);
            long t1 = SystemClock.uptimeMillis();
            TestParametersTransfer.SpeedTestReading localSpeedTestReading1;
            if (this.mCurrentIndex != -1)
                localSpeedTestReading1 = this.mSpeeds[this.mCurrentIndex];
            else
                localSpeedTestReading1 = new TestParametersTransfer.SpeedTestReading();
            if ((Math.ceil(20.0F * fProgress) >= this.mCurrentIndex && (fProgress > 0.04F + Float.valueOf(localSpeedTestReading1.getProgress()).floatValue()))) {
                TestParametersTransfer.SpeedTestReading localSpeedTestReading2 = new TestParametersTransfer.SpeedTestReading(fProgress, t1, rBytes);
                TestParametersTransfer.SpeedTestReading[] arrayOfSpeedTestReading = this.mSpeeds;
                this.mCurrentIndex ++;
                arrayOfSpeedTestReading[this.mCurrentIndex] = localSpeedTestReading2;
                if (fProgress > 0.0F) {
                    long l2 = t1 - Long.valueOf(localSpeedTestReading1.getTime()).longValue();
                    if (l2 == 0L)
                        speed = 0;
                    else
                        speed = Math.round(1000 * (rBytes - Integer.valueOf(localSpeedTestReading1.getRbytes()).intValue()) / l2);
                    this.mSamples[this.mCurrentIndex] = speed;
                    DebugUtil.d("mCurrentIndex:"+mCurrentIndex);
                    DebugUtil.d("speed:"+speed/1000);
                    Arrays.sort(this.mSamples);
                }
            }
            int superSpeed = getSuperSpeed(); // 最大速度
            int firstSpeed = getFirstSpeed(fProgress, rBytes); // 最小速度
//            speed = firstSpeed;
            if ((superSpeed <= 0) || (superSpeed <= firstSpeed))
                speed = firstSpeed;
            else
                speed = (int) (fProgress * superSpeed + (1.0D - fProgress) * firstSpeed);
            DebugUtil.d("superSpeed:"+superSpeed);
            DebugUtil.d("firstSpeed:"+firstSpeed);
            DebugUtil.d("speed:"+speed);
            return speed;
        }
    }

    public class SpeedTestReading {
        private float mProgress;
        private int mRbytes;
        private long mTime;

        public SpeedTestReading() {
            this.mProgress = (1.0F / -1.0F);
            this.mTime = 0L;
            this.mRbytes = 0;
        }

        public SpeedTestReading(float paramLong, long time, int rbytes) {
            this.mProgress = paramLong;
            this.mTime = time;
            this.mRbytes = rbytes;
        }

        public float getProgress() {
            return this.mProgress;
        }

        public int getRbytes() {
            return this.mRbytes;
        }

        public long getTime() {
            return this.mTime;
        }

        public void setProgress(float paramFloat) {
            this.mProgress = paramFloat;
        }

        public void setRbytes(int paramInt) {
            this.mRbytes = paramInt;
        }

        public void setTime(long paramLong) {
            this.mTime = paramLong;
        }
    }
}
