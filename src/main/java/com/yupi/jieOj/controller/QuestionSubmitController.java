package com.yupi.jieOj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.jieOj.common.BaseResponse;
import com.yupi.jieOj.common.ErrorCode;
import com.yupi.jieOj.common.ResultUtils;
import com.yupi.jieOj.exception.BusinessException;
import com.yupi.jieOj.exception.ThrowUtils;
import com.yupi.jieOj.model.dto.question.QuestionQueryRequest;
import com.yupi.jieOj.model.dto.quiestionsubmit.QuestionSubmitAddRequest;
import com.yupi.jieOj.model.dto.quiestionsubmit.QuestionSubmitQueryRequest;
import com.yupi.jieOj.model.entity.Question;
import com.yupi.jieOj.model.entity.QuestionSubmit;
import com.yupi.jieOj.model.entity.User;
import com.yupi.jieOj.model.vo.QuestionSubmitVO;
import com.yupi.jieOj.model.vo.QuestionVO;
import com.yupi.jieOj.service.QuestionSubmitService;
import com.yupi.jieOj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param
     * @param request
     * @return resultNum 提交题目id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表 （除了管理员外，普通用户只能看到非答案，提交代码等公开信息）
     *
     * @param
     * @param questionSubmitQueryRequest
     * @return resultNum 提交题目id
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listquestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                           HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始题目的提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        //返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }

}
