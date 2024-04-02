package com.yupi.jieOj.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.yupi.jieOj.model.dto.question.JudgeCase;
import com.yupi.jieOj.model.dto.question.JudgeConfig;
import lombok.Data;

/**
 * 题目表
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private String tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 通过数
     */
    private Integer submitNum;

    /**
     * 提交数
     */
    private Integer acceptedNum;

    /**
     * 判题用例（json数组）
     */
    private String judgeCase;

    /**
     * 判题配置（json对象）
     */
    private String  judgeConfig;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}