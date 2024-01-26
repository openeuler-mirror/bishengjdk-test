package org.example;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryJavaFileManager extends javax.tools.ForwardingJavaFileManager<JavaFileManager> {

	private final Map<URI, InMemorySourceFile> sources = new HashMap<URI, InMemorySourceFile>();
	private final Map<URI, InMemoryClassFile> classes = new HashMap<URI, InMemoryClassFile>();

	public static URI makeURIforClass(String classname, Kind kind) {
		URI uri = null;
		try {
			uri = new URI("MEM:///" + classname.replace('.', '/') + kind.extension);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	private InMemorySourceFile getSourceFile(String classname, boolean createIfMissing) {
		URI uri = makeURIforClass(classname, Kind.SOURCE);
		InMemorySourceFile o = sources.get(uri);
		if (o == null && createIfMissing) {
			o = new InMemorySourceFile(classname, "");
			sources.put(uri, o);
		}
		return o;
	}

	private InMemoryClassFile getClassFile(String classname, boolean createIfMissing) {
		URI uri = makeURIforClass(classname, Kind.CLASS);
		InMemoryClassFile o = classes.get(uri);
		if (o == null && createIfMissing) {
			o = new InMemoryClassFile(classname);
			classes.put(uri, o);
		}
		return o;
	}

	public InMemoryJavaFileManager(JavaFileManager parent) {
		super(parent);
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location, String classname, Kind kind) throws IOException {
		JavaFileObject o = null;
		switch (kind) {
		case SOURCE: o = getSourceFile(classname, false); break;
		case CLASS: o = getClassFile(classname, false); break;
		default:
			break;
		}
		if (o == null) {
			o = super.getJavaFileForInput(location, classname, kind);
		}
		return o;

	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String classname, Kind kind, FileObject arg3)
			throws IOException {
		switch (kind) {
		case SOURCE: return getSourceFile(classname, true);
		case CLASS: return getClassFile(classname, true);
		default:
			return null;
		}
	}

	public class InMemoryClassLoader extends ClassLoader {
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			InMemoryClassFile o = getClassFile(name, false);
			if (o != null) {
				byte[] bytes = o.getBytes();
				return defineClass(name, bytes, 0, bytes.length);
			}
			return super.findClass(name);
		}
	}

	@Override
	public ClassLoader getClassLoader(Location arg0) {
		return new InMemoryClassLoader();
	}

}
