package com.surevine.alfresco.gateway;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.namespace.QName;
import org.json.simple.JSONObject;

public class GatewayPackage {

	public static final String PATH_KEY = "{gateway}:PATH"; // Special key in
															// the properties
															// map holding the
															// path of the node
	
	public static final String NAME_KEY = "{gateway}:NAME"; // Special key in
															//the properties
															// map holding the
															// name of the node

	private File _baseLocation;
	private boolean _isWritten = false;
	private File _packageFile;
	private UUID _uuid;
	private ContentReader _reader;
	private Map<QName, Serializable> _propertiesToExport;
	private List<File> _filesCreated = new ArrayList<File>();

	public GatewayPackage(File baseLocation, ContentReader reader, Map<QName, Serializable> propertiesToExport) {
		_baseLocation = baseLocation;
		_uuid = UUID.randomUUID();
		_reader = reader;
		_propertiesToExport = propertiesToExport;
	}

	public File getPackageFile() {
		if (!_isWritten) {
			writeToDisk();
		}
		return _packageFile;
	}

	protected synchronized void writeToDisk() {

		// Create the file structure we will use for this artefact
		File packageDir = new File(_baseLocation, _uuid.toString()); // Create
																		// directory
																		// for
																		// package
																		// file
		if (!packageDir.mkdirs()) {
			throw new GatewayException("Could not create the package directory: " + packageDir);
		}

		// Create the file object, note we haven't created the file itself yet
		_packageFile = new File(packageDir, _uuid.toString() + ".tar.gz");
		
		File gatewayMetadataFile = new File(packageDir, ".metadata.json");
		String name = _propertiesToExport.get(QName.createQName(NAME_KEY)).toString();
		if (name==null) {
			name=packageDir.getName();
		}
		File contentFile = new File(packageDir, name);
		
		// We've created our file structure, now let's write to it

		// Write the content to disk, closing underlying resources automatically
		_reader.getContent(contentFile); 

		try {
			createGatewayMetadataFile(gatewayMetadataFile);
		} catch (IOException e) {
			throw new GatewayException("Could not create the gateway metadata file: " + e, e);
		}

		// By this point, we have created and populated our file structure, so
		// now all we need to do is tar gz it up
		try {
			Runtime.getRuntime().exec(new String[] { "tar", "cvzf", _packageFile.toString(), gatewayMetadataFile.getName(), contentFile.getName() }, new String[] {}, packageDir).waitFor();
		} catch (IOException e) {
			throw new GatewayException("Could not create zipped tarball for gateway: " + e, e);
		} catch (InterruptedException e) {
			throw new GatewayException("Interrupted while attempting to create zipped tarball: " + e, e);
		}

		// Register temp dir for deletion, will be deleted in this order
		_filesCreated.add(contentFile);
		_filesCreated.add(gatewayMetadataFile);
		_filesCreated.add(_packageFile);
		_filesCreated.add(packageDir);


		_isWritten = true;
	}
	
	public void deleteFiles() {
		for (File f : _filesCreated) {
			f.delete();
		}
		_isWritten=false;
	}

	@SuppressWarnings("unchecked")
	// For org.json.JSONObject generics warnings
	protected void createGatewayMetadataFile(File file) throws IOException {
		JSONObject json = new JSONObject();

		// TODO: Extract security properties from ICONIC schema
		json.put("SOURCE_TYPE", "ALFRESCO");
		json.put("PATH", _propertiesToExport.get(QName.createQName(PATH_KEY)));
		for (QName qName : _propertiesToExport.keySet()) {
			json.put(qName.toString(), _propertiesToExport.get(qName));
		}
		
		OutputStream os = new FileOutputStream(file);
		os.write(json.toString().getBytes(Charset.forName("UTF-8")));
		os.flush();
		os.close();
	}

}