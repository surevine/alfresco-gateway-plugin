package com.surevine.alfresco.gateway.mock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Locale;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;

//Only implements getContent(File)
public class MockSimpleStringContentReader implements ContentReader {

	private String _contentToWrite;
	
	public MockSimpleStringContentReader(String content) {
		_contentToWrite=content;
	}
	
	@Override
	public void getContent(File file) throws ContentIOException {
		try {
			OutputStream os = new FileOutputStream(file);
			os.write(_contentToWrite.getBytes(Charset.forName("UTF-8")));
			os.flush();
			os.close();
		}
		catch (IOException e) {
			throw new ContentIOException("Couldn't write mock file", e);
		}
	}
	
	//Everything under this line is auto-generated empty/null methods
	
	@Override
	public boolean isChannelOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addListener(ContentStreamListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ContentData getContentData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimetype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMimetype(String mimetype) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncoding(String encoding) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocale(Locale locale) {
		// TODO Auto-generated method stub

	}

	@Override
	public ContentReader getReader() throws ContentIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getLastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ReadableByteChannel getReadableChannel() throws ContentIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileChannel getFileChannel() throws ContentIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getContentInputStream() throws ContentIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getContent(OutputStream os) throws ContentIOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getContentString() throws ContentIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentString(int length) throws ContentIOException {
		// TODO Auto-generated method stub
		return null;
	}

}
