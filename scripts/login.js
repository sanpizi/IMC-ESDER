$(document).ready(function() {
    Page.extend({
        init: function() {
            var self = this,
                $login = $('#login');

            //设置焦点
            $('#username').focus();

            //绑定 login 按钮事件
            $login.prop('disabled', false).on('click', function() {
                if ($login.prop('disabled')) {
                    return;
                }

                self.login();
            });

            //绑定表单提交事件
            $('.login').on('keyup', function(event) {
                if ($login.prop('disabled')) {
                    return;
                }

                if (event.keyCode === 13) {
                    self.login();
                }
            });
        },

        //登录
        login: function() {
            var $username = $('#username'),
                $password = $('#password'),
                $login = $('#login');

            if (!$username.val()) {
                window.alert('Invaid username.');
                $username.focus();
                return;
            }

            if (!$password.val()) {
                window.alert('Invaid password.')
                $password.focus();
                return;
            }

            if ($login.prop('disabled')) {
                return;
            } else {
                //禁用按钮，防止多次点击，重复提交
                $login.prop('disabled', true);
            }

            //提交验证表单
            $.ajax({
                type: "get",
                url: "/mock/login.json",
                data: {
                    "username": !$username.val(),
                    "password": !$password.val()
                },
                dataType: "json",
                success: function(data) {
                    if (data.result === 0) {
                        document.cookie = "username=" + $username.val();
                        window.location.href = '/status.html';
                    } else {
                        window.alert('The username or password is incorrect.');
                        $username.prop('disabled', false).focus();
                    }
                },
                error: function(error) {
                    $username.prop('disabled', false).focus();
                    //window.alert('Login failed.');
                    console.error('Login failed.');
                }
            });
        }
    });

    window.page = new Page();
});