package com.ibp.FlashSaleDataCollector.services;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import com.ibp.FlashSaleDataCollector.entity.GlobalVariable;
import com.ibp.FlashSaleDataCollector.entity.Products;
import com.ibp.FlashSaleDataCollector.entity.Websource;
import com.ibp.FlashSaleDataCollector.util.AbstractManagedBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Service
public class WebCrawler extends AbstractManagedBean {

	@Autowired
	ProductServices prdSvc;
	
	
	public void test2() {
		// TODO Auto-generated method stub
		String id,url,nama,image,soldout;
		Websource app = GlobalVariable.allsource.stream().filter(d->d.getName().equals("shopee")).findFirst().get();
		BigDecimal harga,diskon;
		Integer terjual,currentHeight=8000,scrollHeight=8000,maxHeight = 20000,sleepTime=2000;
		
		log.info("test connection to {}",app.getName());
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
		webClient.getOptions().setThrowExceptionOnScriptError(false);

	    try {
			HtmlPage myPage = webClient.getPage(app.getUrl());

	        webClient.waitForBackgroundJavaScriptStartingBefore(30000);
	    while(currentHeight<maxHeight){	    	
	    	try {	    		
	    		webClient.getCurrentWindow().setInnerHeight(currentHeight);	    		
	    		myPage.wait(sleepTime);
	    		currentHeight = currentHeight+scrollHeight;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

		Document doc;
		try {
			//myPage.g
			File input = new File("D:/Output/test/index.html");
			//Jsoup.par
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
			Elements links = doc.getElementsByClass("page-flash-sale__main");
			//doc = Jsoup.connect("https://shopee.co.id/flash_sale").get();
		log.info("Total ->{}",links.get(0).child(1).getAllElements().get(1).child(1));
		log.info(doc.title());
		
		for (Element headline : links.get(0).child(1).getAllElements().get(1).children()) {
				try{
			url = headline.getElementsByTag("a").attr("href");
			id = extractString(url,digitRegex).get(1).substring(1);
			nama = headline.child(0).child(1).getElementsByTag("div").attr("title");
			image = extractString(headline.child(0).child(0).getElementsByTag("div").attr("style"),urlRegex).get(0);
			harga = new BigDecimal(headline.child(0).child(2).child(0).getElementsByTag("div").text().replaceAll("[^\\d]", ""));
			diskon = new BigDecimal(headline.child(0).child(3).child(0).child(0).child(0).getElementsByTag("div").text().replaceAll("[^\\d]", ""));
			terjual = Integer.parseInt(extractString(headline.child(0).child(3).child(0).child(1).text(),"(\\d+)").get(0));
			soldout = headline.child(0).child(0).child(0).children().text();
			Products prd = new Products(Long.valueOf(id), nama, url, image,1, harga, "Shopee");

				  log.info("id-->{}",id);
				  log.info("url-->{}",url);
				  log.info("nama-->{}",nama);
				  log.info("image-->{}",image);
				  log.info("harga-->{}",harga);
				  log.info("harga diskon-->{}",diskon);
				  log.info("terjual-->{}",terjual);
				  log.info("soldout-->{}",(soldout.trim().length()>1));

				}catch(IndexOutOfBoundsException | NullPointerException ie){
					log.error("",ie);
				}
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// clean up resources        
	    webClient.close();
	    } catch (FailingHttpStatusCodeException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
