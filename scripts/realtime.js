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
                    //总体信息
                    $('#site-status').addClass('site-status-' + data.status.toLowerCase())
                        .attr('title', data.status);
                    $('#site-area').html(data.areaName);
                    $('#site-name').html(data.name).attr('title', 'Site ID: ' + data.id);

                    //告警
                    $('#fatalAlarmNum').text(data.alarmStats.fatal);
                    $('#urgentAlarmNum').text(data.alarmStats.urgent);
                    $('#importantAlarmNum').text(data.alarmStats.important);
                    $('#generalAlarmNum').text(data.alarmStats.general);

                    //处理数据
                    var signalData = {};
                    for (var i = 0; i < data.recordList.length; i++) {
                        signalData[data.recordList[i].signalId] = data.recordList[i];
                    };

                    //信号量
                    var arrSingal = [
                        ["958", "°C"],
                        ["959", "%"],
                        ["957", "°C"],
                        ["1015", ""],
                        ["1006", ""],
                        ["1008", ""],

                        ["962", ""],
                        ["989", "rpm"],
                        ["985", "%"],
                        ["987", "W"],
                        ["986", "kWh"],
                        ["977", ""],
                        ["1014", ""],
                        ["1011", ""],
                        ["992", "V"],

                        ["960", ""],
                        ["961", ""],
                        ["980", "hours"],
                        ["983", "hours"],

                        ["990", ""],
                        ["991", ""],
                    ];
                    for (var j = 0; j < arrSingal.length; j++) {
                        var signal = signalData[arrSingal[j][0]];
                        $('#signal_' + arrSingal[j][0]).html(signal.dataVal + arrSingal[j][1]).siblings('.time').html(signal.dataTime.slice(5,16));
                    };
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取站点指标明细失败。');
                },
                complete: function(xhr, textStatus) {
                    if (window.config['Automatic_Refresh_Interval']) {                        
                        window.setTimeout(function() {
                            self.getSiteDetails();
                        }, window.config['Automatic_Refresh_Interval']);
                    }
                }
            });
        }
    });

    window.page = new Page();
});