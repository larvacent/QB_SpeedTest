package com.quickbird.speedtestengine.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import android.os.SystemClock;

import com.quickbird.enums.SpeedTestError;
import com.quickbird.enums.SpeedTestType;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersLatency;
import com.quickbird.speedtestengine.TestTaskCallbacks;
import com.quickbird.speedtestengine.utils.DebugUtil;

public class LatencyTestTask extends TestTask {
	protected static final String LOGTAG = "LatencyTestTask";
	private int mConnectTimeout = 10000;
	private int mReadTimeout = 10000;
	private TestParametersLatency mResult = null;
	protected int mSampleCount = 5;

	public LatencyTestTask(TestTaskCallbacks paramTestTaskCallbacks) {
		super(paramTestTaskCallbacks);
	}

	private static int processLatency(URL paramURL, int mConnectTimeout, int mReadTimeout) throws IOException, SocketTimeoutException {
		URLConnection localURLConnection = paramURL.openConnection();
		localURLConnection.setUseCaches(false);
		localURLConnection.setDoInput(true);
		localURLConnection.setDoOutput(false);
		localURLConnection.setConnectTimeout(mConnectTimeout);
		localURLConnection.setReadTimeout(mReadTimeout);
		long l = SystemClock.uptimeMillis();
		BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream(), 1024);
		int i = 0;
		while (i < 32) {
			i += localBufferedInputStream.read();
		}
		localBufferedInputStream.close();
		return (int) (SystemClock.uptimeMillis() - l);
	}

	@Override
	protected TestTask.Task[] createTasks() {
		LatencyTask localLatencyTask = new LatencyTask(0,(TestParametersLatency) getResult());
		localLatencyTask.setConnectTimeout(this.mConnectTimeout);
		localLatencyTask.setReadTimeout(this.mReadTimeout);
		TestTask.Task[] arrayOfTask = new TestTask.Task[1];
		arrayOfTask[0] = localLatencyTask;
		return arrayOfTask;
	}

	protected float getProgress(int paramInt) {
		return paramInt / this.mSampleCount;
	}

	@Override
	protected TestParameters getResult() {
		if (this.mResult == null)
			this.mResult = new TestParametersLatency(SpeedTestType.Latency);
		return this.mResult;
	}

	public void setConnectTimeout(int paramInt) {
		this.mConnectTimeout = paramInt;
	}

	public void setReadTimeout(int paramInt) {
		this.mReadTimeout = paramInt;
	}

	public void setSampleCount(int paramInt) {
		this.mSampleCount = paramInt;
	}

	@Override
	protected void taskUpdate(TestTask.Task paramTask) {
		testUpdate(paramTask.getResult());
	}

	private class LatencyTask extends TestTask.Task {
		private int mConnectTimeout = 100000;
		private int mReadTimeout = 10000;

		public LatencyTask(int paramTestParametersLatency, TestParametersLatency arg3) {
			super(paramTestParametersLatency, arg3);
		}

		@Override
		protected TestParameters doInBackground(URL[] paramArrayOfURL) {
			TestParametersLatency localTestParametersLatency = (TestParametersLatency) getResult();
			int i = 0;
			int j = 0;
			setCompleted(false);
			int n = 1;
			int localPing;
			setStartTime(SystemClock.elapsedRealtime());
			for (int count = 0; count <= mSampleCount + 1; count++) {
				try {
					if (count >= LatencyTestTask.this.mSampleCount + 1) {
						setCompleted(true);
						DebugUtil.v(LOGTAG, "ping done");
						if ((n != 0) && (j > 0)) {
							LatencyTestTask.this.success();
							localTestParametersLatency.setSuccess(true);
							DebugUtil.v(LOGTAG, "ping result is :" + i / j);
							localTestParametersLatency.setPing(i / j);
							localTestParametersLatency.setProgress(1.0F);
							return localTestParametersLatency;
						}
					} else {
						localPing = LatencyTestTask.processLatency(paramArrayOfURL[0], this.mConnectTimeout, this.mReadTimeout);
						if (i == 0) {
							i = 1;
						} else {
							i += localPing;
							j++;
							DebugUtil.i(LOGTAG, "Ping: " + localPing);
							localTestParametersLatency.setProgress(LatencyTestTask.this.getProgress(count + 1));
							localTestParametersLatency.setPing(localPing);
							publishProgress(new Void[0]);
						}
					}
				} catch (SocketTimeoutException e) {
					n = 0;
					DebugUtil.e("LatencyTestTask" + e.getMessage());
					LatencyTestTask.this.setError(SpeedTestError.DEVICE_NOT_ONLINE);
					LatencyTestTask.this.failed(SpeedTestError.DEVICE_NOT_ONLINE);
					setCompleted(true);
				} catch (Exception localException1) {
					n = 0;
					DebugUtil.e("LatencyTestTask"+ localException1.getMessage());
					LatencyTestTask.this.setError(SpeedTestError.TEST_RUN);
					LatencyTestTask.this.failed(SpeedTestError.TEST_RUN);
					setCompleted(true);
				}
			}
			return localTestParametersLatency;
		}

		public void setConnectTimeout(int paramInt) {
			this.mConnectTimeout = paramInt;
		}

		public void setReadTimeout(int paramInt) {
			this.mReadTimeout = paramInt;
		}

	}
}
