package com.yupi.jieOj.service.impl;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.jieOj.common.ErrorCode;
import com.yupi.jieOj.constant.CommonConstant;
import com.yupi.jieOj.exception.BusinessException;
import com.yupi.jieOj.model.dto.question.QuestionQueryRequest;
import com.yupi.jieOj.model.dto.quiestionsubmit.QuestionSubmitAddRequest;
import com.yupi.jieOj.model.dto.quiestionsubmit.QuestionSubmitQueryRequest;
import com.yupi.jieOj.model.entity.Question;
import com.yupi.jieOj.model.entity.QuestionSubmit;
import com.yupi.jieOj.model.entity.User;
import com.yupi.jieOj.model.enums.QuestionSubmitLanguageEnum;
import com.yupi.jieOj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.jieOj.model.vo.QuestionSubmitVO;
import com.yupi.jieOj.model.vo.QuestionVO;
import com.yupi.jieOj.model.vo.UserVO;
import com.yupi.jieOj.service.QuestionService;
import com.yupi.jieOj.service.QuestionSubmitService;
import com.yupi.jieOj.mapper.QuestionSubmitMapper;
import com.yupi.jieOj.service.UserService;
import com.yupi.jieOj.utils.SqlUtils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 14316
 * @description 针对表【question_submit(帖子提交题目)】的数据库操作Service实现
 * @createDate 2024-03-21 11:24:46
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;
    /**
     * 提交题目
     *
     * @param
     * @param loginUser
     * @return 提交记录id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        // 锁必须要包裹住事务方法 锁加事务，防止用户重复提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        String code = questionSubmitAddRequest.getCode();
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(enumByValue == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编程语言中错误");
        }
        //设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.Witting.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目提交失败");
        }
        return questionSubmit.getId();
    }
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取单个信息的包装类（用户信息）
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit,User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin((HttpServletRequest) loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




