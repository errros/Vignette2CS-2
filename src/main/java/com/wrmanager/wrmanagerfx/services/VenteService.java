package com.wrmanager.wrmanagerfx.services;

import com.wrmanager.wrmanagerfx.entities.Achat;
import com.wrmanager.wrmanagerfx.entities.Fournisseur;

import com.wrmanager.wrmanagerfx.entities.Vente;
import com.wrmanager.wrmanagerfx.entities.Stock;
import com.wrmanager.wrmanagerfx.models.StockDTO;

import java.util.*;

import static com.wrmanager.wrmanagerfx.Constants.*;

public class VenteService {


     public Optional<Stock> getStockFromCamera(StockDTO operation){


          var productId= operation.getProduct_id();

          var lot = operation.getLot();


         var stocks = stockDAO.getWithProductId(productId);

         // no stock in db
         if(stocks.isEmpty()){
             return Optional.empty();
         }


         //check if has same lot
         if(!lot.isEmpty()){
            var stockOptional = stocks.stream().filter(stock1 -> stock1.getLot().equals(lot)).findAny();
            if(stockOptional.isPresent()){
                return stockOptional;
            }

         }


         //check if has same ddp
         if(Optional.ofNullable(operation.getDate()).isPresent()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(operation.getDate());

            int monthOperation = calendar.get(Calendar.MONTH) + 1;  // Months are zero-based, so add 1
             int yearOperation = calendar.get(Calendar.YEAR);

             var stockOptional = stocks.stream().filter(stock -> {
                 Calendar calendar2 = Calendar.getInstance();
                 calendar2.setTime(operation.getDate());
                 int month = calendar2.get(Calendar.MONTH) + 1;  // Months are zero-based, so add 1
                 int year = calendar2.get(Calendar.YEAR);
                 return year == yearOperation && month == monthOperation;
             }).findAny();

             if(stockOptional.isPresent()){
                  return stockOptional;
             }
         }
                return Optional.ofNullable(stocks.get(0));


     }

    public List<Vente> getAll() {
        return venteDAO.getAll().stream().toList();
    }
    public void sortRowsByLastTimeAdded(){

        ventesList.sort(Comparator.comparing(Vente::getCreeLe).reversed());

    }


}
