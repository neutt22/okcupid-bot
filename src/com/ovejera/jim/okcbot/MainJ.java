package com.ovejera.jim.okcbot;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MainJ {
	
	public static void main(String args[]) throws FailingHttpStatusCodeException, IOException{
		
		String a = "Pansexual, Genderqueer, Non-binary,";
		
		System.out.println(a.contains("Genderqueer"));
	}

}
//HtmlElement customTextArea = (HtmlElement) profilePage.createElement("textarea");
//customTextArea.setAttribute("id", "message_2136796563149912323");
//customTextArea.setAttribute("placeholder", "Compose your message");
//customTextArea.setAttribute("style", "height: 21.2222px;");
//customTextArea.setTextContent("hello poh");
//
//HtmlElement customFlatButton = (HtmlElement) profilePage.createElement("button");
//customFlatButton.setAttribute("type", "submit");
//customFlatButton.setAttribute("class", "flatbutton");
//customFlatButton.setTextContent("Send");
//
//HtmlElement customForm = (HtmlElement) profilePage.createElement("form");
//customForm.setAttribute("class", "compose  okform initialized");
//
//HtmlElement customDiv = (HtmlElement) profilePage.createElement("div");
//customDiv.setAttribute("id", "message_2136796563149912323Container");
//customDiv.setAttribute("class", "inputcontainer textarea");
//
//customDiv.appendChild(customTextArea);
//
//customForm.appendChild(customDiv);
//customForm.appendChild(customFlatButton);
//
//HtmlElement customRootDiv = (HtmlElement) profilePage.createElement("div");
//customRootDiv.setAttribute("data-userid", "2136796563149912323");
//customRootDiv.setAttribute("class", "global_messaging no_messages");
//customRootDiv.setAttribute("style", "left: 0px");
//
//customRootDiv.appendChild(customForm);
//
//HtmlBody body = (HtmlBody) profilePage.getElementById("p_profile");
//body.appendChild(customRootDiv);
//
//System.out.println("Sendin shit...");
//HtmlPage p = customFlatButton.click(); // mobile!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//p.asText();
//System.out.println("cliked...");
//web.waitForBackgroundJavaScript(5000);
//System.out.println("Done.");
