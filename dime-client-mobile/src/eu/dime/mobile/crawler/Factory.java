package eu.dime.mobile.crawler;


public class Factory {
	
	private static ContextCrawler crawler = null;
	
	public static IContextCrawler getCrawlerInstance() {
		if (crawler == null) crawler = new ContextCrawler();
		return crawler;
	}

}
