package com.surevine.alfresco.gateway;

import java.io.File;

import org.alfresco.service.cmr.repository.NodeRef;

public interface PackageCreator {

	public GatewayPackage createPackage(NodeRef nr);
	
	public void postPackage(GatewayPackage thePackage, File destination);
}
