package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.constans.article.article.ArticleStatusConstant;
import com.cmc.entity.*;
import com.cmc.mapper.*;
import com.cmc.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.utils.JwtUtil;
import com.cmc.utils.PasswordUtil;
import com.cmc.utils.RedisUtil;
import com.cmc.vo.AuthorDataVO;
import com.cmc.vo.LoginVO;
import com.cmc.vo.UsersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-09-10
 */
@Service
@Transactional
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Value("${token.prefix}")
    private String tokenPrefix;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserFollowRecordMapper userFollowRecordMapper;
    @Autowired
    private UserCollectionRecordMapper userCollectionRecordMapper;
    @Autowired
    private UserCollectionFolderMapper userCollectionFolderMapper;


    /**
     *
     * @param user
     * @return
     */
    @Override
    public R loginByUsername(Users user) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("username", user.getUsername());
        Users userInfo = usersMapper.selectOne(usersQueryWrapper);
        if (userInfo != null && PasswordUtil.checkPassword(user.getPassword(), userInfo.getPassword())) {
            //生成token
            String token = jwtUtil.generateToken(user.getUsername());
            //将token 存储在redis
            if(!ObjectUtils.isEmpty(token)){
                redisUtil.set(tokenPrefix + ":" + user.getUsername(), token, 7 , TimeUnit.DAYS);
                return R.ok("登录成功",new LoginVO(token,userInfo.getId()));
            }
        }else if (userInfo == null){
            return R.error("账号错误");
        }else if (!PasswordUtil.checkPassword(user.getPassword(), userInfo.getPassword())){
            return R.error("密码错误");
        }
        return R.error("登录失败");
    }

    @Override
    public R register(Users users) {
        users.setPassword(PasswordUtil.hashPassword(users.getPassword()));
        int i = 0;
        i = usersMapper.insert(users);
        // 给新用户添加一个默认收藏夹
        UserCollectionFolder userCollectionFolder = new UserCollectionFolder();
        userCollectionFolder.setUserId(users.getId());
        userCollectionFolder.setName("默认收藏夹");
        userCollectionFolder.setDescription(users.getUsername() + "的默认收藏夹");
        userCollectionFolder.setTargetCount(0);
        userCollectionFolder.setIsDefault("1");
        userCollectionFolder.setVisibility("0");
        i = userCollectionFolderMapper.insert(userCollectionFolder);
        if (i > 0) {

            return R.ok("添加成功");
        }
        return R.error("注册失败");
    }

    @Override
    public R getUserInfoById(Long id) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("id", id);
        Users users = usersMapper.selectOne(usersQueryWrapper);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        return R.ok(usersVO);
    }

    @Override
    public R getUserInfoByToken(String token) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", jwtUtil.getUsername(token));
        Users users = usersMapper.selectOne(queryWrapper);
        //判断redis中有没有
        if (!ObjectUtils.isEmpty(token) && token.equals(redisUtil.get(tokenPrefix + ":" + jwtUtil.getUsername(token)))){
            if (!ObjectUtils.isEmpty(users)) {
                UsersVO usersVO = new UsersVO();
                BeanUtils.copyProperties(users, usersVO);
                return R.ok(usersVO);
            }
        }

        return R.error("查询失败");
    }

    @Override
    public R getUserInfoByUsername(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<Users> users = usersMapper.selectList(queryWrapper);
        if(!ObjectUtils.isEmpty(users.get(0))){
            return R.ok(users.get(0));
        }
        return R.error("failed");
    }


    @Override
    public R getAuthorDataForArticlePage(String username) {
        // 获取用户ID
        Users author = usersMapper.selectList(new QueryWrapper<Users>().eq("username", username)).get(0);

        AuthorDataVO vo = new AuthorDataVO();

        // 获取 该作者原创的文章数量
        Integer originArticleNum =(int) articleMapper.selectList(new QueryWrapper<Article>().eq("create_by",username).eq("status", ArticleStatusConstant.NORMAL))
                .stream().filter(t -> t.getType().equals("0")).count();
        vo.setTotalOriginArticleNum(originArticleNum);

        // 设置当前作者的点赞数量
        int totalLikeCount = 0;
        for (Article article : articleMapper.selectList(new QueryWrapper<Article>().eq("create_by", username).eq("status",ArticleStatusConstant.NORMAL))) {
            totalLikeCount = totalLikeCount + article.getLikeCount();
        }
        vo.setTotalLikeNum(totalLikeCount);
        // 设置当前作者的收藏数量
        List<Article> articleList = articleMapper.selectList(new QueryWrapper<Article>().eq("create_by", author.getUsername()));
        int collectCount = 0;
        for (Article article : articleList) {
            collectCount = collectCount + article.getCollectCount();
        }
        vo.setTotalCollectNum(collectCount);


        // 设置当前作者的粉丝数量
        vo.setTotalFansNum(userFollowRecordMapper.selectList(new QueryWrapper<UserFollowRecord>()
                .eq("followee_id",author.getId())
                .eq("is_deleted","0")).size());

        return R.ok(vo);
    }
}
