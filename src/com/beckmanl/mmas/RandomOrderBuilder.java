package com.beckmanl.mmas;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.beckmanl.mmas.IMenuAccessor.FoodType;
import com.beckmanl.mmas.IMenuAccessor.IMenuItem;
import com.beckmanl.mmas.exceptions.BadOrderException;
import com.beckmanl.mmas.exceptions.UndefinedConfigException;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

public class RandomOrderBuilder {
	
	private ConfigManager config;
	private Map<FoodType, List<IMenuItem<HtmlInput>>> enabledMenuItemsByFoodType;
	private Random random;
	
	public RandomOrderBuilder(ConfigManager config, IMenuAccessor<HtmlInput> menuAccessor) {
		this.config = config;
		this.enabledMenuItemsByFoodType = getMenuItemsByFoodType(menuAccessor);
		this.random = new Random();
	}
	
	public List<IMenuItem<HtmlInput>> getRandomOrder() throws NumberFormatException, UndefinedConfigException, BadOrderException {
		LinkedList<IMenuItem<HtmlInput>> randomOrder = new LinkedList<IMenuItem<HtmlInput>>();
		List<IMenuItem<HtmlInput>> foodItemSet;
		boolean addedSide = false;
		float remainingBudget = config.getMaxBudget();
		
		if (random.nextFloat() < config.getSandwichProb()) {
			foodItemSet = enabledMenuItemsByFoodType.get(FoodType.SANDWICH);
			
			overBudgetRemove(foodItemSet, remainingBudget);
			if (foodItemSet.isEmpty())
				throw new BadOrderException("There are no sandwiches within the set budget. I cannot make you a sandwich");
			
			randomOrder.add(foodItemSet.get(random.nextInt(foodItemSet.size())));
			remainingBudget -= randomOrder.peekLast().getPrice();
		}
		if (random.nextFloat() < config.getCookieProb() && (config.getMultipleSidesEnabled() || !addedSide)) {
			addedSide = true;
			foodItemSet = enabledMenuItemsByFoodType.get(FoodType.COOKIE);
			
			overBudgetRemove(foodItemSet, remainingBudget);
			
			if (!foodItemSet.isEmpty()) {
				randomOrder.add(foodItemSet.get(random.nextInt(foodItemSet.size())));
				remainingBudget -= randomOrder.peekLast().getPrice();
			}
		}
		if (random.nextFloat() < config.getSaladProb() && (config.getMultipleSidesEnabled() || !addedSide)) {
			addedSide = true;
			foodItemSet = enabledMenuItemsByFoodType.get(FoodType.SALAD);
			
			overBudgetRemove(foodItemSet, remainingBudget);
			
			if (!foodItemSet.isEmpty()) {
				randomOrder.add(foodItemSet.get(random.nextInt(foodItemSet.size())));
				remainingBudget -= randomOrder.peekLast().getPrice();
			}
		}
		if (random.nextFloat() < config.getSoupProb() && (config.getMultipleSidesEnabled() || !addedSide)) {
			addedSide = true;
			foodItemSet = enabledMenuItemsByFoodType.get(FoodType.SOUP);
			
			overBudgetRemove(foodItemSet, remainingBudget);
			
			if (!foodItemSet.isEmpty()) {
				randomOrder.add(foodItemSet.get(random.nextInt(foodItemSet.size())));
				remainingBudget -= randomOrder.peekLast().getPrice();
			}
		}
		if (random.nextFloat() < config.getDrinkProb() && (config.getMultipleSidesEnabled() || !addedSide)) {
			addedSide = true;
			foodItemSet = enabledMenuItemsByFoodType.get(FoodType.DRINK);
			
			overBudgetRemove(foodItemSet, remainingBudget);
			
			if (!foodItemSet.isEmpty()) {
				randomOrder.add(foodItemSet.get(random.nextInt(foodItemSet.size())));
				remainingBudget -= randomOrder.peekLast().getPrice();
			}
		}
		
		return randomOrder;
	}
	
	private void overBudgetRemove(List<IMenuItem<HtmlInput>> foodItemSet, float remainingBudget) {
		List<IMenuItem<HtmlInput>> overBudgetRemove = new LinkedList<IMenuItem<HtmlInput>>();
		for (IMenuItem<HtmlInput> foodItem : foodItemSet) {
			if (foodItem.getPrice() > remainingBudget)
				overBudgetRemove.add(foodItem);
		}
		foodItemSet.removeAll(overBudgetRemove);
	}
	
	private Map<FoodType, List<IMenuItem<HtmlInput>>> getMenuItemsByFoodType(IMenuAccessor<HtmlInput> menuAccessor) {
		Map<FoodType, List<IMenuItem<HtmlInput>>> menuItemsByFoodType = new HashMap<IMenuAccessor.FoodType, List<IMenuItem<HtmlInput>>>();
		Map<FoodType, List<String>> enabledMenuItemsByFoodType = config.getEnabledItemsByType();
		
		for (IMenuItem<HtmlInput> menuItem : menuAccessor.getMenuItems()) {
			if (menuItemsByFoodType.get(menuItem.getType()) == null) 
				menuItemsByFoodType.put(menuItem.getType(), new LinkedList<IMenuAccessor.IMenuItem<HtmlInput>>());
			
			List<IMenuItem<HtmlInput>> menuItems = menuItemsByFoodType.get(menuItem.getType());
			List<String> enabledMenuItems = enabledMenuItemsByFoodType.get(menuItem.getType());
			if (enabledMenuItems.contains(menuItem.getName()))
				menuItems.add(menuItem);
		}
		
		return menuItemsByFoodType;
	}
	
}
