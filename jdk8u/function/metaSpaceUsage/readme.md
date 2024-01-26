# 描述
本项目用于测试JDK的metaspace使用情况
# 如何构建
使用maven如下命令进行构建，在`target`目录下生成jar包`metaSpaceUsage-1.0-SNAPSHOT-jar-with-dependencies.jar`。
<br>
执行：
`mvn clean package`
<br>

# 如何运行
通过VM参数`-Dtest.jdk=/path/to/jdk`指定待测JDK的部署路径（注：非JRE的路径，需包含`jcmd`工具）。
<br>
执行命令：
`java  -Dtest.jdk=${JDK_PATH}  -jar ./target/metaSpaceUsage-1.0-SNAPSHOT-jar-with-dependencies.jar`
<br>

# 结果说明
```
[Woker] Invoke method 1000*4 times finished.
[Woker] Loaded 4000 small classes.
[Woker] [End] PID: 102499
[jcmd out]102499:
[jcmd out] PSYoungGen      total 1195520K, used 783078K [0x0000000580b00000, 0x00000005e8e00000, 0x0000000800000000)
[jcmd out]  eden space 848896K, 67% used [0x0000000580b00000,0x00000005a38422e0,0x00000005b4800000)
[jcmd out]  from space 346624K, 61% used [0x00000005b4800000,0x00000005c17775e0,0x00000005c9a80000)
[jcmd out]  to   space 355328K, 0% used [0x00000005d3300000,0x00000005d3300000,0x00000005e8e00000)
[jcmd out] ParOldGen       total 1752576K, used 438502K [0x0000000082000000, 0x00000000ecf80000, 0x0000000580b00000)
[jcmd out]  object space 1752576K, 25% used [0x0000000082000000,0x000000009cc39870,0x00000000ecf80000)
[jcmd out] Metaspace       used 21028K, capacity 37230K, committed 37376K, reserved 1077248K
[jcmd out]  class space    used 3560K, capacity 9358K, committed 9472K, reserved 1048576K
[result] The memory of metaspace committed to os : committed = 37376, and capacity/committed = 37230/37376 = 99.609375%
```
Worker：待测进程的执行4000次反射调用和加载4000个类
<br>
jcmd out：根据待测进程的pid， 执行`jcmd $PID GC.heap_info` 查看metaspace输出结果
<br>
result：解析metaspace的capacity和committed，其中committed表示向os提交的内存大小，此值越小表示实际申请的内存越少。`capacity/committed`比值表示metaspace的容量与向OS提交的内存的比值，比值越大表示申请的内存利用率越高。
<br>


