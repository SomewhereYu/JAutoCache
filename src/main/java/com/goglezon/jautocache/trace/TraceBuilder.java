package com.goglezon.jautocache.trace;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 客户端性能统计插件构建器
 */
public class TraceBuilder implements Trace {
    // 性能插件
    private List<Trace> traces;


    public void start() {
        loadTrace();
    }


    protected void loadTrace() {

        if (traces == null) {
            // 加载插件
            ArrayList plugins = new ArrayList();
            ServiceLoader loader = ServiceLoader.load(Trace.class, Trace.class.getClassLoader());
            Iterator iterator = loader.iterator();

            while (iterator.hasNext()) {
                Trace plugin = (Trace) iterator.next();
                plugins.add(plugin);
            }
            traces = plugins;
        }
    }


    public void begin(final TraceInfo info) {
        if (info == null || traces == null || traces.isEmpty()) {
            return;
        }
        // 开始统计
        for (Trace trace : traces) {
            trace.begin(info);
        }
    }

    public void end(final TraceInfo info) {
        if (info == null || traces == null || traces.isEmpty()) {
            return;
        }
        // 结束统计
        for (Trace trace : traces) {
            trace.end(info);
        }
        info.clear();
    }


    public String getType() {
        return "builder";
    }

}
