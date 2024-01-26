package org.example;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;

import javax.tools.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkerProcess {
    static JavaCompiler compiler = null;
    static InMemoryJavaFileManager fileManager = null;
    static boolean compileSourceForClassName(String classname, int sizeFactor) {

        String code = SourceCodeGenerator.makeSource(sizeFactor).replaceAll("CLASSNAME", classname);

        InMemorySourceFile sourceFile = new InMemorySourceFile(classname, code);
        List<JavaFileObject> sourceFiles = new ArrayList<JavaFileObject>();
        sourceFiles.add(sourceFile);

        if (compiler == null) {
            compiler = ToolProvider.getSystemJavaCompiler();
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        if (fileManager == null) {
            fileManager = new InMemoryJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null));
        }

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, sourceFiles);

        boolean success = task.call();

        return success;
    }

    public static void execute() throws Exception {
        ReflectorFactory reflectorFactory1 = new DefaultReflectorFactory();
        ReflectorFactory reflectorFactory2 = new DefaultReflectorFactory();

        Reflector reflector1 = reflectorFactory1.findForClass(ModelA.class);
        Reflector reflector2 = reflectorFactory2.findForClass(ModelB.class);

        ModelA modelA = new ModelA();
        ModelB modelB = new ModelB();

        Object[] empty = {};
        Object[] one = {"a"};
        for (int j = 1; j <= 1000; j++) {
            reflector1.getSetInvoker("field" + j).invoke(modelA, one);
            reflector1.getGetInvoker("field" + j).invoke(modelA, empty);
            reflector2.getSetInvoker("field" + j).invoke(modelB, one);
            reflector2.getGetInvoker("field" + j).invoke(modelB, empty);
        }
        System.out.println("Invoke method 1000*4 times finished.");

        reflectorFactory1 = null;
        reflectorFactory2 = null;
        reflector1 = null;
        reflector2 = null;
        modelA = null;
        modelB = null;
        System.gc();

        int numSmallClasses = 4000;
        LinkedList<ClassLoader> smallLoaders = new LinkedList<ClassLoader>();

        // create and load n small classes in n classloaders
        for (int i = 0; i < numSmallClasses; i++) {
            String className = "smallclass" + i;
            if (compileSourceForClassName(className, 1)) {
                ClassLoader cl = fileManager.getClassLoader(null);
                smallLoaders.add(cl);
                Class<?> clazz = Class.forName(className, true, cl);
            }
        }
        System.out.println("Loaded " + numSmallClasses + " small classes.");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("[Begin] Process worker is running...");
        execute();
        System.out.println("[End] PID: " + getProcessID());
        System.out.println("[Waiting] ...");
        TimeUnit.SECONDS.sleep(120);
    }

    public static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])
                .intValue();
    }
}
