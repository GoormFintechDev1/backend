package com.example.backend.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosRequestDTO {
    private Long posId;
    private String brNum;
}