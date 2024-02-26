package org.epos.backoffice.api.controller;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.UserManager;
import org.epos.backoffice.bean.RoleEnum;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.Role;

public class VariousTests {
	
	public static void main(String[] args) {
		User user = new User();
		user.setEduPersonUniqueId("4d2b983a88a14e098e06bcdf9254123a@aaai.epos-eu.org");
		user.setFirstName("Valerio");
		user.setLastName("Vinciarelli");
		user.setEmail("valerio.vinciarelli@epos-eric.eu");
		user.setRole(RoleEnum.ADMIN);
		
		User newUser = new User();
		newUser.setEduPersonUniqueId("f190eef9f005482a9e88b4400b080a2a@aaai.epos-eu.org");
		newUser.setFirstName("Valerio");
		newUser.setLastName("Vinciarelli");
		newUser.setEmail("valerio.vinciarelli@ingv.it");
		newUser.setInstanceId("91dfdff5-efbf-4883-b6b2-3d396567a400");
		newUser.setMetaId("35c623ca-f5ce-4926-b496-c3cfbdbba8f4");
		newUser.setRole(RoleEnum.EDITOR);
		
		//ApiResponseMessage message_one = UserManager.updateUser(newUser,user);
		//System.out.println(message_one);
		
		
		ApiResponseMessage message_one = UserManager.getUser("all","all",user,true);
		System.out.println(message_one.getListOfUsers());
		
		//message_one = UserManager.updateUser(newUser, user);
		//System.out.println(message_one);
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
