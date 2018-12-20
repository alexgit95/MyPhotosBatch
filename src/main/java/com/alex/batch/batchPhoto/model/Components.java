
package main.java.com.alex.batch.batchPhoto.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Components {

    @SerializedName("ISO_3166-1_alpha-2")
    @Expose
    private String iSO31661Alpha2;
    @SerializedName("ISO_3166-1_alpha-3")
    @Expose
    private String iSO31661Alpha3;
    @SerializedName("_type")
    @Expose
    private String type;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("village")
    @Expose
    private String village;
    @SerializedName("town")
    @Expose
    private String town;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("postcode")
    @Expose
    private String postcode;
    @SerializedName("road_type")
    @Expose
    private String roadType;
    @SerializedName("state")
    @Expose
    private String state;

    public String getISO31661Alpha2() {
        return iSO31661Alpha2;
    }

    public void setISO31661Alpha2(String iSO31661Alpha2) {
        this.iSO31661Alpha2 = iSO31661Alpha2;
    }

    public String getISO31661Alpha3() {
        return iSO31661Alpha3;
    }

    public void setISO31661Alpha3(String iSO31661Alpha3) {
        this.iSO31661Alpha3 = iSO31661Alpha3;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    

    public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}
    
    

}
