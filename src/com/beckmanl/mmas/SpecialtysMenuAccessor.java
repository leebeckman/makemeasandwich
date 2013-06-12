package com.beckmanl.mmas;

import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

//DONE
public class SpecialtysMenuAccessor implements IMenuAccessor<HtmlInput> {

	private final String SANDWICH_HEADER = "Sandwiches";
	private final String COOKIE_HEADER = "Cookies & Baked Goods";
	private final String SOUP_HEADER = "Soups";
	private final String SALAD_HEADER = "Small Salads";
	private final String DRINK_HEADER = "Cold Drinks";
	
	private List<IMenuItem<HtmlInput>> menuItems;
	
	public SpecialtysMenuAccessor(HtmlElement menuRoot) {
		menuItems = new LinkedList<IMenuItem<HtmlInput>>();
		
		List<?> menuTitleElements = menuRoot.getByXPath("//h2[contains(@class,'accordionMenuHeader')]/a/span");
		for (Object titleElementRaw : menuTitleElements) {
			HtmlElement titleElement = (HtmlElement)titleElementRaw;
			HtmlElement subMenuRoot = (HtmlElement) titleElement.getByXPath("../../following-sibling::ul").get(0);
			
			System.out.println(titleElement.getTextContent());
			
			if (titleElement.getTextContent().contains(SANDWICH_HEADER)) {
				processSubMenu(subMenuRoot, FoodType.SANDWICH);
			}
			else if (titleElement.getTextContent().contains(COOKIE_HEADER)) {
				processSubMenu(subMenuRoot, FoodType.COOKIE);
			}
			else if (titleElement.getTextContent().contains(SOUP_HEADER)) {
				processSubMenu(subMenuRoot, FoodType.SOUP);
			}
			else if (titleElement.getTextContent().contains(SALAD_HEADER)) {
				processSubMenu(subMenuRoot, FoodType.SALAD);
			}
			else if (titleElement.getTextContent().contains(DRINK_HEADER)) {
				processSubMenu(subMenuRoot, FoodType.DRINK);
			}
		}
		
	}
	
	private void processSubMenu(HtmlElement subMenuRoot, FoodType foodType) {
		List<?> menuItemElements = subMenuRoot.getByXPath("//li[@class='productListItem']");
		for (Object menuItemElementRaw : menuItemElements) {
			HtmlElement menuItemElement = (HtmlElement)menuItemElementRaw;
			System.out.println(((HtmlElement)menuItemElement.getByXPath("//div[@class='pName']/a").get(0)).getTextContent());
			menuItems.add(new SpecialtysMenuItem(menuItemElement, foodType));
		}
	}
	
	@Override
	public List<IMenuItem<HtmlInput>> getMenuItems() {
		return menuItems;
	}
	
	public class SpecialtysMenuItem implements IMenuItem<HtmlInput> {
		private String name;
		private float price;
		private FoodType foodType;
		private HtmlInput addControl;
		
		public SpecialtysMenuItem(HtmlElement menuItemElement, FoodType foodType) {
			if (menuItemElement.getByXPath("//div[@class='pName']/a").size() > 0)
				name = ((HtmlElement)menuItemElement.getByXPath("//div[@class='pName']/a").get(0)).getTextContent();
			else
				name = ((HtmlElement)menuItemElement.getByXPath("//div[@class='pName']").get(0)).getTextContent();

			price = Float.parseFloat(((HtmlElement)menuItemElement.getByXPath("//div[@class='pPrice']").get(0)).getTextContent().replaceAll("[^\\d.]+", ""));
			addControl = (HtmlInput) menuItemElement.getByXPath("//div[@class='pBtn']/input").get(0);
			this.foodType = foodType;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public float getPrice() {
			return price;
		}

		@Override
		public FoodType getType() {
			return foodType;
		}

		@Override
		public HtmlInput getAddControl() {
			return addControl;
		}
		
	}

}
