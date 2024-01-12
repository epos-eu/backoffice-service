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
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.DBAPIClient.UpdateQuery;

public class DataProductManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static List<DataProduct> getDataProduct(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		List<DataProduct> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());

			}else {
				list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().instanceId(instance_id));
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
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createDataProduct(DataProduct dataProduct, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new DataProduct? --> InstanceId = null && InstanceChangeId == null
		 * New DataProduct from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(dataProduct.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			dataProduct.setUid(dataProduct.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		dataProduct.setInstanceId(null);
		dataProduct.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (dataProduct.getMetaId() != null) {
			List<DataProduct> retrieved = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(dataProduct.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(dataProduct.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(dataProduct.getMetaId()));
			if(!retrieved.isEmpty()) {
				dataProduct.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		dataProduct.setState(State.DRAFT);
		dataProduct.setEditorId(user.getMetaId());
		dataProduct.setFileProvenance("instance created with the backoffice");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(dataProduct);
			dataProduct.setInstanceId(reference.getInstanceId());
			dataProduct.setMetaId(reference.getMetaId());
			//TODO: Parents?
		} catch (Exception e) {
			e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		manageNewDraftRelations(dataProduct, reference, user, parents, sons);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateDataProduct(DataProduct dataProduct, User user, boolean parents, boolean sons) {

		if(dataProduct.getState()!=null && (dataProduct.getState().equals(State.ARCHIVED) || dataProduct.getState().equals(State.PUBLISHED))) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (dataProduct.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(dataProduct.getInstanceChangedId() == null || dataProduct.getInstanceChangedId().isEmpty())
			dataProduct.setInstanceChangedId(null);

		dataProduct.setEditorId(user.getMetaId());
		dataProduct.setFileProvenance("instance created with the backoffice");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(dataProduct, new SaveQuery().setInstanceId(dataProduct.getInstanceId()));//dbapi.hardUpdate(dataProduct);
		} catch (Exception e) {
			//e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param dataProduct
	 * @return
	 */
	public static boolean deleteDataProduct(String instance_id, User user) {
		List<DataProduct> list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		DataProduct instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		dbapi.delete(DataProduct.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}


	private static void manageNewDraftRelations(DataProduct dataProduct, LinkedEntity relation, User user, boolean parents, boolean sons) {

		if(sons) {
			if(dataProduct.getDistribution()!=null)
				for(LinkedEntity le : dataProduct.getDistribution()) {
					Distribution distribution = DistributionManager.getDistribution(le.getMetaId(), le.getInstanceId(), user).get(0);
					distribution.getDataProduct().add(relation);
					DistributionManager.createDistribution(distribution, user, false, true);
				}
		}
	
	}

}
