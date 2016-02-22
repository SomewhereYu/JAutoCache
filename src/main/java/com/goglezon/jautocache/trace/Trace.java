package com.goglezon.jautocache.trace;

/**
 * Created by xuxianjun on 2016/1/26.
 */
public interface Trace {
    /**
     * 开始跟踪
     *
     * @param info 跟踪数据
     * @return 跟踪数据
     */
    void begin(TraceInfo info);

    /**
     * 结束跟踪
     *
     * @param info 跟踪数据
     */
    void end(TraceInfo info);

    /**
     * 类型
     *
     * @return
     */
    String getType();

}
