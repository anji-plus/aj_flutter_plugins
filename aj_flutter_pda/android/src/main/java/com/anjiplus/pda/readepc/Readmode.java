package com.anjiplus.pda.readepc;

import java.io.Serializable;

/**
 * Read EPC
 */
public class Readmode implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * EPC
     */
    private String EPCNo = "";
    /**
     * TID
     */
    private String TIDNo = "";
    /**
     * lables
     */
    private String CountNo = "";

    public String getEPCNo() {
        return EPCNo;
    }

    public void setEPCNo(String epcNo) {
        EPCNo = epcNo;
    }

    //
    public String getTIDNo() {
        return TIDNo;
    }

    public void setTIDNo(String tidNo) {
        TIDNo = tidNo;
    }

    public String getCountNo() {
        return CountNo;
    }

    public void setCountNo(String countNo) {
        CountNo = countNo;
    }

}
