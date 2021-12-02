package com.hodur;


import lombok.*;

import java.io.Serializable;

/**
 * @author Hodur
 * @className Hello.java
 * @description
 * @date 2021年04月15日 21:39
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
