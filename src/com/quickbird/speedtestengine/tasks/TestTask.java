package com.quickbird.speedtestengine.tasks;

import java.net.URL;

import android.os.AsyncTask;

import com.quickbird.enums.SpeedTestError;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestTaskCallbacks;

public abstract class TestTask {
    private TestTaskCallbacks mCallbacks = null;
    private SpeedTestError mError = SpeedTestError.None;
    private boolean mSuccess = true;
    private int mTaskStackCount = 0;
    protected Task[] mTasks = null;
    protected Runnable mTestCancelled = null;
    private int mTestLength = 10000;
    private URL mUrl = null;

    public TestTask(TestTaskCallbacks paramTestTaskCallbacks) {
        this.mCallbacks = paramTestTaskCallbacks;
    }

    private void processTasks() {
        this.mTaskStackCount = 0;
        if ((this.mTasks == null) || (this.mTasks.length <= 0))
            testComplete();
        else
            for (Task localTask : this.mTasks) {
                URL[] arrayOfURL = new URL[1];
                arrayOfURL[0] = this.mUrl;
                localTask.execute(arrayOfURL);
            }
    }

    public void cancel(boolean paramBoolean) {
        Task[] arrayOfTask = getTasks();
        int j = arrayOfTask.length;
        for (int i = 0; i < j; i++)
            arrayOfTask[i].onCancelled();
    }

    protected abstract Task[] createTasks();

    protected void failed(SpeedTestError paramSpeedTestError) {
        this.mSuccess = false;
        setError(paramSpeedTestError);
    }

    public SpeedTestError getError() {
        return this.mError;
    }

    public String getErrorMessage() {
        return this.mError.getEnglishText();
    }

    protected abstract TestParameters getResult();

    public boolean getSuccess() {
        return this.mSuccess;
    }

    public Task[] getTasks() {
        return this.mTasks;
    }

    public int getTestLength() {
        return this.mTestLength;
    }

    protected void setError(SpeedTestError paramSpeedTestError) {
        this.mError = paramSpeedTestError;
    }

    public void setTestLength(int paramInt) {
        this.mTestLength = paramInt;
    }

    protected void success() {
        this.mSuccess = true;
    }

    protected void taskComplete(Task paramTask) {
        this.mTaskStackCount = (-1 + this.mTaskStackCount);
        if (this.mTaskStackCount <= 0)
            testComplete();
    }

    protected void taskStart(Task paramTask) {
        this.mTaskStackCount = (1 + this.mTaskStackCount);
    }

    protected abstract void taskUpdate(Task paramTask);

    protected void testCancelled() {
    }

    protected void testComplete() {
        TestParameters localTestParameters = getResult();
        if (this.mError != SpeedTestError.None)
            this.mCallbacks.onTestFailed(this.mError, localTestParameters);
        else
            this.mCallbacks.onTestComplete(localTestParameters);
    }

    public void testStart(URL paramURL) {
        this.mCallbacks.onBeginTest();
        this.mUrl = paramURL;
        this.mTasks = createTasks();
        processTasks();
    }

    protected void testUpdate(TestParameters paramTestParameters) {
        TestTaskCallbacks localTestTaskCallbacks = this.mCallbacks;
        TestParameters[] arrayOfTestParameters = new TestParameters[1];
        arrayOfTestParameters[0] = paramTestParameters;
        localTestTaskCallbacks.onTestUpdate(arrayOfTestParameters);
    }

    public abstract class Task extends AsyncTask<URL, Void, TestParameters> {
        private boolean mCancelled = false;
        private boolean mCompleted = false;
        private long mStartTime = 0L;
        private TestParameters mTaskResult = null;
        private int mThreadId = 0;

        public Task(int threadId, TestParameters taskResult) {
            this.mThreadId = threadId;
            this.mTaskResult = taskResult;
        }

        @Override
        protected abstract TestParameters doInBackground(URL[] paramArrayOfURL);

        public boolean getCancelled() {
            return this.mCancelled;
        }

        public boolean getCompleted() {
            return this.mCompleted;
        }

        public TestParameters getResult() {
            return this.mTaskResult;
        }

        public long getStartTime() {
            return this.mStartTime;
        }

        public int getThreadId() {
            return this.mThreadId;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            setCancelled(true);
            TestTask.this.failed(SpeedTestError.TEST_CANCELLED);
        }

        @Override
        protected void onPostExecute(TestParameters paramTestParameters) {
            super.onPostExecute(paramTestParameters);
            TestTask.this.taskComplete(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TestTask.this.taskStart(this);
        }

        @Override
        protected void onProgressUpdate(Void[] paramArrayOfVoid) {
            super.onProgressUpdate(paramArrayOfVoid);
            TestTask.this.taskUpdate(this);
        }

        protected void setCancelled(boolean paramBoolean) {
            this.mCancelled = paramBoolean;
        }

        protected void setCompleted(boolean paramBoolean) {
            this.mCompleted = paramBoolean;
        }

        protected void setStartTime(long paramLong) {
            this.mStartTime = paramLong;
        }
    }
}
