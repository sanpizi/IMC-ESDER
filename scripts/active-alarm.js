$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var $grid = $('.grid'),
                grid;

            //生成表格
            grid = $grid.grid({
                ajax: "/alarms",
                pageSize: 20,
                params: this.params,
                fnComplete: function(data) {
                    //启用查询按钮
                    $('#filter').prop('disabled', false);
                },
                columns: [
                    {
                        header: "Zone",
                        content: function(data) {
                            return data.areaName;
                        }
                    },
                    {
                        header: "Site",
                        content: function(data) {
                            return data.siteName;
                        }
                    },
                    {
                        header: "Signal",
                        content: function(data) {
                            return data.signalName;
                        }
                    },
                    {
                        header: "Severity",
                        content: function(data) {
                            return data.severity;
                        }
                    },
                    {
                        header: "Start Time",
                        content: function(data) {
                            return data.startTime.toString().substr(0, 19);
                        }
                    }
                ]
            });

            $grid.data('grid', grid);

            //拉取区域数据
            this.getAreas();

            //根据 URL 参数设置默认选项
            this.setDefaultSelect();

            //过滤数据
            $('#filter').on('click', function() {
                if (this.disabled) {
                    return;
                }

                //禁用，防止连续查询，必须等待上次请求完成后再启用
                this.disabled = true;

                grid.option.params.areaId = $('#areaId').val();
                grid.option.params.siteId = $('#siteId').val();
                grid.option.params.signalId = $('#signalId').val();
                grid.option.params.status = $('#status').val();

                grid.init();
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
                    });

                    // 如果 URL 参数有区域条件，则自动选中，并触发 change 事件
                    // 2015-12-28 需求，默认显示所有站点
                    // if (self.params.areaId) {
                        $area.val(self.params.areaId).trigger('change');
                    // }
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

            // areaId 为空时，比如选中 all 选项时
            // 2015-12-28 需求，默认显示所有站点
            // if (!areaId) {
            //     $site.html('<option value="">-- All --</option>').prop('disabled', true);
            //     return;
            // }

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

                    $site.html(html).prop('disabled', false);

                    //如果 URL 参数有区域条件，则自动选中
                    if (self.params.siteId) {
                        $('#siteId').val(self.params.siteId);
                    }
                },
                error: function(err) {
                    //window.alert('Failed to get the sites data.');
                    console.error('获取站点数据失败。');
                }
            });
        },

        //根据 URL 参数设置默认选项
        setDefaultSelect: function() {
            if (this.params.siteName) {
                $('#siteId').val(this.params.siteId);
            }
            if (this.params.status) {
                $('#status').val(this.params.status);
            }
        }
    });

    window.page = new Page();
});