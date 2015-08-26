$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var self = this;

            //拉取区域数据
            this.getAreas();

            //更新影响站点数
            this.updateAffectSites();

            //拉取原始数据
            //self.getParams();

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
        },

        //拉取区域数据
        getAreas: function() {
            var self = this,
                $area = $('#areaId');

            $.ajax({
                type: "GET",
                url: "/areas",
                dataType: "json",
                success: function(data) {
                    var areaList = data.areaList,
                        html = '<option value="">-- All --</option>';

                    for (var i = 0; i < areaList.length; i++) {
                        html += '<option value="' + areaList[i].id + '">' + areaList[i].name + '</option>'
                    }

                    $area.html(html)
                        .prop('disabled', false)
                        .on('change', function() {
                            self.getSites($area.val());
                            self.updateAffectSites();
                    });
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取区域数据失败。');
                }
            });
        },

        //拉取站点数据
        getSites: function(areaId) {
            var self = this,
                $site = $('#siteId');

            $.ajax({
                type: "GET",
                url: "/sites",
                dataType: "json",
                data: {
                    areaId: areaId
                },
                success: function(data) {
                    var siteList = data.siteList,
                        html = '<option value="">-- All --</option>';

                    for (var i = 0; i < siteList.length; i++) {
                        html += '<option value="' + siteList[i].id + '">' + siteList[i].name + '</option>'
                    }

                    $site.html(html).prop('disabled', false).on('change', function() {
                        var siteId = $('#siteId').val();
                        if (siteId.length) {
                            $('#affectSites').html(1);
                        } else {
                            self.updateAffectSites();
                        }                        
                    });
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取站点数据失败。');
                }
            });
        },

        //更新设置站点数
        updateAffectSites: function() {
            var self = this,
                $area = $('#areaId');
                $site = $('#siteId');

            $.ajax({
                type: "GET",
                url: "/sites",
                dataType: "json",
                data: {
                    areaId: $area.val()
                },
                success: function(data) {
                    $('#affectSites').html(data.totalRecords);
                },
                error: function(err) {
                    //window.alert('Failed to get the sites data.');
                    console.error('获取区域数据失败。');
                }
            });
        }
    });

    window.page = new Page();
});