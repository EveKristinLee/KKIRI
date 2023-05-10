package com.lets.kkiri.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RtkVerifyPostRes {
    @JsonProperty("isExpired")
    Boolean isExpired;
}
