package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.tools.SimpleJavaFileObject;

public class InMemoryClassFile extends SimpleJavaFileObject {
	String name;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	public InMemoryClassFile(String className) {
		super(InMemoryJavaFileManager.makeURIforClass(className, Kind.CLASS), Kind.CLASS);
		name = className;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return byteArrayOutputStream;
	}

	public byte[] getBytes() {
		return byteArrayOutputStream.toByteArray();
	}

}
