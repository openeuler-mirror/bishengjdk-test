# 描述
本项目用于测试JDK堆外内存占用情况
# 如何构建
使用maven如下命令进行构建，在`target`目录下生成jar包`CHeapUsage-1.0-SNAPSHOT-jar-with-dependencies.jar`。
<br>
执行：
`mvn clean package`
<br>
# 如何运行
传统JDK：
<br>
`java -jar ./target/CHeapUsage-1.0-SNAPSHOT-jar-with-dependencies.jar`
<br>
毕昇JDK(开启Glibc C堆裁剪)：
<br>
`java -XX:+UnlockExperimentalVMOptions  -XX:+GCTrimNativeHeap  -XX:GCTrimNativeHeapInterval=3 -jar ./target/CHeapUsage-1.0-SNAPSHOT-jar-with-dependencies.jar`
<br>
# 结果说明
```
Waiting 10s ...
`top -p $pid` of current process information:
   PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
 31235 ***       20   0   45.9g  93952  15488 S   0.0  0.0   0:00.46 java

```
<br>
打印信息为`top`命令查看当前进程的内存和CPU占用情况，其中`RES`为进程占用的实际物理内存（不包括swap out量）。注意：`RES`越小，表征程序实际占用内存越小。
<br>
