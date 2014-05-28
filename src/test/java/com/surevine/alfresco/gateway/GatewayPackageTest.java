package com.surevine.alfresco.gateway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.namespace.QName;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.surevine.alfresco.gateway.mock.MockSimpleStringContentReader;

public class GatewayPackageTest {

	private static File OUTPUT_DIR=new File("/tmp/alfresco_gateway_test");
	static {
		String outputDirFromProps=System.getProperty(GatewayPackageTest.class.getName().concat(".OUTPUT_DIR"));
		if (outputDirFromProps!=null) {
			OUTPUT_DIR=new File(outputDirFromProps);
		}
	}
	
	@Before
	public void init() {
		if (!(OUTPUT_DIR.exists() || OUTPUT_DIR.mkdirs())) {
			throw new RuntimeException("Couldn't instaniate output directory: "+OUTPUT_DIR);
		}
	}
	
	protected File createTempDir(String testName) {
		File rV = new File(OUTPUT_DIR, testName + "_"+(new Date().getTime()/1000l));
		rV.deleteOnExit();
		return rV;
	}
	
	protected Map<QName, Serializable> getTestProperties() {
		Map<QName, Serializable> rV = new HashMap<QName, Serializable>();
		rV.put(ContentModel.PROP_NAME, "My Test Name");
		rV.put(ContentModel.PROP_DESCRIPTION, "My Test Description");
		rV.put(ContentModel.PROP_TITLE, "My Test Title");
		rV.put(QName.createQName(GatewayPackage.PATH_KEY), "My Test Path");
		return rV;
	}
	
	@Test
	public void runsThroughWithoutException() {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		GatewayPackage thePackage = new GatewayPackage(createTempDir("runsThroughWithoutException"), cr, getTestProperties());
		thePackage.writeToDisk();
		thePackage.deleteFiles();
	}
	
	@Test
	public void checkGatewayMetadata() throws IOException {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir = createTempDir("checkGatewayMetadata");
		GatewayPackage gp = new GatewayPackage(tempDir, cr, getTestProperties());
		gp.writeToDisk();
		File metadataFile = new File(gp.getPackageFile().getParentFile(), ".metadata.json");
		Assert.assertTrue("Metadata file "+metadataFile+" does not exist", metadataFile.exists());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile)));
		String metaDataContent="";
		while (true) { //I know this is horrible, but it'll do for a unit test
			String line = br.readLine();
			if (line==null) {
				break;
			}
			metaDataContent+=line;
		}
		Assert.assertEquals("Contents of metadata file incorrect", metaDataContent.trim(), "{\"SOURCE_TYPE\":\"ALFRESCO\",\"PATH\":\"My Test Path\"}");
		gp.deleteFiles();
	}
	
	@Test
	public void checkTempFilesDeleted() {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir=createTempDir("checkTempFilesDeleted");
		GatewayPackage thePackage = new GatewayPackage(tempDir, cr, getTestProperties());
		thePackage.writeToDisk();
		thePackage.deleteFiles();
		Assert.assertFalse("Temporary files not all deleted at "+tempDir, tempDir.listFiles().length>0);
	}
	
	@Test
	public void checkAlfrescoMetadata() throws IOException {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir = createTempDir("checkAlfrescoMetadata");
		GatewayPackage gp = new GatewayPackage(tempDir, cr, getTestProperties());
		gp.writeToDisk();
		File metadataFile = new File(gp.getPackageFile().getParent()+"/_contents", "alfresco_metadata");
		Assert.assertTrue("Metadata file "+metadataFile+" does not exist", metadataFile.exists());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile)));
		String metaDataContent="";
		while (true) { //I know this is horrible, but it'll do for a unit test
			String line = br.readLine();
			if (line==null) {
				break;
			}
			metaDataContent+=line;
		}
		Assert.assertEquals("Contents of metadata file incorrect", metaDataContent.trim(), "{\"{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}name\":\"My Test Name\",\"{gateway}:PATH\":\"My Test Path\",\"{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}title\":\"My Test Title\",\"{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}description\":\"My Test Description\"}");
		gp.deleteFiles();
	}
	
	@Test
	public void checkContent() throws IOException {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir = createTempDir("checkAlfrescoMetadata");
		GatewayPackage gp = new GatewayPackage(tempDir, cr, getTestProperties());
		gp.writeToDisk();
		File metadataFile = new File(gp.getPackageFile().getParent()+"/_contents", "content");
		Assert.assertTrue("Metadata file "+metadataFile+" does not exist", metadataFile.exists());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile)));
		String metaDataContent="";
		while (true) { //I know this is horrible, but it'll do for a unit test
			String line = br.readLine();
			if (line==null) {
				break;
			}
			metaDataContent+=line;
		}
		Assert.assertEquals("Contents of content file incorrect", metaDataContent.trim(), "Alas, poor Yorrick, I knew him well");
		gp.deleteFiles();
	}
	
	@Test
	public void checkContentFromGzip() throws IOException, InterruptedException {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir = createTempDir("checkContentFromGzip");
		GatewayPackage gp = new GatewayPackage(tempDir, cr, getTestProperties());
		gp.writeToDisk();
		File extractedDir=new File(gp.getPackageFile().getParentFile(), "extracted");
		extractedDir.mkdir();
		File toExamine = new File(extractedDir.toString()+"/_contents/content");

		Runtime.getRuntime().exec(new String[] { 	"tar", 
													"--include",
													"_contents/content",
													"-C",
													extractedDir.toString(),
													"-xzf",
													gp.getPackageFile().toString()
												}, new String[] {}, tempDir).waitFor();
		
		Assert.assertTrue("Metadata file "+toExamine+" does not exist", toExamine.exists());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(toExamine)));
		String metaDataContent="";
		while (true) { //I know this is horrible, but it'll do for a unit test
			String line = br.readLine();
			if (line==null) {
				break;
			}
			metaDataContent+=line;
		}
		Runtime.getRuntime().exec(new String[] { 	"rm", 
				"-rf",
				tempDir.toString(),
			}, new String[] {}, tempDir).waitFor();
		
		Assert.assertEquals("Contents of content file incorrect", metaDataContent.trim(), "Alas, poor Yorrick, I knew him well");
	}
	
	@Test
	public void checkGatewayMetadataFromGzip() throws IOException, InterruptedException {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir = createTempDir("checkGatewayMetadataFromGzip");
		GatewayPackage gp = new GatewayPackage(tempDir, cr, getTestProperties());
		gp.writeToDisk();
		File extractedDir=new File(gp.getPackageFile().getParentFile(), "extracted");
		extractedDir.mkdir();
		File toExamine = new File(extractedDir.toString()+"/.metadata.json");
		
		Runtime.getRuntime().exec(new String[] { 	"tar", 
													"--include",
													".metadata.json",
													"-C",
													extractedDir.toString(),
													"-xzf",
													gp.getPackageFile().toString()
												}, new String[] {}, tempDir).waitFor();
		
		Assert.assertTrue("Metadata file "+toExamine+" does not exist", toExamine.exists());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(toExamine)));
		String metaDataContent="";
		while (true) { //I know this is horrible, but it'll do for a unit test
			String line = br.readLine();
			if (line==null) {
				break;
			}
			metaDataContent+=line;
		}
		Runtime.getRuntime().exec(new String[] { 	"rm", 
				"-rf",
				tempDir.toString(),
			}, new String[] {}, tempDir).waitFor();
		
		Assert.assertEquals("Contents of content file incorrect", metaDataContent.trim(), "{\"SOURCE_TYPE\":\"ALFRESCO\",\"PATH\":\"My Test Path\"}");
	}

	@Test
	public void checkAlfrescoMetadataFromGzip() throws IOException, InterruptedException {
		ContentReader cr = new MockSimpleStringContentReader("Alas, poor Yorrick, I knew him well");
		File tempDir = createTempDir("checkGatewayMetadataFromGzip");
		GatewayPackage gp = new GatewayPackage(tempDir, cr, getTestProperties());
		gp.writeToDisk();
		File extractedDir=new File(gp.getPackageFile().getParentFile(), "extracted");
		extractedDir.mkdir();
		File toExamine = new File(extractedDir.toString()+"/_contents/alfresco_metadata");
		
		Runtime.getRuntime().exec(new String[] { 	"tar", 
													"--include",
													"_contents/alfresco_metadata",
													"-C",
													extractedDir.toString(),
													"-xzf",
													gp.getPackageFile().toString()
												}, new String[] {}, tempDir).waitFor();
		
		Assert.assertTrue("Metadata file "+toExamine+" does not exist", toExamine.exists());
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(toExamine)));
		String metaDataContent="";
		while (true) { //I know this is horrible, but it'll do for a unit test
			String line = br.readLine();
			if (line==null) {
				break;
			}
			metaDataContent+=line;
		}
		Runtime.getRuntime().exec(new String[] { 	"rm", 
				"-rf",
				tempDir.toString(),
			}, new String[] {}, tempDir).waitFor();
		
		Assert.assertEquals("Contents of metadata file incorrect", metaDataContent.trim(), "{\"{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}name\":\"My Test Name\",\"{gateway}:PATH\":\"My Test Path\",\"{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}title\":\"My Test Title\",\"{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}description\":\"My Test Description\"}");
	}
	
	@AfterClass
	public static void shutdown() {
		OUTPUT_DIR.delete();
	}
}
