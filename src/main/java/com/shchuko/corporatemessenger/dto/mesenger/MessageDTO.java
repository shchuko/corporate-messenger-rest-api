package com.shchuko.corporatemessenger.dto.mesenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author shchuko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String author;
    private String content;
    private Date timestamp;
}
