package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.ArticleComment;
import com.cmc.entity.Users;
import com.cmc.mapper.ArticleCommentMapper;
import com.cmc.mapper.UsersMapper;
import com.cmc.service.ArticleCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.vo.ArticleCommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-22
 */
@Service
@Transactional
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements ArticleCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public R getArticleCommentByArticleId(Long articleId) {
        List<ArticleCommentVO> list = new ArrayList<>();

        List<ArticleComment> commentList = articleCommentMapper.selectList(new QueryWrapper<ArticleComment>()
                .eq("article_id", articleId)
                .eq("is_deleted", "0")
                .eq("status", "0")
                .orderByDesc("create_time"));

        if (commentList.isEmpty()) {
            return R.ok(list);
        }

        // 获取所有相关用户 id（包括评论人 + 被回复人）
        List<Long> userIds = commentList.stream()
                .flatMap(c -> Stream.of(c.getUserId(), c.getReplyUserId()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 查询用户信息
        List<Users> userList = usersMapper.selectList(new QueryWrapper<Users>().in("id", userIds));
        Map<Long, Users> userMap = userList.stream()
                .collect(Collectors.toMap(Users::getId, u -> u));

        // 获取父评论
        List<ArticleComment> parentCommentList = commentList.stream()
                .filter(comment -> comment.getParentId() == null)
                .collect(Collectors.toList());

        for (ArticleComment parent : parentCommentList) {
            ArticleCommentVO parentVO = new ArticleCommentVO();
            BeanUtils.copyProperties(parent, parentVO);
            Users user = userMap.get(parent.getUserId());
            parentVO.setUsername(user != null ? user.getNickname() : "未知用户");
            parentVO.setUserAvatar(user != null ? user.getAvatar() : "");

            // 获取子评论
            List<ArticleComment> children = commentList.stream()
                    .filter(comment -> parent.getId().equals(comment.getParentId()))
                    .collect(Collectors.toList());

            if (!children.isEmpty()) {
                List<ArticleCommentVO> childVOList = new ArrayList<>();
                for (ArticleComment child : children) {
                    ArticleCommentVO vo = new ArticleCommentVO();
                    BeanUtils.copyProperties(child, vo);

                    Users commentUser = userMap.get(child.getUserId());
                    vo.setUsername(commentUser != null ? commentUser.getNickname() : "未知用户");
                    vo.setUserAvatar(commentUser != null ? commentUser.getAvatar() : "");

                    if (child.getReplyUserId() != null) {
                        Users replyUser = userMap.get(child.getReplyUserId());
                        vo.setReplyUsername(replyUser != null ? replyUser.getNickname() : null);
                    }

                    childVOList.add(vo);
                }
                parentVO.setChildrenList(childVOList);
            }

            list.add(parentVO);
        }

        return R.ok(list);
    }

    @Override
    public R addArticleComment(ArticleComment articleComment) {
        articleComment.setStatus("0");
        articleComment.setIsDeleted("0");
        articleComment.setLikeCount(0);
        int i = articleCommentMapper.insert(articleComment);
        if (i > 0) {
            return R.ok("success",articleComment);
        }

        return R.error("fail");
    }
}
