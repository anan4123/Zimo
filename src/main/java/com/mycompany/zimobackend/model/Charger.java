package com.mycompany.zimobackend.model;

public class Charger {
    private int id;
    private int stationId;
    private String chargerType;
    private String status;
    
    public Charger() {}
    
    public Charger(int id, int stationId, String chargerType, String status){
        this.id = id;
        this.stationId = stationId;
        this.chargerType = chargerType;
        this.status = status;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getStationId(){
        return stationId;
    }
    
    public void setStationId(int stationId){
        this.stationId = stationId;
    }
    
    public String getChargerType(){
        return chargerType;
    }
    
    public void setChargerType(String chargerType){
        this.chargerType = chargerType;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }

}
