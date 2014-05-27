package com.surevine.alfresco.gateway;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

public class PackageCreatorImpl implements PackageCreator {
	
	private NodeService _nodeService;
	
	public void setNodeService(NodeService nodeService) {
		_nodeService=nodeService;
	}
	
	private ContentService _contentService;
	
	public void setContentService(ContentService contentService) {
		_contentService=contentService;
	}
	
	private PermissionService _permissionService;
	
	public void setPermissionService(PermissionService permissionService) {
		_permissionService=permissionService;
	}
	
	private File _workingDir = new File ("/tmp/alfresco_gateway_plugin");
	
	public void setWorkingDir(String workingDir) {
		_workingDir=new File (workingDir);
	}
	
	private Collection<QName> _exportProperties;
	
	public void setQNamesToExport(Collection<String> qNames) {
		_exportProperties = new ArrayList<QName>();
		for (String qName : qNames) {
			_exportProperties.add(QName.createQName(qName));
		}
	}
	
	protected Collection<QName> getQNamesToExport() {
		if (_exportProperties==null) {
			_exportProperties=getDefaultExportProperties();
		}
		return _exportProperties;
	}
	
	protected Collection<QName> getDefaultExportProperties() {
		Collection<QName> rV = new ArrayList<QName>();
		rV.add(ContentModel.PROP_DESCRIPTION);
		rV.add(ContentModel.PROP_NAME);
		rV.add(ContentModel.PROP_TITLE);
		return rV;
	}
	
	@Override
	public GatewayPackage createPackage(NodeRef nr) {
		ContentReader cr =_contentService.getReader(nr, ContentModel.PROP_CONTENT);
		String pathString = _nodeService.getPath(nr).toDisplayPath(_nodeService, _permissionService);
		Map<QName, Serializable> props = getPropertiesToExport(nr);
		props.put(QName.createQName(GatewayPackage.PATH_KEY), pathString); 		//Always include the path under the special key {gateway}:PATH
		return new GatewayPackage(_workingDir, cr, props);
	}
	
	/**
	 * Gets the properties to be exported as a map
	 * @param nr
	 * @return
	 */
	public Map<QName, Serializable> getPropertiesToExport(NodeRef nr) {
		Map<QName, Serializable> rV = new HashMap<QName, Serializable>();
		for (QName qName: getQNamesToExport()) {
			Serializable value = _nodeService.getProperty(nr, qName);
			if (value!=null) {
				rV.put(qName, value);
			}
		}
		return rV;
	}

}
