package org.epos.backoffice.api.controller;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.UserManager;
import org.epos.backoffice.bean.RoleEnum;
import org.epos.backoffice.bean.User;

public class VariousTests {
	
	public static void main(String[] args) {
		User user = new User();
		user.setEduPersonUniqueId("4d2b983a88a14e098e06bcdf9254123a@aaai.epos-eu.org");
		user.setFirstName("Valerio");
		user.setLastName("Vinciarelli");
		user.setEmail("valerio.vinciarelli@epos-eric.eu");
		user.setRole(RoleEnum.ADMIN);
		
		ApiResponseMessage message_one = UserManager.getUser(null, "test", user, true);
		System.out.println(message_one);
		
		User newUser = new User();
		newUser.setInstanceId("eb8e9b60-8929-4da2-a5f7-a6e50df7c3c3");
		newUser.setRole(RoleEnum.EDITOR);
		
		
		message_one = UserManager.updateUser(newUser, user);
		System.out.println(message_one);
	}
	/*public static void main(String[] args) {
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
		
	}*/
	
	

}
