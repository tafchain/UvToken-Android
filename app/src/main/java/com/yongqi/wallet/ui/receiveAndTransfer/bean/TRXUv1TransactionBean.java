package com.yongqi.wallet.ui.receiveAndTransfer.bean;

public class TRXUv1TransactionBean {

    /**
     * txid : a1a8c99d172376407edea59dfc92f35d085c8586afff2d85a00c3b9e29f3fbfb
     * from : TXGaPLY9zaeW8VcvuhQp4dYkAKVPqSJqJp
     * to : TEoh2zhNeGDnfJ7NtNQsDPAWga8iGBb8QR
     * contract_address :
     * amount : 10
     * data :
     * blockhash : 15482785
     * blocktime : 1622787609000
     * fee : 100000
     */

    private String txid;
    private String from;
    private String to;
    private String contract_address;
    private String amount;
    private String data;
    private Long blockhash;
    private Long blocktime;
    private String result;
    private String fee;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContract_address() {
        return contract_address;
    }

    public void setContract_address(String contract_address) {
        this.contract_address = contract_address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(Long blockhash) {
        this.blockhash = blockhash;
    }

    public Long getBlocktime() {
        return blocktime;
    }

    public void setBlocktime(Long blocktime) {
        this.blocktime = blocktime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
