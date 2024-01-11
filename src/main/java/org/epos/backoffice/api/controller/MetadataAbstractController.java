package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.DataProductManager;
import org.epos.backoffice.api.util.DistributionManager;
import org.epos.backoffice.api.util.OperationManager;
import org.epos.backoffice.api.util.WebServiceManager;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public abstract class MetadataAbstractController<T extends EPOSDataModelEntity> extends AbstractController<T> {

	public MetadataAbstractController(ObjectMapper objectMapper, HttpServletRequest request, Class<T> entityType) {
		super(objectMapper, request, entityType);
	}
	protected ResponseEntity<?> getMethod(String meta_id, String instance_id) {
		if (meta_id == null)
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(1, "The [meta_id] field can't be left blank"));
		if(instance_id == null) {
			instance_id = "all";
		}

		User user = getUserFromSession();

		List<T> revertedList = new ArrayList<>();
		List<T> list = new ArrayList<T>();
		
		if(entityType.equals(DataProduct.class)) list.addAll((Collection<? extends T>) DataProductManager.getDataProduct(meta_id,instance_id, user));
		if(entityType.equals(Distribution.class)) list.addAll((Collection<? extends T>) DistributionManager.getDistribution(meta_id,instance_id, user));
		if(entityType.equals(WebService.class)) list.addAll((Collection<? extends T>) WebServiceManager.getWebService(meta_id,instance_id, user));
		if(entityType.equals(Operation.class)) list.addAll((Collection<? extends T>) OperationManager.getOperation(meta_id,instance_id, user));

		list.forEach(e -> revertedList.add(0, e));
		
		if (list.isEmpty())
			return ResponseEntity.status(404).body("[]");

		return ResponseEntity
				.status(200)
				.body(gson.toJson(revertedList));
	}


	protected ResponseEntity<?> deleteMethod(String instance_id) {
		if (instance_id == null)
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank"));

		User user = getUserFromSession();

		boolean done = false;
		if(entityType.equals(DataProduct.class)) done = DataProductManager.deleteDataProduct(instance_id, user);
		if(entityType.equals(Distribution.class)) done = DistributionManager.deleteDistribution(instance_id, user);
		if(entityType.equals(WebService.class)) done = WebServiceManager.deleteWebService(instance_id, user);
		if(entityType.equals(Operation.class)) done = OperationManager.deleteOperation(instance_id, user);

		if(done) return ResponseEntity.status(200).body(new ApiResponseMessage(ApiResponseMessage.OK, "Instance deleted"));
		else return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Instance not deleted"));
	}

	protected ResponseEntity<?> postMethod(EPOSDataModelEntity body, boolean takeCareOfTheParent) {

		User user = getUserFromSession();
		
		if(entityType.equals(DataProduct.class)) {
			ApiResponseMessage response = DataProductManager.createDataProduct((DataProduct) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(Distribution.class)) {
			ApiResponseMessage response = DistributionManager.createDistribution((Distribution) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(WebService.class)) {
			ApiResponseMessage response = WebServiceManager.createWebService((WebService) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(Operation.class)) {
			ApiResponseMessage response = OperationManager.createOperation((Operation) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}

		return ResponseEntity.status(400).body(null);
	}

	protected ResponseEntity<?> updateMethod(EPOSDataModelEntity body, boolean takeCareOfTheParent) {

		User user = getUserFromSession();

		if(entityType.equals(DataProduct.class)) {
			ApiResponseMessage response = DataProductManager.updateDataProduct((DataProduct) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(Distribution.class)) {
			ApiResponseMessage response = DistributionManager.updateDistribution((Distribution) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		if(entityType.equals(WebService.class)) {
			ApiResponseMessage response = WebServiceManager.updateWebService((WebService) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		if(entityType.equals(Operation.class)) {
			ApiResponseMessage response = OperationManager.updateOperation((Operation) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}

		return ResponseEntity.status(400).body(null);
	}
}