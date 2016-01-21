$(document).ready(function() {
    Page.extend({
        init: function() {
            var self = this,
                $login = $('#login');

            //获取 url 参数
            this.getUrlParams();

            //设置焦点
            setTimeout(function() {
                $('#username').focus();
            }, 1);

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

                if (event.keyCode === 13 && event.target.type === 'password') {
                    self.login();
                }
            });
        },

        //登录
        login: function() {
            var self = this,
                $username = $('#username'),
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
                url: "/login",
                data: {
                    "username": $username.val(),
                    "password": $password.val()
                },
                dataType: "json",
                success: function(data) {
                    if (data.result === 0) {
                        document.cookie = "username=" + $username.val();
                        document.cookie = "role=" + data.tag.toLowerCase();
                        if (self.params.goUrl) {
                            window.location.href = decodeURIComponent(self.params.goUrl);
                        } else {
                            window.location.href = '/overview.html';
                        }                        
                    } else {
                        window.alert('The username or password is incorrect.');
                        $login.prop('disabled', false);
                        $username.focus();
                    }
                },
                error: function(error) {
                    $login.prop('disabled', false)
                    $username.focus();
                    //window.alert('Login failed.');
                    console.error('Login failed.');
                }
            });
        }
    });

    window.page = new Page();
});