package com.quickbird.speedtestengine.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import android.os.SystemClock;

import com.quickbird.enums.SpeedTestError;
import com.quickbird.enums.SpeedTestType;
import com.quickbird.speedtest.business.WebSite;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersLatency;
import com.quickbird.speedtestengine.TestTaskCallbacks;
import com.quickbird.speedtestengine.utils.DebugUtil;

public class LatencyTestTask extends TestTask {
	protected static final String LOGTAG = "LatencyTestTask";
	private int mConnectTimeout = 10000;
	private int mReadTimeout = 10000;
	private TestParametersLatency mResult = null;
	protected int mSampleCount = 3;

	public LatencyTestTask(TestTaskCallbacks paramTestTaskCallbacks) {
		super(paramTestTaskCallbacks);
	}
	
	private static WebSite processLatency(URL paramURL, int mConnectTimeout, int mReadTimeout) throws IOException, SocketTimeoutException {
		WebSite webSiteTest = new WebSite();
		URLConnection localURLConnection = paramURL.openConnection();
		localURLConnection.setUseCaches(false);
		localURLConnection.setDoInput(true);
		localURLConnection.setDoOutput(false);
		localURLConnection.setConnectTimeout(mConnectTimeout);
		localURLConnection.setReadTimeout(mReadTimeout);
		long l = SystemClock.uptimeMillis();
		BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream(), 262144);
		int totalByte = 0;
		int len = 0;
		while (totalByte < 262144) {
			len = localBufferedInputStream.read();
//			DebugUtil.d("len:" + len);
			if (len == -1)
				break;
			totalByte += len;
			if (totalByte >= 32 && webSiteTest.getPing() == -1) {
				webSiteTest.setPing((int) (SystemClock.uptimeMillis() - l));
				DebugUtil.d("Ping:" + webSiteTest.getPing());
			}
		}
		long tempTime = (SystemClock.uptimeMillis() - l);
		if (tempTime <= 0)
			tempTime = 1;
		webSiteTest.setSpeed((int) (1000 * totalByte / tempTime)); // 单位为B/s
		localBufferedInputStream.close();
		return webSiteTest;
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
		private int mConnectTimeout = 5000;
		private int mReadTimeout = 5000;

		public LatencyTask(int paramTestParametersLatency, TestParametersLatency arg3) {
			super(paramTestParametersLatency, arg3);
		}

		@Override
		protected TestParameters doInBackground(URL[] paramArrayOfURL) {
			TestParametersLatency localTestParametersLatency = (TestParametersLatency) getResult();
			setCompleted(false);
			setStartTime(SystemClock.elapsedRealtime());
			try {
				WebSite webSiteTest = new WebSite();
				webSiteTest = LatencyTestTask.processLatency(paramArrayOfURL[0],this.mConnectTimeout, this.mReadTimeout);
				while (true) {
					if (SystemClock.elapsedRealtime() - getStartTime() > 5000)
						break;
				}
				setCompleted(true);
				LatencyTestTask.this.success();
				localTestParametersLatency.setSuccess(true);
				localTestParametersLatency.setPing(webSiteTest.getPing());
				localTestParametersLatency.setSpeed(webSiteTest.getSpeed());
				localTestParametersLatency.setProgress(1.0F);
			} catch (SocketTimeoutException e) {
				DebugUtil.e("LatencyTestTask" + e.getMessage());
				LatencyTestTask.this.setError(SpeedTestError.DEVICE_NOT_ONLINE);
				LatencyTestTask.this.failed(SpeedTestError.DEVICE_NOT_ONLINE);
				setCompleted(true);
			} catch (Exception localException1) {
				DebugUtil.e("LatencyTestTask" + localException1.getMessage());
				LatencyTestTask.this.setError(SpeedTestError.TEST_RUN);
				LatencyTestTask.this.failed(SpeedTestError.TEST_RUN);
				setCompleted(true);
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
