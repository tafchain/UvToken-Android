package com.yongqi.wallet.ui.receiveAndTransfer.bean;

import java.util.List;

public class TRXTransactionBean {

    /**
     * data : [{"ret":[{"contractRet":"SUCCESS","fee":100000}],"signature":["e44c29c64d4f32d989e4aa03c01f974d7a4cb1ac6be83092a9b2a7a58df38e104e0722a22479a46364fc8bd262668f30a4238b2323cf66a0aebe8e9343585d7b00"],"txID":"a1a8c99d172376407edea59dfc92f35d085c8586afff2d85a00c3b9e29f3fbfb","net_usage":0,"raw_data_hex":"0a023f8e220836b97360eceb5aa840d0e0baad9d2f5a65080112610a2d747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e5472616e73666572436f6e747261637412300a1541e9a3903902f1f584e63a78e3171631504be8e521121541350bded243be72edee69520a6e89ab63935c9946180a70ee91b7ad9d2f","net_fee":100000,"energy_usage":0,"blockNumber":15482785,"block_timestamp":1622787609000,"energy_fee":0,"energy_usage_total":0,"raw_data":{"contract":[{"parameter":{"value":{"amount":10,"owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","to_address":"41350bded243be72edee69520a6e89ab63935c9946"},"type_url":"type.googleapis.com/protocol.TransferContract"},"type":"TransferContract"}],"ref_block_bytes":"3f8e","ref_block_hash":"36b97360eceb5aa8","expiration":1622787666000,"timestamp":1622787606766},"internal_transactions":[]},{"ret":[{"contractRet":"SUCCESS","fee":37660}],"signature":["b8b6677d2af09adb1762f8daab2cf58aa6cfc86dfc940a8f38dc76a3a38337be3b6e76f300c785c2515f3a296232c53f5abf2d90fbc83868b5394c35077255cf01"],"txID":"008a02e3db09e97afd032289acd86ed9ee45cd05ac06f84ea63c61c6cb309039","net_usage":0,"raw_data_hex":"0a023f5a220860465f53d94c8a7f4080efb0ad9d2f5a69080112650a2d747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e5472616e73666572436f6e747261637412340a154141f0a2b01c950b4d38c9abfe9b96579dea065168121541e9a3903902f1f584e63a78e3171631504be8e5211880a8d6b90770a2a4adad9d2f","net_fee":37660,"energy_usage":0,"blockNumber":15482733,"block_timestamp":1622787447000,"energy_fee":0,"energy_usage_total":0,"raw_data":{"contract":[{"parameter":{"value":{"amount":2000000000,"owner_address":"4141f0a2b01c950b4d38c9abfe9b96579dea065168","to_address":"41e9a3903902f1f584e63a78e3171631504be8e521"},"type_url":"type.googleapis.com/protocol.TransferContract"},"type":"TransferContract"}],"ref_block_bytes":"3f5a","ref_block_hash":"60465f53d94c8a7f","expiration":1622787504000,"timestamp":1622787445282},"internal_transactions":[]},{"ret":[{"contractRet":"SUCCESS","fee":917980}],"signature":["fcd542a0ee08dd5ef700cd094d69b895b7b4a592ac7a388ec220e392f81b409c6f70f48eef9b436e4c1ccd539592b2fa8f8037f1dc1012ffa840fc846639cd5c01"],"txID":"ce29ca0b3a936a17f581895e56fc8a2a8f89218382ac633554cba8db3ffe4c76","net_usage":345,"raw_data_hex":"0a02d15c2208a92e9a40efc5101240d0fdd8849d2f5aae01081f12a9010a31747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e54726967676572536d617274436f6e747261637412740a1541e9a3903902f1f584e63a78e3171631504be8e521121541ee4f434f249619c0de8527f14a858654ce3389392244a9059cbb000000000000000000000000e11973395042ba3c0b52b4cdf4e15ea77818f275000000000000000000000000000000000000000000000000000000000098968070a5b5d5849d2f900180ade204","net_fee":0,"energy_usage":0,"blockNumber":15454580,"block_timestamp":1622702136000,"energy_fee":917980,"energy_usage_total":13114,"raw_data":{"contract":[{"parameter":{"value":{"data":"a9059cbb000000000000000000000000e11973395042ba3c0b52b4cdf4e15ea77818f2750000000000000000000000000000000000000000000000000000000000989680","owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","contract_address":"41ee4f434f249619c0de8527f14a858654ce338939"},"type_url":"type.googleapis.com/protocol.TriggerSmartContract"},"type":"TriggerSmartContract"}],"ref_block_bytes":"d15c","ref_block_hash":"a92e9a40efc51012","expiration":1622702178000,"fee_limit":10000000,"timestamp":1622702119589},"internal_transactions":[]},{"ret":[{"contractRet":"SUCCESS","fee":1967980}],"signature":["ca9b88a4868589b984576a3ec773ba8c66099b36f385456e711b4eabe4ee3e7867670d25d7543036b0c2ad472268d6602d82ad18303ad95b1f73deed9566532301"],"txID":"75b61f5842ba02030fa84abf72b9fa7559d6e67463dce1ace2175a31654dd713","net_usage":345,"raw_data_hex":"0a02514c2208e409833d766683e940a0d0a9d59c2f5aae01081f12a9010a31747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e54726967676572536d617274436f6e747261637412740a1541e9a3903902f1f584e63a78e3171631504be8e521121541ee4f434f249619c0de8527f14a858654ce3389392244a9059cbb000000000000000000000000e11973395042ba3c0b52b4cdf4e15ea77818f27500000000000000000000000000000000000000000000000000000000000f424070ce8da6d59c2f900180ade204","net_fee":0,"energy_usage":0,"blockNumber":15421805,"block_timestamp":1622602821000,"energy_fee":1967980,"energy_usage_total":28114,"raw_data":{"contract":[{"parameter":{"value":{"data":"a9059cbb000000000000000000000000e11973395042ba3c0b52b4cdf4e15ea77818f27500000000000000000000000000000000000000000000000000000000000f4240","owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","contract_address":"41ee4f434f249619c0de8527f14a858654ce338939"},"type_url":"type.googleapis.com/protocol.TriggerSmartContract"},"type":"TriggerSmartContract"}],"ref_block_bytes":"514c","ref_block_hash":"e409833d766683e9","expiration":1622602836000,"fee_limit":10000000,"timestamp":1622602778318},"internal_transactions":[]},{"ret":[{"contractRet":"SUCCESS","fee":0}],"signature":["1e8801e77e83458cde13539415c0a0013870583f25e0da8e72331fbb22e5343b603b90397ea4c8f890299f0932291563b19c2f4713b9294d84815d452624223c00"],"txID":"055b8484f02d3ac3d688f62aa5219c14a508ea13a8865e0ddacb3fdd2c62889c","net_usage":268,"raw_data_hex":"0a02505822082bf4d4218427b7c640e09bfcd49c2f5a68080112640a2d747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e5472616e73666572436f6e747261637412330a1541e9a3903902f1f584e63a78e3171631504be8e521121541e11973395042ba3c0b52b4cdf4e15ea77818f2751880ade20470c2d2f8d49c2f","net_fee":0,"energy_usage":0,"blockNumber":15421563,"block_timestamp":1622602089000,"energy_fee":0,"energy_usage_total":0,"raw_data":{"contract":[{"parameter":{"value":{"amount":10000000,"owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","to_address":"41e11973395042ba3c0b52b4cdf4e15ea77818f275"},"type_url":"type.googleapis.com/protocol.TransferContract"},"type":"TransferContract"}],"ref_block_bytes":"5058","ref_block_hash":"2bf4d4218427b7c6","expiration":1622602092000,"timestamp":1622602033474},"internal_transactions":[]},{"ret":[{"contractRet":"SUCCESS","fee":100000}],"signature":["69d27d787e590f5956daeadc699c0ab52977b5d42f0d58f174816d115454dd219d02ce5a5330c2caabba13bd5cde9dbcb232985a042977ac115bd9c66b523b4f01"],"txID":"d668774194e5674740b80f8b6469c02be1da4af6821015addeea784ac7be9b5c","net_usage":0,"raw_data_hex":"0a024fe72208cd14e0ba2269357540a8c3e7d49c2f5a69080112650a2d747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e5472616e73666572436f6e747261637412340a154141f0a2b01c950b4d38c9abfe9b96579dea065168121541e9a3903902f1f584e63a78e3171631504be8e5211880a8d6b9077087f0e3d49c2f","net_fee":100000,"energy_usage":0,"blockNumber":15421434,"block_timestamp":1622601696000,"energy_fee":0,"energy_usage_total":0,"raw_data":{"contract":[{"parameter":{"value":{"amount":2000000000,"owner_address":"4141f0a2b01c950b4d38c9abfe9b96579dea065168","to_address":"41e9a3903902f1f584e63a78e3171631504be8e521"},"type_url":"type.googleapis.com/protocol.TransferContract"},"type":"TransferContract"}],"ref_block_bytes":"4fe7","ref_block_hash":"cd14e0ba22693575","expiration":1622601753000,"timestamp":1622601693191},"internal_transactions":[]}]
     * success : true
     * meta : {"at":1622798931978,"page_size":6}
     */

    private boolean success;
    private MetaBean meta;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class MetaBean {
        /**
         * at : 1622798931978
         * page_size : 6
         */

        private long at;
        private int page_size;

        public long getAt() {
            return at;
        }

        public void setAt(long at) {
            this.at = at;
        }

        public int getPage_size() {
            return page_size;
        }

        public void setPage_size(int page_size) {
            this.page_size = page_size;
        }
    }

    public static class DataBean {
        /**
         * ret : [{"contractRet":"SUCCESS","fee":100000}]
         * signature : ["e44c29c64d4f32d989e4aa03c01f974d7a4cb1ac6be83092a9b2a7a58df38e104e0722a22479a46364fc8bd262668f30a4238b2323cf66a0aebe8e9343585d7b00"]
         * txID : a1a8c99d172376407edea59dfc92f35d085c8586afff2d85a00c3b9e29f3fbfb
         * net_usage : 0
         * raw_data_hex : 0a023f8e220836b97360eceb5aa840d0e0baad9d2f5a65080112610a2d747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e5472616e73666572436f6e747261637412300a1541e9a3903902f1f584e63a78e3171631504be8e521121541350bded243be72edee69520a6e89ab63935c9946180a70ee91b7ad9d2f
         * net_fee : 100000
         * energy_usage : 0
         * blockNumber : 15482785
         * block_timestamp : 1622787609000
         * energy_fee : 0
         * energy_usage_total : 0
         * raw_data : {"contract":[{"parameter":{"value":{"amount":10,"owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","to_address":"41350bded243be72edee69520a6e89ab63935c9946"},"type_url":"type.googleapis.com/protocol.TransferContract"},"type":"TransferContract"}],"ref_block_bytes":"3f8e","ref_block_hash":"36b97360eceb5aa8","expiration":1622787666000,"timestamp":1622787606766}
         * internal_transactions : []
         */

        private String txID;
        private int net_usage;
        private String raw_data_hex;
        private int net_fee;
        private int energy_usage;
        private int blockNumber;
        private long block_timestamp;
        private int energy_fee;
        private int energy_usage_total;
        private RawDataBean raw_data;
        private List<RetBean> ret;
        private List<String> signature;
        private List<?> internal_transactions;

        public String getTxID() {
            return txID;
        }

        public void setTxID(String txID) {
            this.txID = txID;
        }

        public int getNet_usage() {
            return net_usage;
        }

        public void setNet_usage(int net_usage) {
            this.net_usage = net_usage;
        }

        public String getRaw_data_hex() {
            return raw_data_hex;
        }

        public void setRaw_data_hex(String raw_data_hex) {
            this.raw_data_hex = raw_data_hex;
        }

        public int getNet_fee() {
            return net_fee;
        }

        public void setNet_fee(int net_fee) {
            this.net_fee = net_fee;
        }

        public int getEnergy_usage() {
            return energy_usage;
        }

        public void setEnergy_usage(int energy_usage) {
            this.energy_usage = energy_usage;
        }

        public int getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(int blockNumber) {
            this.blockNumber = blockNumber;
        }

        public long getBlock_timestamp() {
            return block_timestamp;
        }

        public void setBlock_timestamp(long block_timestamp) {
            this.block_timestamp = block_timestamp;
        }

        public int getEnergy_fee() {
            return energy_fee;
        }

        public void setEnergy_fee(int energy_fee) {
            this.energy_fee = energy_fee;
        }

        public int getEnergy_usage_total() {
            return energy_usage_total;
        }

        public void setEnergy_usage_total(int energy_usage_total) {
            this.energy_usage_total = energy_usage_total;
        }

        public RawDataBean getRaw_data() {
            return raw_data;
        }

        public void setRaw_data(RawDataBean raw_data) {
            this.raw_data = raw_data;
        }

        public List<RetBean> getRet() {
            return ret;
        }

        public void setRet(List<RetBean> ret) {
            this.ret = ret;
        }

        public List<String> getSignature() {
            return signature;
        }

        public void setSignature(List<String> signature) {
            this.signature = signature;
        }

        public List<?> getInternal_transactions() {
            return internal_transactions;
        }

        public void setInternal_transactions(List<?> internal_transactions) {
            this.internal_transactions = internal_transactions;
        }

        public static class RawDataBean {
            /**
             * contract : [{"parameter":{"value":{"amount":10,"owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","to_address":"41350bded243be72edee69520a6e89ab63935c9946"},"type_url":"type.googleapis.com/protocol.TransferContract"},"type":"TransferContract"}]
             * ref_block_bytes : 3f8e
             * ref_block_hash : 36b97360eceb5aa8
             * expiration : 1622787666000
             * timestamp : 1622787606766
             */

            private String ref_block_bytes;
            private String ref_block_hash;
            private long expiration;
            private long timestamp;
            private List<ContractBean> contract;

            public String getRef_block_bytes() {
                return ref_block_bytes;
            }

            public void setRef_block_bytes(String ref_block_bytes) {
                this.ref_block_bytes = ref_block_bytes;
            }

            public String getRef_block_hash() {
                return ref_block_hash;
            }

            public void setRef_block_hash(String ref_block_hash) {
                this.ref_block_hash = ref_block_hash;
            }

            public long getExpiration() {
                return expiration;
            }

            public void setExpiration(long expiration) {
                this.expiration = expiration;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public List<ContractBean> getContract() {
                return contract;
            }

            public void setContract(List<ContractBean> contract) {
                this.contract = contract;
            }

            public static class ContractBean {
                /**
                 * parameter : {"value":{"amount":10,"owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","to_address":"41350bded243be72edee69520a6e89ab63935c9946"},"type_url":"type.googleapis.com/protocol.TransferContract"}
                 * type : TransferContract
                 */

                private ParameterBean parameter;
                private String type;

                public ParameterBean getParameter() {
                    return parameter;
                }

                public void setParameter(ParameterBean parameter) {
                    this.parameter = parameter;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public static class ParameterBean {
                    /**
                     * value : {"amount":10,"owner_address":"41e9a3903902f1f584e63a78e3171631504be8e521","to_address":"41350bded243be72edee69520a6e89ab63935c9946"}
                     * type_url : type.googleapis.com/protocol.TransferContract
                     */

                    private ValueBean value;
                    private String type_url;

                    public ValueBean getValue() {
                        return value;
                    }

                    public void setValue(ValueBean value) {
                        this.value = value;
                    }

                    public String getType_url() {
                        return type_url;
                    }

                    public void setType_url(String type_url) {
                        this.type_url = type_url;
                    }

                    public static class ValueBean {
                        /**
                         * amount : 10
                         * owner_address : 41e9a3903902f1f584e63a78e3171631504be8e521
                         * to_address : 41350bded243be72edee69520a6e89ab63935c9946
                         */

                        private int amount;
                        private String data;
                        private String owner_address;
                        private String to_address;
                        private String contract_address;


                        public int getAmount() {
                            return amount;
                        }

                        public void setAmount(int amount) {
                            this.amount = amount;
                        }

                        public String getOwner_address() {
                            return owner_address;
                        }

                        public void setOwner_address(String owner_address) {
                            this.owner_address = owner_address;
                        }

                        public String getTo_address() {
                            return to_address;
                        }

                        public void setTo_address(String to_address) {
                            this.to_address = to_address;
                        }

                        public String getData() {
                            return data;
                        }

                        public void setData(String data) {
                            this.data = data;
                        }

                        public String getContract_address() {
                            return contract_address;
                        }

                        public void setContract_address(String contract_address) {
                            this.contract_address = contract_address;
                        }
                    }
                }
            }
        }

        public static class RetBean {
            /**
             * contractRet : SUCCESS
             * fee : 100000
             */

            private String contractRet;
            private int fee;

            public String getContractRet() {
                return contractRet;
            }

            public void setContractRet(String contractRet) {
                this.contractRet = contractRet;
            }

            public int getFee() {
                return fee;
            }

            public void setFee(int fee) {
                this.fee = fee;
            }
        }
    }
}
