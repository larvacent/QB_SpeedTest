package com.quickbird.speedtestengine.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.os.SystemClock;

import com.quickbird.enums.SpeedTestError;
import com.quickbird.enums.SpeedTestType;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersTransfer;
import com.quickbird.speedtestengine.TestTaskCallbacks;
import com.quickbird.speedtestengine.utils.DebugUtil;

public class DownloadTestTask extends SpeedTestTask {
    protected static final String LOGTAG = "DownloadTestTask";
    protected static final double MAXSIZE = 2513694.0D;

    public DownloadTestTask(TestTaskCallbacks paramTestTaskCallbacks, int paramInt) {
        super(paramTestTaskCallbacks, paramInt);
    }

    @Override
    protected SpeedTestType getSpeedTestType() {
        return SpeedTestType.Download;
    }

    @Override
    protected SpeedTestTask.SpeedTask getTaskInstance(int paramInt) {
        return new DownloadSpeedTask(paramInt, new TestParametersTransfer(SpeedTestType.Download));
    }

    protected class DownloadSpeedTask extends SpeedTestTask.SpeedTask {
        private int mTestLength = 20000;

        public DownloadSpeedTask(int paramTestParametersTransfer, TestParametersTransfer arg3) {
            super(paramTestParametersTransfer, arg3);
        }

        private void processDownload(URL paramURL, TestParametersTransfer paramTestParametersTransfer) {
            int totalByte = 0;
            try {
                byte[] arrayOfByte = new byte[262144];
                URLConnection localURLConnection = paramURL.openConnection();
                localURLConnection.setUseCaches(false);
                localURLConnection.setDoInput(true);
                localURLConnection.setDoOutput(false);
                localURLConnection.setConnectTimeout(this.mTestLength);
                localURLConnection.setReadTimeout(this.mTestLength);
                paramTestParametersTransfer.clearBytes();
                paramTestParametersTransfer.clearProgress();
                publishProgress(new Void[0]);
                BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream(), 262144);
                int k = 0;
                long t3 = 0L;
                long t1 = 150L;// 控制多长时间获取一次数据
                while (true) {
                    if ((k != 0) || (getCancelled()) || (getCompleted())) {
                        localBufferedInputStream.close();
                        break;
                    }
                    int tempByte = localBufferedInputStream.read(arrayOfByte);
                    totalByte += tempByte;
                    long t2 = SystemClock.uptimeMillis();
                    if ((tempByte == 0) && (t3 > 0L) && (t2 < 200L + t3))
                        continue;
                    if (t2 > t3 + t1) {
                        paramTestParametersTransfer.setProgress(getProgress(totalByte));
                        paramTestParametersTransfer.setBytes(totalByte);
                        publishProgress(new Void[0]);
                        t3 = t2;
                        t1 = 30L;
                    }
                    if (tempByte != -1)
                        continue;
                    k = 1;
                }
            } catch (IOException localIOException) {
                DebugUtil.e("DownloadTestTask", "Download test IO failed:" + localIOException);
                DownloadTestTask.this.setError(SpeedTestError.TEST_RUN_IO);
            } catch (Exception localException) {
                DebugUtil.e("DownloadTestTask", "Download test failed" + localException);
                DownloadTestTask.this.setError(SpeedTestError.TEST_RUN);
            }
        }

        @Override
        protected TestParameters doInBackground(URL[] paramArrayOfURL) {
            TestParametersTransfer localTestParametersTransfer = (TestParametersTransfer) getResult();
            try {
                DownloadTestTask.this.setError(SpeedTestError.None);
                setStartTime(SystemClock.uptimeMillis());
                processDownload(paramArrayOfURL[0], localTestParametersTransfer);
                if (DownloadTestTask.this.getError() == SpeedTestError.None) {
                    localTestParametersTransfer.setSuccess(true);
                    DownloadTestTask.this.success();
                    setCompleted(true);
                    return localTestParametersTransfer;
                }else{
                    localTestParametersTransfer.setSuccess(false);
                    DownloadTestTask.this.failed(DownloadTestTask.this.getError());
                }
            } catch (Exception localException) {
                while (true) {
                    DebugUtil.e("DownloadTestTask" + localException.getMessage());
                    DownloadTestTask.this.setError(SpeedTestError.TEST_RUN);
                    localTestParametersTransfer.setSuccess(false);
                    DownloadTestTask.this.failed(DownloadTestTask.this.getError());
                    break;
                }
            }
            return localTestParametersTransfer;
        }

        protected float getProgress(int paramInt) {
            double d2 = paramInt / MAXSIZE;
            double d1 = (int) (SystemClock.uptimeMillis() - getStartTime()) / this.mTestLength;
            if (d1 >= 1.0D){
                setCompleted(true);
            }
            return (float) Math.max(d2, d1);
        }
    }
}
