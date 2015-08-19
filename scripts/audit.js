$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var $grid = $('.grid'),
                grid;
            
            grid = $grid.grid({
                "ajax": "/mock/alarms.json",
                "header": ["Site Id", "Site Name", "Signal Name", "Sampling Data", "Sampling Time", "Status"]
            });

            //更新总告警数
            this.getTotalWarning();
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
        }
    });

    window.page = new Page();
});