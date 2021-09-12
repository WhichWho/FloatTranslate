package com.cnl.translate.utils;

public class PreferenceAnalyse {

    public static enum MODE {
        INPUT, TAP, AUTO
    }

    public static MODE getMode() {
        PreferenceQuery pq = PreferenceQuery.getInstance();
        if (pq == null) throw new RuntimeException("PreferenceQuery not init.");
        if (!pq.getBoolean("use_input")) {
            return MODE.INPUT;
        } else if (pq.getBoolean("use_auto")) {
            return MODE.AUTO;
        } else {
            return MODE.TAP;
        }
    }

}
