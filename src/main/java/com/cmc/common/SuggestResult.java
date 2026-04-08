package com.cmc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestResult {
    private String keyword;  // 高亮后的显示
    private String rawKeyword; // 原始 keyword
    private long weight;

}