package com.ibp.FlashSaleDataCollector.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ibp.FlashSaleDataCollector.entity.GlobalVariable;
import com.ibp.FlashSaleDataCollector.entity.Products;
import com.ibp.FlashSaleDataCollector.entity.TransactionHistory;
import com.ibp.FlashSaleDataCollector.entity.Websource;
import com.ibp.FlashSaleDataCollector.util.AbstractManagedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibp.FlashSaleDataCollector.repo.ProductRepo;

@Service
public class ProductServices extends AbstractManagedBean {
	@Autowired
	private ProductRepo prdRepo;
	
	public Boolean addProduct(Products prd, TransactionHistory trx){
		try{
			log.info("Saving data prd -> {}",prd);
				if(prd != null && trx != null){
					List<TransactionHistory> listTrx = new ArrayList<TransactionHistory>();
					listTrx.add(trx);
					prd.setTransactions(listTrx);
					prdRepo.save(prd);
					return true;
				}
			}
			catch(Exception e){
				log.error("",e);
				return false;
			
			}
		return false;
	}	
	public void addProducts(List<Products> prd){
		List<Websource> app = GlobalVariable.allsource;//.stream().filter(d->d.getName().equals("shopee")).findFirst().get();
		String pathUrl,fileUrl;
		BigDecimal divisor = BigDecimal.valueOf(Long.parseLong("100000"));
		pathUrl = app.stream().filter(d->d.getName().equals("shopeeUrl")).findFirst().get().getUrl();
		fileUrl = app.stream().filter(d->d.getName().equals("shopeeImg")).findFirst().get().getUrl();

		List<Products> finalProduct = new ArrayList<Products>();
		for(Products tmp:prd)
		{
			if(tmp.getProductSource().equals("shopee")){
				tmp.setProductPrice(tmp.getProductPrice().divide(divisor));
				tmp.setProductUrl(pathUrl+CleanUpURL(tmp.getProductName()+"-i."+tmp.getProductUrl()));
				tmp.setProductImage(fileUrl+tmp.getProductImage());
				List<TransactionHistory> trxHis = new ArrayList<TransactionHistory>(); 
				for(TransactionHistory tmpHis :tmp.getTransactions()){
					tmpHis.setDiscountPrice(tmpHis.getDiscountPrice().divide(divisor));
					tmpHis.setPrice(tmpHis.getPrice().divide(divisor));
					trxHis.add(tmpHis);
				}
				tmp.setTransactions(trxHis);
			}
			finalProduct.add(tmp);
		}
		if(finalProduct.size()>0){
			log.info("{} rows saved",finalProduct.size());
			prdRepo.saveAll(finalProduct);
		}
	}
}
