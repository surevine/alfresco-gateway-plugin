package com.surevine.alfresco.gateway;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

public class GatewayPackagingAction extends ActionExecuterAbstractBase {
	
	private PackageCreator _packageCreator;
	
	public void setPackageCreator(PackageCreator pc) {
		_packageCreator=pc;
	}
	
	private File _destination= new File("/tmp/alfresco-into-gateway");
	
	public void setDestination(String d) {
		_destination=new File(d);
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		GatewayPackage gp = _packageCreator.createPackage(actionedUponNodeRef);
		_packageCreator.postPackage(gp, _destination);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		// Intentionally left blank
	}

}
