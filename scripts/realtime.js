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
                columns: [
                    {
                        header: "Site Id",
                        content: function(data) {
                            return data.siteId;
                        }
                    },
                    {
                        header: "Site Name",
                        content: function(data) {
                            return data.siteName;
                        }
                    },
                    {
                        header: "Signal Name",
                        content: function(data) {
                            return data.signalName;
                        }
                    },
                    {
                        header: "Sampling Data",
                        content: function(data) {
                            return data.samplingData;
                        }
                    },
                    {
                        header: "Sampling Time",
                        content: function(data) {
                            return data.samplingTime;
                        }
                    },
                    {
                        header: "Status",
                        content: function(data) {
                            var html = data.warning.toLowerCase() === 'yes' ? '<span style="color:#f00">Warning</span>' : 'Normal'
                            return html;
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
                grid.option.params.areaId = $('#areaId').val();
                grid.option.params.siteId = $('#siteId').val();
                grid.option.params.status = $('#status').val();

                grid.init();
            });

            //更新总告警数
            //this.getTotalWarning();
        },

        //更新总告警数
        getTotalWarning: function() {
            $.ajax({
                type: "GET",
                url: "/globalStats",
                dataType: "json",
                success: function(data) {
                    $('#totalWarningSite').text(data.sites.alarm);
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取整体统计数据失败。');
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

                    $site.html(html).prop('disabled', false);
                },
                error: function(err) {
                    //window.alert('Failed to get the sites data.');
                    console.error('获取站点数据失败。');
                }
            });
        },

        //根据 URL 参数设置默认选项
        setDefaultSelect: function() {
            if (this.params.status.length) {
                $('#status').val(this.params.status);
            }
        }
    });

    window.page = new Page();
});