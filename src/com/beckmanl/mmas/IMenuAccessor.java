package com.beckmanl.mmas;

import java.util.List;

//DONE
public interface IMenuAccessor<T> {
	public enum FoodType {
		SANDWICH,
		COOKIE,
		SOUP,
		SALAD,
		DRINK
	}
	
	public List<IMenuItem<T>> getMenuItems();
	
	public interface IMenuItem<T> {
		public String getName();
		
		public float getPrice();
		
		public FoodType getType();
		
		public T getAddControl();
	}
}
