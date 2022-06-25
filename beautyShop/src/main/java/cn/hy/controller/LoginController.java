package cn.hy.controller;

import cn.hy.base.BaseController;
import cn.hy.pojo.*;
import cn.hy.service.ItemCategoryService;
import cn.hy.service.ItemService;
import cn.hy.service.ManageService;
import cn.hy.service.UserService;
import cn.hy.utils.Consts;
import cn.hy.utils.UUIDUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.Comparable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 登录
 */
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    ManageService manageService;
    @Autowired
    private ItemCategoryService itemCategoryService;
    @Autowired
    private ItemService itemService;

    /**
     * 管理员登录前
     * @return
     */
    @RequestMapping("login")
    public String login() {
        return "/login/mLogin";
    }

    /**
     * 大数据推荐
     */
    @RequestMapping("algorithm")
    public String algorithm(Model model, HttpServletRequest request) {
        try {
            Integer userid = (Integer) request.getSession().getAttribute(Consts.USERID);
            List<Comment> users = new ArrayList<>();
            //1,加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");//固定写法，加载驱动
            //2,用户信息和url    useUnicode=true&characterEncoding=utf8&&useSSL=true
            String url = "jdbc:mysql://localhost:3306/beautyShop?&serverTimezone=GMT&useSSL=false&useUnicode=true&characterEncoding=utf-8";
            String username = "root";
            String password = "";
            //3,连接成功，数据库对象,connection代表数据库
            Connection connection = DriverManager.getConnection(url, username, password);
            //4,执行sql的对象，statement执行sql的对象
            Statement statement = connection.createStatement();
            //5,执行sql的对象 去 执行sql，可能存在结果，查看返回结果
            String sql = "select * from comment";
            ResultSet resultSet = statement.executeQuery(sql);//返回的结果集,结果集中封装了我们全部的查询出来的结果
            while (resultSet.next()) {
                Comment comment = new Comment();
                comment.setId((Integer) resultSet.getObject("id"));
                comment.setScore((Integer) resultSet.getObject("score"));
                comment.setUserId((Integer) resultSet.getObject("user_id"));
                comment.setItemId((Integer) resultSet.getObject("item_id"));
                comment.setContent(resultSet.getString("content"));
                comment.setAddTime(resultSet.getTime("addTime"));
                users.add(comment);
            }
            Recommend recommend = new Recommend();
            List<User1> usersList = new ArrayList<>();
            List<Integer> count=new ArrayList();
            for (Comment comment:users){
                if(!count.contains(comment.userId)){
                    count.add(comment.userId);
                    User1 userCount=new User1(comment.userId.toString());
                    for (Comment com:users){
                        String id=com.itemId.toString();
                        int score=com.score;
                        if(com.userId.equals(comment.userId)){
                            userCount.set(id,score);
                        }
                    }
                    usersList.add(userCount);
                }
            }
            List<ITem> recommend1 = recommend.recommend(userid.toString(), usersList);
//            for (Comment comment : recommend1) {
//                System.out.println(comment);
//            }
            StringBuilder sql5 = new StringBuilder("select * from item where id in (");
            int size = recommend1.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    //sql5.append(recommend1.get(i).getItemId());
                    sql5.append((Integer.valueOf(recommend1.get(i).itemName)));
                    if (i != size - 1) {
                        sql5.append(",");
                    }
                }
                sql5.append(")");
                List<Item> rxsItems = itemService.listBySqlReturnEntity(sql5.toString());
                model.addAttribute("rxs", rxsItems);
            } else {
                //        //热门商品
                String sql4 = "select * from item where isDelete=0 order by gmNum desc limit 0,10";
                List<Item> rxsItems = itemService.listBySqlReturnEntity(sql4);
                model.addAttribute("rxs", rxsItems);
            }
            //6,释放连接
            resultSet.close();
            statement.close();
            connection.close();
        }catch (Exception e){
            //        //热门商品
            String sql4 = "select * from item where isDelete=0 order by gmNum desc limit 0,10";
            List<Item> rxsItems = itemService.listBySqlReturnEntity(sql4);
            model.addAttribute("rxs", rxsItems);
        }
        return "/login/algorithm";
    }

    /**
     * 登录验证
     *
     * @return
     */
    @RequestMapping("toLogin")
    public String toLogin(Manage manage, HttpServletRequest request) {
        Manage byEntity = manageService.getByEntity(manage);
        //判断是否存在
        if (null == byEntity) {
            return "redirect:/login/mQuit";
        }
        request.getSession().setAttribute(Consts.MANAGE, byEntity);
        return "/login/mIndex";
    }

    /**
     * 管理员退出
     */
    @RequestMapping("mQuit")
    public String mtuichu(HttpServletRequest request) {
        request.getSession().setAttribute(Consts.MANAGE, null);
        return "/login/mLogin";
    }

    /**
     * 首页
     */
    @RequestMapping("/uIndex")
    public String uIndex(Model model, Item item, HttpServletRequest request) {
        String sql = "select * from item_category where isDelete=0 and pid is null order by name";
        List<ItemCategory> fatherList = itemCategoryService.listBySqlReturnEntity(sql);
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(fatherList)) {
            for (ItemCategory ic : fatherList) {
                CategoryDto categoryDto = new CategoryDto();
                categoryDto.setFather(ic);
                //查询二级分类,子水果的pid是父水果的id
                String sql2 = "select * from item_category where isDelete=0 and pid=" + ic.getId();
                List<ItemCategory> childrensList = itemCategoryService.listBySqlReturnEntity(sql2);
                categoryDto.setChildrens(childrensList);
                categoryDtoList.add(categoryDto);
                model.addAttribute("lbs", categoryDtoList);
            }
        }
        //促销商品
        String sql3 = "select * from item where isDelete=0 and zk is not null order by zk desc limit 0,10";
        List<Item> zkxItems = itemService.listBySqlReturnEntity(sql3);
        model.addAttribute("zks", zkxItems);

        //热门商品
        String sql4 = "select * from item where isDelete=0 order by gmNum desc limit 0,10";
        List<Item> rxsItems = itemService.listBySqlReturnEntity(sql4);
        model.addAttribute("rxs", rxsItems);
        return "login/uIndex";
    }

    /**
     * 跳转到普通用户注册
     */
    @RequestMapping("/res")
    public String res() {
        return "login/res";
    }

    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping("/uLogin")
    public String uLogin() {
        return "login/uLogin";
    }

    /**
     * 处理普通用户注册
     */
    @RequestMapping("/toRes")
    public String toRes(User user) {
        userService.insert(user);
        //跳转到登录页面
        return "redirect:/login/uLogin";
    }

    /**
     * 处理普通用户登录
     *
     * @param user
     * @return
     */
    @RequestMapping("/utoLogin")
    public String utoLogin(User user, HttpServletRequest request, Model model) {
        User byEntity = userService.getByEntity(user);
        //判断是否存在
        if (null == byEntity) {
            model.addAttribute("message", "密码或账号错误");
            return "login/uLogin";
        }
        //放入域中
        request.getSession().setAttribute("role", 2);
        // Consts定义一个常量 ，可以避免使用"username"
        request.getSession().setAttribute(Consts.USERNAME, byEntity.getUserName());
        request.getSession().setAttribute(Consts.USERID, byEntity.getId());
        request.getSession().setAttribute(Consts.USERID, byEntity.getId());
        return "redirect:/login/uIndex";
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @RequestMapping("/uQuit")
    public String uQuit(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login/uIndex";
    }

    /**
     * 跳转到修改密码
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/pass")
    public String pass(HttpServletRequest request, Model model) {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) {
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User u = userService.load(userId);
        model.addAttribute("obj", u);
        return "login/pass";
    }

    /**
     * 修改普通用户密码
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/upass")
    @ResponseBody
    public String upass(String password, HttpServletRequest request) {
        JSONObject js = new JSONObject();
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) {
            js.put(Consts.RES, 0);
            return js.toString();
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User load = userService.load(userId);
        load.setPassWord(password);
        userService.updateById(load);
        js.put(Consts.RES, 1);
        return js.toString();
    }
}



class ITem implements Comparable<ITem> {
    public String itemName;
    public int score;
    public ITem(String itemName, int score) {
        this.itemName = itemName;
        this.score = score;
    }

    @Override
    public String toString() {
        return "ITem{" +
                "itemName='" + itemName + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(ITem o) {
        return score > o.score ? -1 : 1;
    }
}

class User1 {
    public String username;
    public List<ITem> itemList = new ArrayList<>();

    public User1() {}

    public User1(String username) {
        this.username = username;
    }

    public User1 set(String itemName, int score) {
        this.itemList.add(new ITem(itemName, score));
        return this;
    }

//    public Movie find(String movieName) {
//        for (Movie movie : movieList) {
//            if (movie.movieName.equals(username)) {
//                return movie;
//            }
//        }
//        return null;
//    }

    @Override
    public String toString() {
        return "User1{" +
                "username='" + username + '\'' +
                '}';
    }
}

class Recommend {
    /**
     * 在给定username的情况下，计算其他用户和它的距离并排序
     * @param username
     * @return
     */
    private Map<Double, String> computeNearestNeighbor(String username, List<User1> users) {
        Map<Double, String> distances = new TreeMap<>();

        User1 u1 = new User1(username);
        for (User1 user:users) {
            if (username.equals(user.username)) {
                u1 = user;
            }
        }

        for (int i = 0; i < users.size(); i++) {
            User1 u2 = users.get(i);

            if (!u2.username.equals(username)) {
                double distance = pearson_dis(u2.itemList, u1.itemList);

                distances.put(distance, u2.username);
            }

        }
        System.out.println("该用户与其他用户的皮尔森相关系数 -> " + distances);
        return distances;
    }


    /**
     * 计算2个打分序列间的pearson距离
     * 选择公式四进行计算
     * @param rating1
     * @param rating2
     * @return
     */
    private double pearson_dis(List<ITem> rating1, List<ITem> rating2) {
        int n=rating1.size();
        List<Integer> rating1ScoreCollect = rating1.stream().map(A -> A.score).collect(Collectors.toList());
        List<Integer> rating2ScoreCollect = rating2.stream().map(A -> A.score).collect(Collectors.toList());

        double Ex= rating1ScoreCollect.stream().mapToDouble(x->x).sum();
        double Ey= rating2ScoreCollect.stream().mapToDouble(y->y).sum();
        double Ex2=rating1ScoreCollect.stream().mapToDouble(x->Math.pow(x,2)).sum();
        double Ey2=rating2ScoreCollect.stream().mapToDouble(y->Math.pow(y,2)).sum();
        double Exy= IntStream.range(0,n).mapToDouble(i->rating1ScoreCollect.get(i)*rating2ScoreCollect.get(i)).sum();
        double numerator=Exy-Ex*Ey/n;
        double denominator=Math.sqrt((Ex2-Math.pow(Ex,2)/n)*(Ey2-Math.pow(Ey,2)/n));
        if (denominator==0) return 0.0;
        return numerator/denominator;
    }


    public List<ITem> recommend(String username, List<User1> users) {
        //找到最近邻
        Map<Double, String> distances = computeNearestNeighbor(username, users);
        //遍历map集合
        //获职所有的键
        Set<Double> set = distances.keySet();
        double max=0;
        double last=0;
        for (Double aDouble : set) {
            if(Math.abs(aDouble)>max){
                last=aDouble;
                max=Math.abs(aDouble);
            }
        }
        String nearest=distances.get(last);
        //String nearest = distances.values().iterator().next();
        System.out.println("最近邻 -> " + nearest);
        //找到最近邻看过，但是我们没看过的商品，计算推荐
        User1 neighborRatings = new User1();
        for (User1 user:users) {
            if (nearest.equals(user.username)) {
                neighborRatings = user;
            }
        }
        System.out.println("最近邻看过的商品 -> " + neighborRatings.itemList);

        User1 userRatings = new User1();
        for (User1 user:users) {
            if (username.equals(user.username)) {
                userRatings = user;
            }
        }
        System.out.println("用户看过的商品 -> " + userRatings.itemList);

        //根据自己和邻居的电影计算推荐的电影
        List<ITem> recommendationMovies = new ArrayList<>();
        boolean flag=false;
        for (ITem item : neighborRatings.itemList) {
            for(ITem uitem:userRatings.itemList){
                if(item.itemName.equals(uitem.itemName)){
                    flag=true;
                    break;
                }
            }
            if(flag==false){
                recommendationMovies.add(item);
            }
//            if (userRatings.find(movie.movieName) == null) {
//                recommendationMovies.add(movie);
//            }
        }
        Collections.sort(recommendationMovies);
        return recommendationMovies;
    }
}





