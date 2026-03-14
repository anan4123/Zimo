/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zimobackend.model;

public class Charger {
    private int id;
    private int stationId;
    private String charger_type;
    private String status;
    private int battery_level;
    
    public Charger() {}
    
    public Charger(int id, int stationId, String charger_type, String status, int battery_level){
        this.id = id;
        this.stationId = stationId;
        this.charger_type = charger_type;
        this.status = status;
        this.battery_level = battery_level;
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
        return charger_type;
    }
    
    public void setChargerType(String charger_type){
        this.charger_type = charger_type;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public int getBatteryLevel(){
        return battery_level;
    }
    
    public void setBatteryLevel(int battery_level){
        this.battery_level = battery_level;
    }

}
