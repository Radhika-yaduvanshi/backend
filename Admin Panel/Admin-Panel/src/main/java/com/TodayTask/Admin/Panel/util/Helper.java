package com.TodayTask.Admin.Panel.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Helper {

	@Autowired
	private ObjectMapper mapper;

	/**
	public UserEntity convertUserProxyToEntity(UserProxy userproxy)
	{
	return mapper.convertValue(userproxy, UserEntity.class);	
	}
	
	public UserProxy convertUserEntityToProxy(UserEntity userEntity)
	{
	return mapper.convertValue(userEntity, UserProxy.class);	
	}
	
	public List<UserEntity> convertUserListProxyToEntity(List<UserProxy> userProxy)
	{
	return userProxy.stream().map(a->mapper.convertValue(a, UserEntity.class)).toList();	
	}
	
	public List<UserProxy> convertUserListEntityToProxy(List<UserEntity> userEntitylst)
	{
		return userEntitylst.stream().map(a->mapper.convertValue(a, UserProxy.class)).toList();
	}
	**/
	
	public <S, T> T convert(S source, Class<T> targetClass) 
	{
	        return mapper.convertValue(source, targetClass);
	}
	   
	public <S,T> List<T> convertList(List<S> sourceList,Class<T> tragetClass)
	{
		return sourceList.stream().map(source->convert(source,tragetClass)).collect(Collectors.toList());
	}
	
}
