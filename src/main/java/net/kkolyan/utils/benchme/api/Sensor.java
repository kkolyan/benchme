package net.kkolyan.utils.benchme.api;

public interface Sensor {

    /**
     *
     * @param signalIndex {@link Signal#index()}
     */
    void onEventSignal(int signalIndex);
}
