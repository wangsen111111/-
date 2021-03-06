package cn.hy.controller;

import cn.hy.pojo.Comment;
import cn.hy.service.CommentService;
import cn.hy.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping("/exAdd")
    public String exAdd(HttpServletRequest request, Comment comment){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        System.out.println(comment.getScore());
        int score=comment.getScore();
        if(attribute==null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        comment.setScore(score);
        comment.setUserId(userId);
        comment.setAddTime(new Date());
        commentService.insert(comment);
        return  "redirect:/itemOrder/my";
    }
}
