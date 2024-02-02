package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class DirectBufferDemo {

    static CountDownLatch cdl = new CountDownLatch(10);

    private static String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    public static void printProcessInfo() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("linux")) {
            System.out.println("Please run this test on Linux platform.");
            return;
        }

        String pid = getPid();
        System.out.println("`top -p $pid` of current process information: ");
        String command = "top -b -n 1 -p " + pid  + " | grep -B 1 " + pid;
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
        process.destroy();
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        int num = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(num);

        for (int i=0; i< num; i++) {
            executorService.submit(new Thread(new Runnable() {
                @Override
                public void run() {

                    ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
                    ByteBuf directBuffer = allocator.directBuffer(1024);
                    directBuffer.writeBytes("Hello, Netty!".getBytes());
                    byte[] bytes = new byte[directBuffer.readableBytes()];
                    directBuffer.readBytes(bytes);
                    directBuffer.release();
                    cdl.countDown();
                }
            }));
        }

        cdl.await();
        executorService.shutdown();
        System.out.println("Waiting 10s ...");
        TimeUnit.SECONDS.sleep(10);
        printProcessInfo();
    }
}