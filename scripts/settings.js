$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var self = this;

            //提交表单
            $('#save').on('click', function() {
                var bt = this;

                if (!self.validateForm()) {
                    return;
                }

                $.ajax({
                    type: "POST",
                    url: "/modifyAccount",
                    dataType: "json",
                    data: {
                        username: self.getCookie('username'),
                        oldPassword: $('#oldPassword').val(),
                        newPassword: $('#newPassword').val()
                    },
                    beforeSend: function() {
                        //禁用按钮
                        bt.disabled = true;
                    },
                    success: function(data) {
                        if (data.result === 0) {
                            alert('Change password successful.')
                        } else {
                            alert(data.errMsg);
                        }
                    },
                    error: function(err) {
                        //window.alert('Failed to change the password.');
                        console.error('修改配置失败。');
                    },
                    complete: function() {
                        //启用按钮
                        bt.disabled = false;
                    }
                });
            })
        },

        //验证表单
        validateForm: function() {
            var result = true,
                self = this;
               
            return result;
        }
    });

    window.page = new Page();
});