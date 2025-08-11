package com.aiphone.dto;

import lombok.Data;

@Data
public class Resource {
    String associated_data;
    String nonce;
    String ciphertext;
}
