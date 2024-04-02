package com.yupi.jieOj.model.dto.question;

import lombok.Data;

@Data
public class JudgeConfig {

    /**
     * 时间限(ms)
     */
    private long timeLimit;
    /**
     * 内存限制(KB)
     */
    private long memoryLimit;

    /**
     * 堆栈限制(KB)
     */
    private long stackLimit;
}
