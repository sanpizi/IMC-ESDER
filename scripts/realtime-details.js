$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            //获取站点指标明细
            this.getSiteDetails();
        },

        //更新总告警数
        getSiteDetails: function() {
            var self = this;

            $.ajax({
                type: "GET",
                url: "/site/" + self.params.siteId,
                dataType: "json",
                success: function(data) {
                    $('#site-status').addClass('site-status-' + data.status.toLowerCase());
                    $('#site-area').html(data.areaName);
                    $('#site-name').html(data.name).attr('title', 'Site ID: ' + data.id);
                    //告警
                    $('#fatalAlarmNum').text(data.alarmStats.fatal);
                    $('#urgentAlarmNum').text(data.alarmStats.urgent);
                    $('#importantAlarmNum').text(data.alarmStats.important);
                    $('#generalAlarmNum').text(data.alarmStats.general);
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取站点指标明细失败。');
                }
            });
        }
    });

    window.page = new Page();
});