package com.wrmanager.wrmanagerfx.services;

import com.wrmanager.wrmanagerfx.entities.Categorie;
import com.wrmanager.wrmanagerfx.entities.Produit;
import com.wrmanager.wrmanagerfx.entities.Stock;

import java.util.Comparator;
import java.util.List;

import static com.wrmanager.wrmanagerfx.Constants.*;

public class StockService {




    public Stock save (Produit produit , Stock stock){

         produit.addStock(stock);


        var ret = stockDAO.save(stock);
        stockList.add(ret);
        sortRowsByLastTimeAdded();

        return ret;



    }

    //test
    public Stock update (Stock stock){


        var ret=   stockDAO.update(stock);


        // ****
             // Refresh Stock Table
        //****
        //produitsViewController.getProduitsTable().refresh();
        //stockViewController.getStockTable().refresh();
        return ret;
    }


    public void delete(Stock stock){

        stockDAO.deleteById(stock.getId());
        stockList.remove(stock);
        sortRowsByLastTimeAdded();

    }


    public List<Stock> getAll(){


        var list = stockDAO.getAll().stream().toList();
        return list;

    }



    private void sortRowsByLastTimeAdded() {

        stockList.sort(Comparator.comparing(Stock::getCreeLe).reversed());

    }


}
