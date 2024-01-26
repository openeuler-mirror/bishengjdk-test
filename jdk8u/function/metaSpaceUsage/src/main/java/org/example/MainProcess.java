package org.example;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainProcess {

    public static void main(String[] args) throws Exception {


        String jdkPath = System.getProperty("test.jdk");
        if (jdkPath == null) {
            throw new RuntimeException(
                    "System property 'test.jdk' not set. Please set this property using '-Dtest.jdk" + "=/path/to/jdk'.");
        }


        ArrayList<String> startArgs = new ArrayList<>();
        startArgs.add(jdkPath + "/bin/java");
        startArgs.add("-Dsun.reflect.noInflation=true");
        startArgs.add("-Dsun.reflect.inflationThreshold=0");
        startArgs.add("-XX:SoftRefLRUPolicyMSPerMB=0");
        startArgs.add("-cp");
        startArgs.add(System.getProperty("java.class.path"));
        startArgs.add("org.example.WorkerProcess");


        // create worker process
        ProcessBuilder pbWorker = new ProcessBuilder(startArgs.toArray(new String[startArgs.size()]));
        pbWorker.redirectErrorStream(true);
        Process pWorker = pbWorker.start();

        BufferedReader buff = new BufferedReader(new InputStreamReader(pWorker.getInputStream()));
        String in = buff.readLine();

        String[] tmp = null;
        boolean executeEnd = false;
        int workProcessId = -1;
        while ( (in != null) && (executeEnd != true)) {
            in = buff.readLine();
            System.out.println("[Woker] " +  in);
            if(in.contains("[End]")){
                tmp = in.split("\\s");
                workProcessId = Integer.valueOf(tmp[2]).intValue();
                executeEnd = true;
            }
        }


        // create 'jcmd' process to check the size of metadata
        // jcmd <pid> VM.metaspace
        String result  = null;
        Process pJcmd = null;

        long used = 0L;
        long capacity = 0L;
        long committed =0L;

        if (workProcessId != -1) {
            ArrayList<String> jcmdArgs = new ArrayList<>();
            jcmdArgs.add(jdkPath + "/bin/jcmd");
            jcmdArgs.add(String.valueOf(workProcessId));
            jcmdArgs.add("GC.heap_info");

            ProcessBuilder jcmdProcess = new ProcessBuilder(jcmdArgs.toArray(new String[jcmdArgs.size()]));
            jcmdProcess.redirectErrorStream(true);
            pJcmd = jcmdProcess.start();


            List<String> out = new ArrayList<>();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pJcmd.getInputStream()));
            String ss = buf.readLine();

            while (ss != null) {
                System.out.println("[jcmd out]" + ss);

                if(ss.contains("Metaspace")) {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(ss);
                    List<Long> nums = new ArrayList<>();
                    while (matcher.find()) {
                        nums.add(Long.valueOf(matcher.group(0)));
                    }
                    capacity = nums.get(1).longValue();
                    committed = nums.get(2).longValue();
                }
                ss = buf.readLine();
            }
        } else {
            System.out.println("[Error] create worker process failed !");
            return ;
        }

        double ret = (double) capacity/committed * 100;
        System.out.println("[result] The memory of metaspace committed to os : committed = " +  committed + ", and capacity/committed = " + capacity + "/" + committed + " = " + ret + "%");
        pWorker.destroy();
        pJcmd.destroy();

    }
}
