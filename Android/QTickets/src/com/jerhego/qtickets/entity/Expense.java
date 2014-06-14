package com.jerhego.qtickets.entity;

import java.util.Date;

public class Expense {
	
	private int IdExpense;
	private int GuaranteePeriod;
	private int IdTicket;
	private String Name;
	private double Price;
	private int IdCategory;
    private Date PurchaseDate;
    private String ShopNIF;
    private String ShopName;
    private int IdUser;
    private int IdGroup;
    private Date CreationDate;
    
	public int getIdExpense() {
		return IdExpense;
	}
	public void setIdExpense(int idExpense) {
		IdExpense = idExpense;
	}
	public int getGuaranteePeriod() {
		return GuaranteePeriod;
	}
	public void setGuaranteePeriod(int guaranteePeriod) {
		GuaranteePeriod = guaranteePeriod;
	}
	public int getIdTicket() {
		return IdTicket;
	}
	public void setIdTicket(int idTicket) {
		IdTicket = idTicket;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	public int getIdCategory() {
		return IdCategory;
	}
	public void setIdCategory(int idCategory) {
		IdCategory = idCategory;
	}
	public Date getPurchaseDate() {
		return PurchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		PurchaseDate = purchaseDate;
	}	
	public String getShopNIF() {
		return ShopNIF;
	}
	public void setShopNIF(String shopNIF) {
		ShopNIF = shopNIF;
	}
	public String getShopName() {
		return ShopName;
	}
	public void setShopName(String shopName) {
		ShopName = shopName;
	}
	public int getIdUser() {
		return IdUser;
	}
	public void setIdUser(int idUser) {
		IdUser = idUser;
	}
	public int getIdGroup() {
		return IdGroup;
	}
	public void setIdGroup(int idGroup) {
		IdGroup = idGroup;
	}
	public Date getCreationDate() {
		return CreationDate;
	}
	public void setCreationDate(Date creationDate) {
		CreationDate = creationDate;
	}
    
    

}
