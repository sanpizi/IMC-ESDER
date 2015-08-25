$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var self = this;

            //拉取原始数据
            self.getParams();

            //提交表单
            $('.form').on( 'click', '[type="button"]', function(e) {
                var $row = $(this).closest('tr');

                if (e.target.value === 'Save') {
                    self.setParams($row);
                } else if (e.target.value === 'Reset') {
                    var $input = $row.find('.setting-input');
                    $input.val($input.attr('data-default-value'));
                }

            })
        },

        //验证表单
        validateForm: function() {
            var result = true,
                self = this;
               
            return result;
        },

        //拉取原始数据
        getParams: function() {            
            $.ajax({
                type: "GET",
                url: "/settings",
                dataType: "json",
                data: {
                },
                success: function(data) {
                    $('#p_1').val(data['1']);
                },
                error: function(err) {
                    //window.alert('Failed to get the settings.');
                    console.error('获取配置信息失败。');
                }
            });
        },

        //保存设置
        setParams: function($row) {
            var bt = $row.find('[value="Save"]')[0],
                input = $row.find('.setting-input')[0];

            $.ajax({
                type: "POST",
                url: "/settings",
                dataType: "json",
                data: {
                    id: input.id.replace('p_', ''),
                    value: input.value
                },
                beforeSend: function() {
                    //禁用按钮
                    bt.disabled = true;
                },
                success: function(data) {
                    if (data.result === 0) {
                        //
                    } else {
                        alert(data.errMsg);
                    }
                },
                error: function(err) {
                    //window.alert('Failed to save the settings.');
                    console.error('保存配置失败。');
                },
                complete: function() {
                    //启用按钮
                    bt.disabled = false;
                }
            });
        }
    });

    window.page = new Page();
});