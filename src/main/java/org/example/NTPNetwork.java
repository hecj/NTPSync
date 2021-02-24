package org.example;

public class NTPNetwork {

    long t1,t2,t3,t4;
    public long getT1() {
        return t1;
    }

    public void setT1(long t1) {
        this.t1 = t1;
    }

    public long getT2() {
        return t2;
    }

    public void setT2(long t2) {
        this.t2 = t2;
    }

    public long getT3() {
        return t3;
    }

    public void setT3(long t3) {
        this.t3 = t3;
    }

    public long getT4() {
        return t4;
    }

    public void setT4(long t4) {
        this.t4 = t4;
    }
    // 网络传输时间
    public long getDelayTime() {
        return (t2 - t1) + (t4 - t3);
    }

    // 客户端相对服务端时间
    public double getIntervalTime() {
        return 0.5 * (t2 - t1 + t3 - t4);
    }

    public double getAccuracyMin(){
        return getIntervalTime() - (getDelayTime() / 2);
    }

    /**
     * Calculates the max accuracy of this measurement
     * @return max accuracy
     */
    public double getAccuracyMax(){
        return getIntervalTime() + (getDelayTime()/ 2);
    }
}
