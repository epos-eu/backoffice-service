package org.epos.backoffice.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.DataProductManager;
import org.epos.backoffice.api.util.DistributionManager;
import org.epos.backoffice.bean.RoleEnum;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.DataProduct;
import org.epos.eposdatamodel.Distribution;
import org.epos.eposdatamodel.LinkedEntity;

public class VariousTests {
	
	public static void main(String[] args) {
		DataProduct dp = new DataProduct();
		dp.setUid("MOCCABBACCA2");

		User user = new User();
		user.setRole(RoleEnum.ADMIN);
		user.setMetaId("fixedUser5_metaid");
		System.out.println("----> CREATE DATAPRODUCT");
		ApiResponseMessage message_one = DataProductManager.createDataProduct(dp, user, true, true);
		System.out.println(message_one);
		LinkedEntity dataproduct = message_one.getEntity();
		
		dp.setInstanceId(dataproduct.getInstanceId());
		dp.setMetaId(dataproduct.getMetaId());
		dp.setUid(dataproduct.getUid());
		ArrayList<String> titles = new ArrayList<String>();
		titles.add("TEST");
		dp.setTitle(titles);
		System.out.println("----> UPDATE DATAPRODUCT");
		DataProductManager.updateDataProduct(dp, user, true, true);
	
		
		Distribution distr = new Distribution();
		distr.setDataProduct(List.of(dataproduct));
		System.out.println("----> CREATE DISTRIBUTION");
		ApiResponseMessage message_three = DistributionManager.createDistribution(distr,user, true, true);
		System.out.println(message_three);
		
		System.out.println(DataProductManager.getDataProduct(dataproduct.getMetaId(), dataproduct.getInstanceId(), user));
		

		System.out.println(DistributionManager.getDistribution(message_three.getEntity().getMetaId(), message_three.getEntity().getInstanceId(), user));
		

		dp.setDistribution(List.of(message_three.getEntity()));

		System.out.println("----> UPDATE DATAPRODUCT");
		ApiResponseMessage message_two = DataProductManager.updateDataProduct(dp, user, true, true);
		System.out.println(message_two);
		
		
		
		System.out.println(DataProductManager.getDataProduct(dataproduct.getMetaId(), dataproduct.getInstanceId(), user).getListOfEntities());
		System.out.println(DistributionManager.getDistribution(message_three.getEntity().getMetaId(), message_three.getEntity().getInstanceId(), user).getListOfEntities());
		
	}

}
