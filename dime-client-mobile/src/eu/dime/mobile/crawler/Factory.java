package eu.dime.mobile.crawler;

import eu.dime.mobile.helper.interfaces.IContextCrawler;

public class Factory {
	
	private static ContextCrawler crawler = null;
	
	public static IContextCrawler getCrawlerInstance() {
		if (crawler == null) crawler = new ContextCrawler();
		return crawler;
	}

}
