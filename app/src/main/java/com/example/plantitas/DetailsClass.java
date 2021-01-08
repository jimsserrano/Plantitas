package com.example.plantitas;

public class DetailsClass {
    public String plantname,price,type,description,deal;
    public DetailsClass() {

    }
    public DetailsClass(String plantname,String price,String type,String description,String deal ) {

        this.plantname=plantname;
        this.price=price;
        this.type=type;
        this.description=description;
        this.deal=deal;
    }

    public  String getPlantname()
    {
        return plantname;
    }
    public void setPlantname(String plantname)
    {
        this.plantname = plantname;
    }

    public  String getPrice()
    {
        return price;
    }
    public void setPrice(String price)
    {
        this.price = price;
    }

    public  String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public  String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public  String getDeal()
    {
        return deal;
    }
    public void setDeal(String deal)
    {
        this.deal = deal;
    }
}
