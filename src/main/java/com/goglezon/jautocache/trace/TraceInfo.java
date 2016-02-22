package com.goglezon.jautocache.trace;

import java.util.HashMap;
import java.util.Map;

/**
 * 方法跟踪信息
 * Created by xuxianjun on 2016/1/26.
 */
public class TraceInfo {


    // 成功标示
    private boolean success = true;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    // 开始时间
    private long startTime;
    // 结束时间
    private long endTime;

    // 绑定每种性能统计插件的数据对象
    private Map<Object, Object> targets = new HashMap<Object, Object>(3);

    public TraceInfo(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取绑定的对象
     *
     * @param key 键
     * @return 值
     */
    public Object get(final Object key) {
        if (key == null) {
            return null;
        }
        return targets.get(key);
    }

    /**
     * 绑定对象
     *
     * @param key   键
     * @param value 值
     */
    public void put(final Object key, final Object value) {
        if (key != null && value != null) {
            targets.put(key, value);
        }
    }

    /**
     * 清理
     */
    public void clear() {
        startTime = 0;
        endTime = 0;
        targets.clear();
        success = true;
        className = null;
        methodName = null;
    }

    /**
     * 失败
     */
    public TraceInfo error() {

        endTime = System.currentTimeMillis();
        success = false;
        return this;
    }

}
