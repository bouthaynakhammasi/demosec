package com.aziz.demosec.dto;


import lombok.Data;

@Data
public class VerifyCodeRequest {
    private String email;
    private String code;
}
