/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zimobackend.model;


public class Feedback {
    private String topic;
    private String suggestion;
    private String recommendation;  
    
    public Feedback(){}
    
    public Feedback(String topic, String suggestion, String recommendation){
        this.topic = topic;
        this.suggestion = suggestion;
        this.recommendation = recommendation;
    }
    
    public String getTopic(){
        return topic;
    }
    
    public void setTopic(String topic){
        this.topic = topic;
    }
    
    public String getSuggestion(){
        return suggestion;
    }
    
    public void setSuggestion(String suggestion){
        this.suggestion = suggestion;
    }
    
    public String getRecommendation(){
        return recommendation;
    }
    
    public void setRecommendation(String recommendation){
        this.recommendation = recommendation;
    }
}
