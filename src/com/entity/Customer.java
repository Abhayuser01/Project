package com.entity;

public class Customer {
	  private int customerID;
	    private String name;
	    private String email;
	    private String phone;

	    // Constructors
	    public Customer() {}

	    public Customer(int customerID, String name, String email, String phone) {
	        this.customerID = customerID;
	        this.name = name;
	        this.email = email;
	        this.phone = phone;
	    }

	    // Getters and Setters
	    public int getCustomerID() {
	        return customerID;
	    }

	    public void setCustomerID(int customerID) {
	        this.customerID = customerID;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getPhone() {
	        return phone;
	    }

	    public void setPhone(String phone) {
	        this.phone = phone;
	    }

	    @Override
		public String toString() {
			return "Customer [customerID=" + customerID + ", name=" + name + ", email=" + email + ", phone=" + phone
					+ "]";
		}

}
