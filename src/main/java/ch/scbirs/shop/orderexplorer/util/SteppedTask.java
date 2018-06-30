package ch.scbirs.shop.orderexplorer.util;

public interface SteppedTask {

    void doStep() throws Exception;

    boolean isDone();

    int currentProgress();

    int maxProgress();
}
