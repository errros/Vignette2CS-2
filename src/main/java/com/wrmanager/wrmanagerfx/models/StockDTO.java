package com.wrmanager.wrmanagerfx.models;

import lombok.*;

import java.util.Date;


@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
public class StockDTO {


    private Long product_id;

    private Float ppa;

    private String lot;

    private Date date;


    @Override
    public String toString() {
        return "StockDTO{" +
                "product_id=" + product_id +
                ", ppa=" + ppa +
                ", lot='" + lot + '\'' +
                ", date=" + date +
                '}';
    }
}
