package org.epos.backoffice.api.util;

import org.epos.eposdatamodel.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EPOSDataModelHelper {

    public static <S extends EPOSDataModelEntity> List<LinkedEntity> getParents(S son) {
    	if (son instanceof ContactPoint) {
    		List<LinkedEntity> entities = new ArrayList<LinkedEntity>();
    		if( Optional.ofNullable(((ContactPoint) son).getOrganization()).isPresent()) entities.add(Optional.ofNullable(((ContactPoint) son).getOrganization()).get());
    		if(Optional.ofNullable(((ContactPoint) son).getPerson()).isPresent()) entities.add(Optional.ofNullable(((ContactPoint) son).getPerson()).get());
    		return entities;
    	}
        if (son instanceof Distribution) return Optional.ofNullable(((Distribution) son).getDataProduct()).orElse(List.of());
        if (son instanceof WebService) return Optional.ofNullable(((WebService) son).getDistribution()).orElse(List.of());
        if (son instanceof Operation) return Optional.ofNullable(((Operation) son).getWebservice()).orElse(List.of());
        if (son instanceof DataProduct) return List.of();
        System.err.println("GetParents ...just... Why? "+son.getClass());
		return null;
    }	

    public static <P extends EPOSDataModelEntity> List<LinkedEntity> getSons(P parent) {
        if (parent instanceof Distribution)
            return Optional.ofNullable(List.of(((Distribution) parent).getAccessService())).orElse(List.of());
        if (parent instanceof WebService)
            return Optional.ofNullable(((WebService) parent).getSupportedOperation()).orElse(List.of());
        if (parent instanceof DataProduct)
            return Optional.ofNullable(((DataProduct) parent).getDistribution()).orElse(List.of());
        if (parent instanceof Operation)
            return List.of();
        System.err.println("GetSons ...just... Why? "+parent.getClass());
		return List.of();
    }

    public static <S extends EPOSDataModelEntity> Class<? extends EPOSDataModelEntity> getParentClass(S son) {
    	if (son instanceof ContactPoint) {
    		if(Optional.ofNullable(((ContactPoint) son).getOrganization()).isPresent()) return Organization.class;
    		if(Optional.ofNullable(((ContactPoint) son).getPerson()).isPresent()) return Person.class;
    	}
        if (son instanceof Distribution) return DataProduct.class;
        if (son instanceof WebService) return Distribution.class;
        if (son instanceof Operation) return WebService.class;
        System.err.println("GetSons ...just... Why? "+son.getClass());
		return null;
    }

    public static <P extends EPOSDataModelEntity> Class<? extends EPOSDataModelEntity> getSonClass(P parent) {
        if (parent instanceof DataProduct) return Distribution.class;
        if (parent instanceof Distribution) return WebService.class;
        if (parent instanceof WebService) return Operation.class;
        System.err.println("GetSons ...just... Why? "+parent.getClass());
		return null;
    }

    public static <P extends EPOSDataModelEntity> Class<? extends EPOSDataModelEntity> getSonClass(Class<P> parentClass) {
        if (parentClass.equals(DataProduct.class)) return Distribution.class;
        if (parentClass.equals(Distribution.class)) return WebService.class;
        if (parentClass.equals(WebService.class)) return Operation.class;
        System.err.println("GetSonClass B  ...just... Why? "+parentClass);
		return parentClass;
    }
}
