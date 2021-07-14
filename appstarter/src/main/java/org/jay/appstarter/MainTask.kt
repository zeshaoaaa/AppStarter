package org.jay.appstarter;

// 主要任务
public abstract class MainTask extends Task {

    @Override
    public boolean runOnMainThread() {
        return true;
    }

}
