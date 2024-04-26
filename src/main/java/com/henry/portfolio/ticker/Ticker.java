package com.henry.portfolio.ticker;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SECURITY_DEFINITIONS")
public class Ticker {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
    @Column(name="TICKER", nullable = false, unique = true)
    String ticker;
    @Column(name="SECURITY_TYPE", nullable = false)
    String securityType;
    @Column(name="STD_DEV")
    BigDecimal stdDev;
    @Column(name="EXP_RETURN")
    BigDecimal expReturn;
    @Column(name="MATURITY")
    BigDecimal maturity;
    @Column(name="STRIKE")
    BigDecimal strike;

//    public Long getId() {
//        return id;
//    }

    public String getTicker() {
        return ticker;
    }

    public String getSecurityType() {
        return securityType;
    }

    public BigDecimal getMaturity() {
        return maturity;
    }

    public BigDecimal getStrike() {
        return strike;
    }

    public BigDecimal getStdDev() {
        return stdDev;
    }

    public BigDecimal getExpReturn() {
        return expReturn;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public void setMaturity(BigDecimal maturity) {
        this.maturity = maturity;
    }

    public void setStrike(BigDecimal strike) {
        this.strike = strike;
    }

    public void setStdDev(BigDecimal stdDev) {
        this.stdDev = stdDev;
    }

    public void setExpReturn(BigDecimal expReturn) {
        this.expReturn = expReturn;
    }

}
