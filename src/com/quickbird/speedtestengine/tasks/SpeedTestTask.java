package com.quickbird.speedtestengine.tasks;

import com.quickbird.enums.SpeedTestType;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersTransfer;
import com.quickbird.speedtestengine.TestTaskCallbacks;

public abstract class SpeedTestTask extends TestTask {
    protected static final String LOGTAG = "SpeedTestTask";
    private TestParametersTransfer mResult = null;
    private int mThreads = 4;

    public SpeedTestTask(TestTaskCallbacks paramTestTaskCallbacks, int paramInt) {
        super(paramTestTaskCallbacks);
        this.mThreads = paramInt;
    }

    @Override
    protected TestTask.Task[] createTasks() {
        TestTask.Task[] arrayOfTask = new TestTask.Task[this.mThreads];
        for (int i = 0; i < this.mThreads; i++)
            arrayOfTask[i] = getTaskInstance(i);
        return arrayOfTask;
    }

    @Override
    protected TestParameters getResult() {
        if (this.mResult == null)
            this.mResult = new TestParametersTransfer(getSpeedTestType(), this.mThreads);
        return this.mResult;
    }

    protected abstract SpeedTestType getSpeedTestType();

    protected abstract SpeedTask getTaskInstance(int paramInt);

    @Override
    protected void taskComplete(TestTask.Task paramTask) {
        super.taskComplete(paramTask);
    }

    @Override
    protected void taskStart(TestTask.Task paramTask) {
        super.taskStart(paramTask);
    }

    @Override
    protected void taskUpdate(TestTask.Task paramTask) {
        TestParametersTransfer localTestParametersTransfer1 = (TestParametersTransfer) paramTask.getResult();
        TestParametersTransfer localTestParametersTransfer2 = (TestParametersTransfer) getResult();
        localTestParametersTransfer2.setProgressAndBytes(
                paramTask.getThreadId(),
                localTestParametersTransfer1.getProgress(),
                localTestParametersTransfer1.getBytes());
        testUpdate(localTestParametersTransfer2);
    }

    protected abstract class SpeedTask extends TestTask.Task {
        public SpeedTask(int paramTestParametersTransfer, TestParametersTransfer arg3) {
            super(paramTestParametersTransfer, arg3);
        }
    }
}