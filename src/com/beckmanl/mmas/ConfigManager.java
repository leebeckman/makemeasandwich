package com.beckmanl.mmas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.beckmanl.mmas.IMenuAccessor.FoodType;
import com.beckmanl.mmas.exceptions.NotSUException;
import com.beckmanl.mmas.exceptions.UndefinedConfigException;

public class ConfigManager {
	private final String USERNAME_KEY = "username";
	private final String PASSWORD_KEY = "password";
	private final String PICKUP_KEY = "pickup";
	private final String SANDWICH_P_KEY = "sandwichP";
	private final String COOKIE_P_KEY = "cookieP";
	private final String SALAD_P_KEY = "saladP";
	private final String SOUP_P_KEY = "soupP";
	private final String DRINK_P_KEY = "drinkP";
	private final String MULTIPLESIDES_KEY = "multipleSides";
	private final String MAX_BUDGET_KEY = "maxBudget";
	private final String MENU_FILE_PATH_KEY = "menuFilePath";

	private Properties props;
	private Map<FoodType, List<String>> menuItemsByType;
	
	public ConfigManager(File configFile) throws NotSUException, FileNotFoundException, IOException, URISyntaxException, UndefinedConfigException {
		if (!checkHasSU())
			throw new NotSUException("Run with sudo");
		
		props = new Properties();
		props.load(new FileInputStream(configFile));
		
		menuItemsByType = new HashMap<FoodType, List<String>>();
		File menuFile = new File(getProperty(MENU_FILE_PATH_KEY));
		
		BufferedReader br = new BufferedReader(new FileReader(menuFile));
		String line, entry;
		FoodType mode = null;
		Set<String> foodTypes = new HashSet<String>();
		for (FoodType foodType : Arrays.asList(FoodType.values()))
			foodTypes.add(foodType.toString());
		while ((line = br.readLine()) != null) {
			entry = line.trim();
                        if (entry.startsWith("#")) {
                            continue;
                        }
			if (foodTypes.contains(entry)) {
				mode = FoodType.valueOf(entry);
				if (menuItemsByType.get(mode) == null) 
					menuItemsByType.put(mode, new LinkedList<String>());
			}
			else if (!entry.isEmpty()) {
				menuItemsByType.get(mode).add(entry);
			}
		}
		br.close();
	}
	
	public String getUsername() throws UndefinedConfigException {
		return getProperty(USERNAME_KEY);
	}
	
	public String getPassword() throws UndefinedConfigException {
		return getProperty(PASSWORD_KEY);
	}
	
	public String getPickupLocation() throws UndefinedConfigException {
		return getProperty(PICKUP_KEY);
	}

	public Map<FoodType, List<String>> getEnabledItemsByType() {
		return menuItemsByType;		
	}
	
	public boolean getMultipleSidesEnabled() throws UndefinedConfigException {
		return getProperty(MULTIPLESIDES_KEY).equals("true");
	}
	
	public float getMaxBudget() throws NumberFormatException, UndefinedConfigException {
		return Float.parseFloat(getProperty(MAX_BUDGET_KEY));
	}

	public float getSandwichProb() throws NumberFormatException, UndefinedConfigException {
		return Float.parseFloat(getProperty(SANDWICH_P_KEY));
	}

	public float getCookieProb() throws NumberFormatException, UndefinedConfigException {
		return Float.parseFloat(getProperty(COOKIE_P_KEY));
	}

	public float getSoupProb() throws NumberFormatException, UndefinedConfigException {
		return Float.parseFloat(getProperty(SOUP_P_KEY));
	}

	public float getSaladProb() throws NumberFormatException, UndefinedConfigException {
		return Float.parseFloat(getProperty(SALAD_P_KEY));
	}

	public float getDrinkProb() throws NumberFormatException, UndefinedConfigException {
		return Float.parseFloat(getProperty(DRINK_P_KEY));
	}

	private boolean checkHasSU() {
		return System.getProperty("user.name").equals("root");
	}
	
	private String getProperty(String key) throws UndefinedConfigException {
		String value = props.getProperty(key);
		if (value == null)
			throw new UndefinedConfigException("Undefined config value: " + key);
		
		return value;
	}
	
}
