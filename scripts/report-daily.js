$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var $grid = $('.grid'),
                grid;
            
            grid = $grid.grid({
                ajax: "/hisalarms",
                params: this.params,
                fnComplete: function(data) {
                    var $export = $('#export'),
                        max = $export.attr('data-max-records'),
                        total = data.totalRecords;

                    if (total > max) {
                        $export.prop('disabled', true)
                            .attr('title', 'Maximum of ' + max + ' records');
                    } else{
                        $export.prop('disabled', false)
                            .attr('title', '');
                    }

                },
                columns: [
                    {
                        header: "Report",
                        content: function(data) {
                            return '<a href="/statsFile?reportId='+ data.areaId + '">' + 'HisDataDaily-' + data.startTime.toString().substr(0, 7) + '.xls' + '</a>';
                        }
                    },
                    {
                        header: "Created Time",
                        content: function(data) {
                            return data.startTime.toString().substr(0, 16);
                        }
                    }
                ]
            });

            $grid.data('grid', grid);

            //拉取区域数据
            this.getAreas();

            //过滤数据
            $('#filter').on('click', function() {
                grid.option.params.areaId = $('#areaId').val();
                grid.option.params.siteId = $('#siteId').val();
                grid.option.params.signalId = $('#signalId').val();
                grid.option.params.startTime = $('#startTime').val();
                grid.option.params.endTime = $('#endTime').val();
                grid.option.params.interval = $('#interval').val();

                grid.init();
            });

            //导出数据
            $('#export').on('click', function() {
                grid.exportData('/exportHisAlarms');
            });
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
        }
    });

    window.page = new Page();
});