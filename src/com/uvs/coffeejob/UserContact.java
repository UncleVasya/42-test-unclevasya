package com.uvs.coffeejob;

public class UserContact {
    private String mType;
    private String mValue;

    public UserContact() {
    	
    }
    
    public UserContact(String type, String value) {
    	setType(type);
    	setValue(value);
    }
    
    // setters
    public void setType(String type) {
        mType = type;
    }
    
    public void setValue(String value) {
        mValue = value;
    }
    
    // getters
    public String getType() {
        return mType;
    }
   
    public String getValue() {
        return mValue;
    }
    
    @Override
	public boolean equals(Object contact) {
    	UserContact uc = (UserContact) contact;
    	return (this.getType().equals(uc.getType()) && 
    			this.getValue().equals(uc.getValue()));
    }
}
