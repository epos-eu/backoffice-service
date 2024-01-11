package org.epos.backoffice.api.util;

import static org.epos.backoffice.bean.RoleEnum.ADMIN;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.DataProduct;
import org.epos.eposdatamodel.Distribution;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.eposdatamodel.WebService;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.DBAPIClient.UpdateQuery;

public class DistributionManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static List<Distribution> getDistribution(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		List<Distribution> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());

			}else {
				list = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery().instanceId(instance_id));
			}
		}

		System.out.println(list);

		list = list.stream()
				.filter(
						elem -> user.getRole().equals(ADMIN) || elem.getState().equals(State.PUBLISHED) ||
						(elem.getState().equals(State.DRAFT) && user.getMetaId().equals(elem.getEditorId()))
						)
				.collect(Collectors.toList());

		return list;
	}

	/**
	 * 
	 * @param Distribution
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createDistribution(Distribution distribution, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new Distribution? --> InstanceId = null && InstanceChangeId == null
		 * New Distribution from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(distribution.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			distribution.setUid(distribution.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		distribution.setInstanceId(null);
		distribution.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (distribution.getMetaId() != null) {
			List<Distribution> retrieved = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(distribution.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(distribution.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(distribution.getMetaId()));
			if(!retrieved.isEmpty()) {
				distribution.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		distribution.setState(State.DRAFT);
		distribution.setEditorId(user.getMetaId());
		distribution.setFileProvenance("instance created with the backoffice");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(distribution);
			distribution.setInstanceId(reference.getInstanceId());
			distribution.setMetaId(reference.getMetaId());
			//TODO: Parents?
		} catch (Exception e) {
			e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		manageRelations(distribution, reference, user, parents, sons);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param distribution
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateDistribution(Distribution distribution, User user, boolean parents, boolean sons) {

		if(distribution.getState()!=null && (distribution.getState().equals(State.ARCHIVED) || distribution.getState().equals(State.PUBLISHED))) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (distribution.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(distribution.getInstanceChangedId() == null || distribution.getInstanceChangedId().isEmpty())
			distribution.setInstanceChangedId(null);

		distribution.setEditorId(user.getMetaId());
		distribution.setFileProvenance("instance created with the backoffice");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(distribution, new SaveQuery().setInstanceId(distribution.getInstanceId()));//dbapi.hardUpdate(distribution);
		} catch (Exception e) {
			//e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		manageRelations(distribution, reference, user, parents, sons);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param distribution
	 * @return
	 */
	public static boolean deleteDistribution(String instance_id, User user) {
		List<Distribution> list = dbapi.retrieve(Distribution.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		Distribution instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		dbapi.delete(Distribution.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}

	private static void manageRelations(Distribution distribution, LinkedEntity relation, User user, boolean parents, boolean sons) {

		System.out.println("*************\nManaging relation of: "+distribution);
		System.out.println("DataProduct: "+distribution.getDataProduct());
		if(parents) {
			if(distribution.getDataProduct()!=null) {
				for(LinkedEntity le : distribution.getDataProduct()) {
					DataProduct dataProduct = DataProductManager.getDataProduct(le.getMetaId(), le.getInstanceId(), user).get(0);
					dataProduct.addDistribution(relation);
					DataProductManager.updateDataProduct(dataProduct, user, false, false);
				}
			}
		}
	}


}
