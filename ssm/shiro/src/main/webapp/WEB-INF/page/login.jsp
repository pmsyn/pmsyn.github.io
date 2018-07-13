<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>登录</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="resource/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="resource/css/bootstrap-responsive.min.css">
    <style>
        body {
            background-image: url('resource/img/login_bg.jpg');
            background-repeat: round;
        }

        .login {
            width: 40%;
            height: 300px;
            margin: 150px auto;
            border: 1px solid #fff;
            /* border-bottom: 3px solid #0e419c; */
            text-align: center;
            background: #ffff;
            box-shadow: #46aff9 -1px 1px 5px 5px;
            border-radius: 10px;
        }

        .error_msg {
            color: #ff0000;
        }
    </style>
</head>
<body>
<div class="login">
    <form action="checkLogin" method="post">
        <div style="margin: 100px auto 10px auto;">
            <i class="icon-user"></i>
            <input type="text" name="userName" class="input-large" placeholder="请输入用户名……"><br>
            <i class="icon-pencil"></i>
            <input type="password" name="password" class="input-large" placeholder="输入密码" style="margin-top:10px;">
        </div>

        <div>
            <button class="	btn btn-small btn-primary">登录</button>
            <lable style="margin-left: 10px;">
                <input type="checkbox" name="rememberMe" value ="true" class="checkbox"
                       style="width:15px;margin:0px;">记住我
            </lable>
        </div>
    </form>
    <c:if test="${not empty msg}">
        <div class="error_msg"><p>${msg}</p></div>
    </c:if>
</div>

</body>
</html>