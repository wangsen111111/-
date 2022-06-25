<%@page language="java" contentType="text/html; character=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>首页</title>
    <link type="text/css" rel="stylesheet" href="${ctx}/resource/user/css/style.css">
    <script src="${ctx}/resource/user/js/jquery-1.8.3.min.js"></script>
    <script src="${ctx}/resource/user/js/jquery.luara.0.0.1.min.js"></script>
</head>
<body>
<%@include file="/common/utop.jsp"%>
<!--导航条-->
<div class="width100" style="height: 45px;background: #45dd4c;margin-top: 40px;position: relative;z-index: 100;">
    <!--中间的部分-->
    <div class="width1200 center_yh relative_yh" style="height: 45px;">
        <!--普通导航-->
        <div class="left_yh font16" id="pageNav">
            <a href="${ctx}/login/uIndex">首页</a>
            <a href="${ctx}/news/list">公告</a>
            <a href="${ctx}/message/add">留言</a>
            <a href="${ctx}/login/algorithm">大数据推荐</a>
        </div>
    </div>
</div>

<div class="width1200 center_yh hidden_yh font14" style="height: 40px;line-height: 40px;">
</div>
<div class="width100 hidden_yh" style="background: #f0f0f0;padding-top:34px;padding-bottom: 34px;">
    <div class="width1200 hidden_yh center_yh">
        <div id="vipRight" style="height: 60px;line-height: 60px;text-indent: 50px; background: #f5f8fa;width: 1200px;border:1px solid #ddd;">
            以下商品是根据您对过往购买商品的评分为您专属推荐的
        </div>
        <!--大数据推荐-->
        <div class="width1200 center_yh hidden_yh">
            <div class="width100" style="height: 45px;line-height: 45px;border-bottom: 2px solid #dd4545; margin-top: 20px;">
                <font class="left_yh font20">大数据推荐</font>
            </div>
            <div class="width100 hidden_yh" style="height: 480px;">
                <div class="normalPic">
                    <c:forEach items="${rxs}" var="data" varStatus="l">
                        <a href="${ctx}/item/view?id=${data.id}">
                            <h3 class="yihang c_33 font14 font100" style="padding-left: 10px;padding-right: 10px;">${data.name}</h3>
                            <p class="red font14" style="padding-left: 10px;">${data.price}</p>
                            <img src="${data.url1}" width="105" height="118" alt="" style="margin:0 auto">
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>


    </div>
</div>

<%@include file="/common/ufooter.jsp"%>
</body>
</html>



















