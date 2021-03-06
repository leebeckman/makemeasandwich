package com.beckmanl.mmas;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.beckmanl.mmas.IMenuAccessor.FoodType;
import com.beckmanl.mmas.IMenuAccessor.IMenuItem;
import com.beckmanl.mmas.exceptions.BadLoginException;
import com.beckmanl.mmas.exceptions.BadOrderException;
import com.beckmanl.mmas.exceptions.UndefinedConfigException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class SpecialtysBrowser {

	private WebClient webClient;
	private ConfigManager config;
	private RandomOrderBuilder randomOrderBuilder;
	
	private HtmlPage currentPage;
	
	public SpecialtysBrowser(WebClient webClient, ConfigManager config) {
		this.webClient = webClient;
		this.config = config;
		this.currentPage = null;
		this.randomOrderBuilder = null;
	}
	
	// DONE
	public void doOrder() throws BadLoginException, BadOrderException, MalformedURLException, IOException, FailingHttpStatusCodeException, UndefinedConfigException {
		doLogin(config.getUsername(), config.getPassword());
		
		goToMenu();
		
		HtmlElement menuRoot = (HtmlElement) currentPage.getByXPath("//div[@class='accordionMenu']").get(0);
		
		randomOrderBuilder = new RandomOrderBuilder(config, new SpecialtysMenuAccessor(menuRoot));
		
		List<IMenuItem<HtmlInput>> randomOrder = randomOrderBuilder.getRandomOrder();
		
		addItemsToOrder(randomOrder);
		
		if (!verifyOrder(randomOrder))
			throw new BadOrderException("Order failed on verification");
		
		doCheckout(randomOrder);
	}
	
	// DONE
	private void doLogin(String username, String password) throws BadLoginException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		if (!goToLogin())
			throw new BadLoginException("Failed to load Login page");
		
		HtmlForm loginForm = currentPage.getForms().get(0);
		HtmlInput usernameInput = loginForm.getInputByName("ctl00$cpMain$tUsername");
		HtmlInput passwordInput = loginForm.getInputByName("ctl00$cpMain$tPassword");
		HtmlInput submitInput = loginForm.getInputByName("ctl00$cpMain$btnLogin");
		
		if (usernameInput == null || passwordInput == null || submitInput == null)
			throw new BadLoginException("Failed to find Login form fields");
		
		usernameInput.setValueAttribute(username.trim());
		passwordInput.setValueAttribute(password.trim());
		
		currentPage = submitInput.click();
		if (!verifyLocation("http://www.specialtys.com/mobile/Default.aspx"))
			throw new BadLoginException("Failed to load post-Login page");
	}
	
	// DONE
	private boolean goToLogin() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		currentPage = webClient.getPage("https://www.specialtys.com/Mobile/Login.aspx?Redirect=False");
		return verifyLocation("https://www.specialtys.com/Mobile/Login.aspx?Redirect=False");
	}
	
	// DONE
	private boolean goToMenu() throws IOException {
		return navigateAndVerify("Menu.aspx", "http://www.specialtys.com/mobile/Menu.aspx");
	}
	
	// DONE
	private boolean goToCheckout() throws IOException {
		return navigateAndVerify("Checkout.aspx", "http://www.specialtys.com/mobile/Checkout.aspx");
	}
	
	// DONE
	private boolean goToPickup() throws IOException, BadOrderException {
		HtmlInput pickupInput = (HtmlInput) currentPage.getByXPath("//input[@name='ctl00$cpMain$btnContinue']").get(0);
		if (pickupInput == null)
			throw new BadOrderException("No input to navigate to pickup");
		pickupInput.click();
		webClient.waitForBackgroundJavaScript(10000);
		currentPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		return verifyLocation("http://www.specialtys.com/mobile/CheckoutLocation.aspx");
	}
	
	// DONE
	private boolean goToPayment() throws IOException, BadOrderException {
		if (!verifyLocation("http://www.specialtys.com/mobile/CheckoutDateTime.aspx"))
			return false;
		HtmlInput paymentInput = (HtmlInput) currentPage.getByXPath("//input[@name='ctl00$cpMain$btnNext']").get(0);
		if (paymentInput == null)
			throw new BadOrderException("No input to navigate to payment");
		paymentInput.click();
		webClient.waitForBackgroundJavaScript(10000);
		currentPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		return verifyLocation("http://www.specialtys.com/mobile/CheckoutPayment.aspx");
	}
	
	// DONE
	private boolean navigateAndVerify(String navigateHref, String verifyURL) throws IOException {
		if (verifyLocation(verifyURL))
			return true;
		HtmlAnchor link = currentPage.getAnchorByHref(navigateHref);
		currentPage = link.click();
		return verifyLocation(verifyURL);
	}
	
	// DONE
	private boolean verifyLocation(String pageURL) {
		return webClient.getCurrentWindow().getEnclosedPage().getUrl().toString().equals(pageURL);
	}
	
	// DONE
	private void addItemsToOrder(List<IMenuItem<HtmlInput>> randomOrder) throws IOException, BadOrderException {
		if (!goToMenu())
			throw new BadOrderException("Could not load menu");
		
		for (IMenuItem<HtmlInput> orderItem : randomOrder) {
			orderItem.getAddControl().click();
			webClient.waitForBackgroundJavaScript(10000);
			currentPage = (HtmlPage)webClient.getCurrentWindow().getEnclosedPage();
		}
	}
	
	// DONE
	private boolean verifyOrder(List<IMenuItem<HtmlInput>> randomOrder) throws BadOrderException, IOException {
		if (!goToCheckout())
			throw new BadOrderException("Could not load checkout");
		
		HtmlPage testPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		
		int itemsFound = 0;
		List<?> orderRoot = currentPage.getByXPath("//ul[@id='cpMain_checkoutOrdered']//li[@class='checkoutListItem']");
		for (Object orderItemRootRaw : orderRoot) {
			HtmlElement orderItemRoot = (HtmlElement) orderItemRootRaw;
			String itemName = orderItemRoot.getByXPath(".//div[@class='pName']/text()").get(0).toString().trim();
			float itemPrice = Float.parseFloat(orderItemRoot.getByXPath(".//div[@class='pPrice']/text()").get(0).toString().replaceAll("[^\\d.]+", ""));
			int quantity = Integer.parseInt(orderItemRoot.getByXPath(".//div[@class='pQty']/input/attribute::value").get(0).toString().replaceAll("[^\\d.]+", ""));
			
			if (quantity != 1)
				return false;
			
			boolean itemFound = false;
			for (IMenuItem<HtmlInput> orderItem : randomOrder) {
				if (orderItem.getName().equals(itemName) && orderItem.getPrice() == itemPrice) {
					itemFound = true;
					break;
				}
			}
			
			if (!itemFound)
				return false;
			itemsFound++;
		}
		
		return (itemsFound == randomOrder.size());
	}
	
	
	private boolean doCheckout(List<IMenuItem<HtmlInput>> randomOrder) throws BadOrderException, IOException, UndefinedConfigException {
		if (!goToCheckout())
			throw new BadOrderException("Could not load checkout");
		if (!goToPickup())
			throw new BadOrderException("Could not load pickup locations");
		if (!setPickupLocation(config.getPickupLocation()))
			throw new BadOrderException("Could not set pickup location");
		if (!setPickupTime())
			throw new BadOrderException("Could not set pickup time");
		
		float subTotal = Float.parseFloat(currentPage.getByXPath("//td[text()='Subtotal']/../td[@class='numeric']/text()").get(0).toString());
		float totalPrice = 0;
		
		for (IMenuItem<HtmlInput> orderItem : randomOrder) {
			totalPrice += orderItem.getPrice();
		}
		
		if (Math.abs(subTotal - totalPrice) > 0.05)
			throw new BadOrderException("Order failed on subtotal verification, " + subTotal + " vs " + totalPrice);
		
		List<?> creditCardRoots = currentPage.getByXPath("//ul[@id='creditCardAccordion']/li[@class='accordionListItem']");
		if (creditCardRoots.size() == 0)
			throw new BadOrderException("No credit card entry found");
		
		HtmlElement creditCardRoot = (HtmlElement) creditCardRoots.get(0);
		
		HtmlInput buyButton = (HtmlInput)creditCardRoot.getByXPath(".//input[@id='cpMain_cCreditCard_rptrCreditCard_btnPay_0']").get(0);
		buyButton.click();
		webClient.waitForBackgroundJavaScript(10000);
		currentPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		
		return true;
	}
	
	private boolean setPickupLocation(String location) throws IOException {
		HtmlInput setPickupLocationInput = (HtmlInput) currentPage.getByXPath("//div[@class='lAddress' and text()='" + location + "']/../div[@class='lBtn']/input").get(0);
		if (setPickupLocationInput == null)
			return false;
		
		setPickupLocationInput.click();
		webClient.waitForBackgroundJavaScript(10000);
		currentPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		return verifyLocation("http://www.specialtys.com/mobile/CheckoutDateTime.aspx");
	}
	
	private boolean setPickupTime() throws IOException, BadOrderException {
		HtmlSelect pickupTimeSelect = (HtmlSelect) currentPage.getByXPath("//p[@id='cpMain_selectTimeSection']/select[@name='ctl00$cpMain$selTime']").get(0);
		if (!pickupTimeSelect.getSelectedOptions().get(0).getText().contains("ASAP"))
			throw new BadOrderException("No ASAP pickup time");
		
		return (goToPayment());
	}
	
	
}
