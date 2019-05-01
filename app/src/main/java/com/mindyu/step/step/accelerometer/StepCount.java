package com.mindyu.step.step.accelerometer;

import android.util.Log;

/*
* 根据StepDetector传入的步点"数"步子
* */
public class StepCount implements StepCountListener {

    private int count = 0;
    private int mCount = 0;
    private StepValuePassListener mStepValuePassListener;
    private long timeOfLastPeak = 0;
    private long timeOfThisPeak = 0;
    private long averageTimeOfEveryStep = 0;
    private StepDetector stepDetector;

    public StepCount() {
        stepDetector = new StepDetector();
        stepDetector.initListener(this);
    }
    public StepDetector getStepDetector(){
        return stepDetector;
    }

    /*
        * 连续走十步才会开始计步
        * 连续走了9步以下,停留超过3秒,则计数清空
        * */
    @Override
    public void countStep() {
        this.timeOfLastPeak = this.timeOfThisPeak;
        this.timeOfThisPeak = System.currentTimeMillis();
        long diffValue = this.timeOfThisPeak - this.timeOfLastPeak;
        if (diffValue <= 3000L) {
            this.averageTimeOfEveryStep += diffValue;
            if (this.count < 9) {
                this.count++;
                resSomeValue();
            } else if (this.count == 9) {
                this.count++;
                this.mCount += this.count;

                this.averageTimeOfEveryStep = this.averageTimeOfEveryStep/10;
                Log.i("result","averageTimeOfEveryStep" + this.averageTimeOfEveryStep);
                if(this.averageTimeOfEveryStep <= 380){
                    Log.i("result","跑步");
                }else if(this.averageTimeOfEveryStep >= 450){
                    Log.i("result","走路");
                }else{
                    Log.i("result","快走");
                }
                notifyListener();
            } else {
                this.mCount++;
                resSomeValue();
                notifyListener();
            }
        } else {//超时
            this.count = 1;//为1,不是0
        }

    }

    private void resSomeValue(){
        this.count = 0;
        this.averageTimeOfEveryStep = 0;
    }


    public void initListener(StepValuePassListener listener) {
        this.mStepValuePassListener = listener;
    }

    public void notifyListener() {
        if (this.mStepValuePassListener != null)
            this.mStepValuePassListener.stepChanged(this.mCount);
    }


    public void setSteps(int initValue) {
        this.mCount = initValue;
        this.count = 0;
        timeOfLastPeak = 0;
        timeOfThisPeak = 0;
        notifyListener();
    }
}
