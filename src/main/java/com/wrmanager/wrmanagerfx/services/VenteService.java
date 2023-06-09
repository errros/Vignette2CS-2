package com.wrmanager.wrmanagerfx.services;

import com.wrmanager.wrmanagerfx.entities.Achat;
import com.wrmanager.wrmanagerfx.entities.Fournisseur;
import com.wrmanager.wrmanagerfx.entities.Stock;
import com.wrmanager.wrmanagerfx.entities.Vente;
import com.wrmanager.wrmanagerfx.models.StockDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.wrmanager.wrmanagerfx.Constants.*;

public class VenteService {


     public Optional<Stock> vendre(StockDTO operation){

       if(operation.getProduct_id() != 0) {
           var s =produitDAO.getById(operation.getProduct_id());
          if (s.isPresent()
                  && s.get().getStocks().stream().
                  filter(stock -> stock.getLot().equals(operation.getLot())).findAny().isPresent()){
              var ss =s.get().getStocks().stream().
                      filter(stock -> stock.getLot().equals(operation.getLot())).findAny().get();

              ss.setQty(ss.getQty() -1);
              System.out.println("nouveau stock before sql : " + ss);
              return Optional.of(stockDAO.update(ss));
          }
       }

       return Optional.empty();
     }

    public List<Vente> getAll() {
        return venteDAO.getAll().stream().toList();
    }
    public void sortRowsByLastTimeAdded(){

        ventesList.sort(Comparator.comparing(Vente::getCreeLe).reversed());

    }


}
