package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.ArticleColumn;
import com.cmc.entity.UserColumn;
import com.cmc.mapper.ArticleColumnMapper;
import com.cmc.mapper.UserColumnMapper;
import com.cmc.service.UserColumnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
@Service
public class UserColumnServiceImpl extends ServiceImpl<UserColumnMapper, UserColumn> implements UserColumnService {

    @Autowired
    private UserColumnMapper userColumnMapper;
    @Autowired
    private ArticleColumnMapper articleColumnMapper;

    @Override
    public R getAllUserColumnsByUsername(String username) {
        QueryWrapper<UserColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", username);
        List<UserColumn> list = userColumnMapper.selectList(queryWrapper);
        return R.ok(list);
    }

    @Override
    public R addUserColumn(UserColumn userColumn) {

        userColumn.setStatus("0");

        int i = userColumnMapper.insert(userColumn);
        if (i > 0){
            return R.ok("添加成功");
        }


        return R.error("添加失败");
    }

    @Override
    public R getColumnByArticleId(Long articleId) {

        List<UserColumn> list = new ArrayList<>();
        // 根据文章id 获取 包含所有id 的专栏
        List<ArticleColumn> articleColumns = articleColumnMapper.selectList(new QueryWrapper<ArticleColumn>().eq("article_id", articleId));
        for (ArticleColumn articleColumn : articleColumns) {
            // 查询 所以的column数据
            UserColumn userColumn = userColumnMapper.selectList(new QueryWrapper<UserColumn>().eq("id", articleColumn.getColumnId())).get(0);
            // 获取  该专栏 有多少文章
            userColumn.setTotalArticleCount(articleColumnMapper.selectList(new QueryWrapper<ArticleColumn>().eq("column_id",articleColumn.getColumnId())).size());
            list.add(userColumn);
        }


        return R.ok(list);
    }
}
