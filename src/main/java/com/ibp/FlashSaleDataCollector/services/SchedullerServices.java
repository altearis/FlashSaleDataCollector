package com.ibp.FlashSaleDataCollector.services;

import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.ibp.FlashSaleDataCollector.entity.GlobalVariable;
import com.ibp.FlashSaleDataCollector.entity.Products;
import com.ibp.FlashSaleDataCollector.entity.PromotionCategory;
import com.ibp.FlashSaleDataCollector.entity.TransactionHistory;
import com.ibp.FlashSaleDataCollector.model.ShopeeItemOnSale;
import com.ibp.FlashSaleDataCollector.util.AbstractManagedBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class SchedullerServices extends AbstractManagedBean {

	@Autowired
	ProductServices prdSvc;

	@Autowired
	PromotionService prmSvc;

	private final int maxQueue = 50;
	private int maxRunJob = 0, currentRunJob = 0, currentSession = 0, maxSession = 0;

	private void resetCounter(){
		GlobalVariable.jobRun = true;
		this.currentSession=0;
		this.maxSession=0;
		this.currentRunJob=0;
		this.maxRunJob=0;
	}
	@ConditionalOnProperty("scheduler.enable")
	@Scheduled(cron = "${scheduler.shopee.flashsale}")	
	@Async
	    public  CompletableFuture<Boolean> runJobShopee(){
		resetCounter();
		String apiUrl1 = GlobalVariable.allsource.stream().filter(d->d.getName().equals("shopee_api1")).findFirst().get().getUrl();
		String apiUrl2 = GlobalVariable.allsource.stream().filter(d->d.getName().equals("shopee_api2")).findFirst().get().getUrl();

		List<Long> promID = new ArrayList<Long>();
				
		JSONObject json;
		try {
			json = parseObject(null, apiUrl1,"GET");
			JSONArray array = (json.getJSONObject("data").getJSONArray("sessions"));

			List<PromotionCategory> promCat = new ArrayList<PromotionCategory>();
			 for (int i = 0; i < array.length(); i++)
			    {
			        JSONObject item = array.getJSONObject(i);
			        if (item.keySet().contains("promotionid"))
			        {
			        	PromotionCategory dt = new PromotionCategory();
			        	dt.setPromotionId(item.getLong("promotionid"));
			        	dt.setPromotionName(item.getString("name"));
			        	dt.setPromotionStart(item.getLong("start_time"));
			        	dt.setPromotionEnd(item.getLong("end_time"));
			        	promCat.add(dt);
			            promID.add(item.getLong("promotionid"));
			            this.maxSession++;
			        }
			    }
			 prmSvc.saveData(promCat);
			 for(Long pid:promID)
			 {
				this.currentSession++;
				json = parseObject(null, apiUrl2.replaceAll(";pid;", String.valueOf(pid)),"GET");
				array = (json.getJSONObject("data").getJSONArray("item_brief_list"));
				List<ShopeeItemOnSale> itemOnSaleUnGroup = new ArrayList<ShopeeItemOnSale>();
			
				for (int i = 0; i < array.length(); i++)
			    {
			        JSONObject item = array.getJSONObject(i);
			        if (item.keySet().contains("itemid"))
			        {
			        	itemOnSaleUnGroup.add( new ShopeeItemOnSale(item.getInt("catid"),item.getLong("itemid")));		        	
			        }
			    }
			 
				List<ShopeeItemOnSale> itsemOnSale = itemOnSaleUnGroup.stream()
				        .collect(Collectors.groupingBy(ShopeeItemOnSale::getCategory))
				        .entrySet().stream()
				        .map(e -> new ShopeeItemOnSale(
				                e.getKey(),
				                e.getValue().stream().map(ShopeeItemOnSale::getItem).collect(Collectors.toList())))
				        .collect(Collectors.toList());
				List<JSONObject> forApiRequest = new ArrayList<JSONObject>();
				for(ShopeeItemOnSale tmp:itsemOnSale)
				{
					if(tmp.getItemLists().size()<=maxQueue){
						JSONObject newRequest = new JSONObject();
				 		newRequest.put("promotionid", pid);
				 		newRequest.put("categoryid",tmp.getCategory());
				 		newRequest.put("itemids",tmp.getItemLists());
				 		newRequest.put("limit",tmp.getItemLists().size());
				 		newRequest.put("with_dp_items",true);
				 		forApiRequest.add(newRequest);
				 		this.maxRunJob++;
					}else{
						List<Long> stack = new ArrayList<Long>();
						List<List<Long>> topStack = new ArrayList<List<Long>>();
						int x=0;
						for(Long itemid:tmp.getItemLists()){
							x++;
							stack.add(itemid);
							if(stack.size()==50){
								topStack.add(stack);
								stack.clear();
								this.maxRunJob++;
							}
							if(x==tmp.getItemLists().size()){
								topStack.add(stack);
								this.maxRunJob++;
							}
								
						}
						for(List<Long> dt:topStack){
							JSONObject newRequest = new JSONObject();
						 	newRequest.put("promotionid", pid);
						 	newRequest.put("categoryid",tmp.getCategory());
						 	newRequest.put("itemids",dt);
						 	newRequest.put("limit",dt.size());
						 	newRequest.put("with_dp_items",true);
						 	forApiRequest.add(newRequest);
						}
					}
				}
				getItemList(forApiRequest);
				 
			}
			 return CompletableFuture.completedFuture(Boolean.TRUE);
		}catch (NumberFormatException | KeyManagementException | JSONException
				| NoSuchAlgorithmException | KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(); 
			return CompletableFuture.completedFuture(Boolean.FALSE);
		}
		
		 
	}
	@Async
	private CompletableFuture<Boolean> getItemList(List<JSONObject> itsemOnSale){

		String apiUrl3 = GlobalVariable.allsource.stream().filter(d->d.getName().equals("shopee_api3")).findFirst().get().getUrl();
		
		JSONObject json;
		JSONArray array;
		try {
				for(JSONObject tmp:itsemOnSale)
				{	
				 	try {
				 		json = parseObject(tmp, apiUrl3,"POST");
				 		this.currentRunJob++;
				 		array = (json.getJSONObject("data").getJSONArray("items"));
				 		List<Products> prods = new ArrayList<Products>();
				 		for (int i = 0; i < array.length(); i++)
				 		{
				 			JSONObject item = array.getJSONObject(i);
				 			List<TransactionHistory> his = new ArrayList<TransactionHistory>();
				 			BigDecimal discountPrice = BigDecimal.ZERO;
				 			int flashSaleStock=0;
				 			String trxHisId=String.valueOf(item.getLong("itemid"));
				 			try{				 				
				 				discountPrice = item.getBigDecimal("price");
				 			}catch(JSONException e){}				 			
				 			try{				 				
				 				flashSaleStock = item.getInt("flash_sale_stock");
				 			}catch(JSONException e){}
				 			try{				 				
				 				trxHisId = item.getLong("promotionid")+""+item.getLong("itemid")+""+flashSaleStock;
				 			}catch(JSONException e){}
				 			his.add(new TransactionHistory(trxHisId,item.getLong("itemid"),item.getLong("promotionid"),item.getBigDecimal("price_before_discount"),discountPrice,(item.getInt("stock")<1?Boolean.TRUE:Boolean.FALSE),item.getInt("raw_discount")+"%",item.getInt("stock"),flashSaleStock));
				 			prods.add(new Products(item.getLong("itemid"), item.getString("name"), String.valueOf(item.getLong("shopid")+"."+item.getLong("itemid")), item.getString("image"),item.getInt("flash_catid"), item.getBigDecimal("price_before_discount"), "shopee",his));
				 		}
				 		prdSvc.addProducts(prods);
				 		
				 	} catch (KeyManagementException | JSONException | NoSuchAlgorithmException | KeyStoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				 	log.info("Promotional Session {} out of {}, Running Job {} out of {}",this.currentSession,this.maxSession,this.currentRunJob,this.maxRunJob);
					Thread.sleep(ThreadLocalRandom.current().nextLong(1500, 5000));					
				}
				 
				 log.info("Data Grabber Complete...");
				 GlobalVariable.jobRun = false;
				return CompletableFuture.completedFuture(Boolean.TRUE);
			} catch (NumberFormatException | InterruptedException | JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace(); 
				return CompletableFuture.completedFuture(Boolean.FALSE);
			}
	}	

}
