
package main.java.com.alex.batch.batchPhoto.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Currency {

    @SerializedName("alternate_symbols")
    @Expose
    private List<Object> alternateSymbols = null;
    @SerializedName("decimal_mark")
    @Expose
    private String decimalMark;
    @SerializedName("html_entity")
    @Expose
    private String htmlEntity;
    @SerializedName("iso_code")
    @Expose
    private String isoCode;
    @SerializedName("iso_numeric")
    @Expose
    private Integer isoNumeric;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("smallest_denomination")
    @Expose
    private Integer smallestDenomination;
    @SerializedName("subunit")
    @Expose
    private String subunit;
    @SerializedName("subunit_to_unit")
    @Expose
    private Integer subunitToUnit;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("symbol_first")
    @Expose
    private Integer symbolFirst;
    @SerializedName("thousands_separator")
    @Expose
    private String thousandsSeparator;

    public List<Object> getAlternateSymbols() {
        return alternateSymbols;
    }

    public void setAlternateSymbols(List<Object> alternateSymbols) {
        this.alternateSymbols = alternateSymbols;
    }

    public String getDecimalMark() {
        return decimalMark;
    }

    public void setDecimalMark(String decimalMark) {
        this.decimalMark = decimalMark;
    }

    public String getHtmlEntity() {
        return htmlEntity;
    }

    public void setHtmlEntity(String htmlEntity) {
        this.htmlEntity = htmlEntity;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Integer getIsoNumeric() {
        return isoNumeric;
    }

    public void setIsoNumeric(Integer isoNumeric) {
        this.isoNumeric = isoNumeric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSmallestDenomination() {
        return smallestDenomination;
    }

    public void setSmallestDenomination(Integer smallestDenomination) {
        this.smallestDenomination = smallestDenomination;
    }

    public String getSubunit() {
        return subunit;
    }

    public void setSubunit(String subunit) {
        this.subunit = subunit;
    }

    public Integer getSubunitToUnit() {
        return subunitToUnit;
    }

    public void setSubunitToUnit(Integer subunitToUnit) {
        this.subunitToUnit = subunitToUnit;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getSymbolFirst() {
        return symbolFirst;
    }

    public void setSymbolFirst(Integer symbolFirst) {
        this.symbolFirst = symbolFirst;
    }

    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    public void setThousandsSeparator(String thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

}
