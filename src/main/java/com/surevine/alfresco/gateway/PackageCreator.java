package com.surevine.alfresco.gateway;

import org.alfresco.service.cmr.repository.NodeRef;

public interface PackageCreator {

	public GatewayPackage createPackage(NodeRef nr);
}
