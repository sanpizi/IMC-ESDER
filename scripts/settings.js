$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var self = this,
                $oldPassword = $('#oldPassword'),
                $newPassword = $('#newPassword'),
                $confirmPassword = $('#confirmPassword'),
                $validateOldPassword = $('#validateOldPassword'),
                $validateNewPassword = $('#validateNewPassword'),
                $validateConfirmPassword = $('#validateConfirmPassword');

            $oldPassword.focus();

            //表单验证
            $oldPassword.on('blur', function() {
                if (!this.value) {
                    $validateOldPassword.html('Please enter your old password.');
                } else {
                    $validateOldPassword.html('');
                }
            });

            //表单验证
            $newPassword.on('blur', function() {
                if (!this.value) {
                    $validateNewPassword.html('Please enter your new password.');
                } else {
                    $validateNewPassword.html('');
                }
            });

            //表单验证
            $confirmPassword.on('blur', function() {
                if (!this.value) {
                    $validateConfirmPassword.html('Please confirm your new password.');
                } else if (this.value !== $newPassword.val()) {
                    $validateConfirmPassword.html('Please confirm your new password.');
                } else {
                    $validateConfirmPassword.html('');
                }
            });

            //提交表单
            $('#submit').on('click', function() {
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
                        console.error('修改密码失败。');
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
                self = this,
                $oldPassword = $('#oldPassword'),
                $newPassword = $('#newPassword'),
                $confirmPassword = $('#confirmPassword'),
                $validateOldPassword = $('#validateOldPassword'),
                $validateNewPassword = $('#validateNewPassword'),
                $validateConfirmPassword = $('#validateConfirmPassword');

            if (!$oldPassword.val()) {
                $oldPassword.trigger('blur');

                result = false;
            }

            if (!$newPassword.val()) {
                $newPassword.trigger('blur');

                result = false;
            }

            if (!$confirmPassword.val() || $newPassword.val() !== $confirmPassword.val()) {
                $confirmPassword.trigger('blur');

                result = false;
            }

            return result;
        }
    });

    window.page = new Page();
});