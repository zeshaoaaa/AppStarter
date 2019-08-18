package org.jay.launchstarter;

public abstract class MainTask extends Task {

    @Override
    public boolean runOnMainThread() {
        return true;
    }

}
