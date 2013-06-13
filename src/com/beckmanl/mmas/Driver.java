package com.beckmanl.mmas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.beckmanl.mmas.exceptions.BadLoginException;
import com.beckmanl.mmas.exceptions.BadOrderException;
import com.beckmanl.mmas.exceptions.NotSUException;
import com.beckmanl.mmas.exceptions.UndefinedConfigException;
import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public class Driver {

	private static final String CONFIG_FILE_NAME = "mmas.config";
	
	public static void main(String[] args) {
		try {
			WebClient webClient = getWebClient();
			ConfigManager config = new ConfigManager(new URI("file:///C:/Users/beckmanl/Documents/GitHub/makemeasandwich/res/mmas.config"));
			
			SpecialtysBrowser specialtysBrowser = new SpecialtysBrowser(webClient, config);
			specialtysBrowser.doOrder();
			
			System.out.println("OKAY");
			
		} catch (NotSUException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (BadLoginException e) {
			e.printStackTrace();
		} catch (BadOrderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UndefinedConfigException e) {
			e.printStackTrace();
		}
	}
	
	//DONE
	private static WebClient getWebClient() {
		WebClient webClient = new WebClient();
		
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
	    java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

	    webClient.setIncorrectnessListener(new IncorrectnessListener() {

	        @Override
	        public void notify(String arg0, Object arg1) {

	        }
	    });
	    webClient.setCssErrorHandler(new ErrorHandler() {

	        @Override
	        public void warning(CSSParseException exception) throws CSSException {

	        }

	        @Override
	        public void fatalError(CSSParseException exception) throws CSSException {

	        }

	        @Override
	        public void error(CSSParseException exception) throws CSSException {

	        }
	    });
	    webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {

	        @Override
	        public void timeoutError(HtmlPage arg0, long arg1, long arg2) {

	        }

	        @Override
	        public void scriptException(HtmlPage arg0, ScriptException arg1) {

	        }

	        @Override
	        public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {

	        }

	        @Override
	        public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {

	        }
	    });
	    webClient.setHTMLParserListener(new HTMLParserListener() {

			@Override
			public void error(String arg0, URL arg1, String arg2, int arg3,
					int arg4, String arg5) {
				
			}

			@Override
			public void warning(String arg0, URL arg1, String arg2, int arg3,
					int arg4, String arg5) {
				
			}
	    });

		webClient.setAlertHandler(new AlertHandler() {
			@Override
			public void handleAlert(Page arg0, String arg1) {
			}
		});
		
		return webClient;
	}

}
