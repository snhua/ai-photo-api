package com.aiphone.common.exception;

public class CosResponse {
    private int code;
    private String message;
    private CosData data;

    public CosResponse() {}

    public CosResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CosResponse(int code, String message, CosData data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CosData getData() {
        return data;
    }

    public void setData(CosData data) {
        this.data = data;
    }

    public static class CosData {
        private String cosHost;
        private String cosKey;
        private String policy;
        private String qSignAlgorithm;
        private String qAk;
        private String qKeyTime;
        private String qSignature;

        public CosData() {}

        // Getters and Setters
        public String getCosHost() {
            return cosHost;
        }

        public void setCosHost(String cosHost) {
            this.cosHost = cosHost;
        }

        public String getCosKey() {
            return cosKey;
        }

        public void setCosKey(String cosKey) {
            this.cosKey = cosKey;
        }

        public String getPolicy() {
            return policy;
        }

        public void setPolicy(String policy) {
            this.policy = policy;
        }

        public String getQSignAlgorithm() {
            return qSignAlgorithm;
        }

        public void setQSignAlgorithm(String qSignAlgorithm) {
            this.qSignAlgorithm = qSignAlgorithm;
        }

        public String getQAk() {
            return qAk;
        }

        public void setQAk(String qAk) {
            this.qAk = qAk;
        }

        public String getQKeyTime() {
            return qKeyTime;
        }

        public void setQKeyTime(String qKeyTime) {
            this.qKeyTime = qKeyTime;
        }

        public String getQSignature() {
            return qSignature;
        }

        public void setQSignature(String qSignature) {
            this.qSignature = qSignature;
        }
    }
} 