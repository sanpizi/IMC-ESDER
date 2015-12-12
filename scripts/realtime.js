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
                    //生动展开树菜单到站点
                    $('li.closed[data-area-id=' + data.areaId + ']>span.area').trigger('click');

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

                    //信号量：ID, 单位，有效数字，格式化
                    var arrSingal = [
                        ["958",     "°C",     1,    {}],
                        ["959",     "%",      0,    {}],
                        ["957",     "°C",     1,    {}],
                        ["1015",    "",       0,    {'0':'Normal', '1':'Alarm'}],
                        ["993",     "",       0,    {'0': 'Normal', '1': 'Over Temp', '2': 'Under Temp', '3': 'Sensor Disconnected'}],
                        ["994",     "",       0,    {'0': 'Normal', '1': 'Over Temp', '2': 'Under Humidity', '3': 'Sensor Disconnected'}],
        
                        ["962",     "",       0,    {}],
                        ["989",     "rpm",    1,    {}],
                        ["985",     "%",      0,    {}],
                        ["987",     "W",      1,    {}],
                        ["986",     "kWh",    1,    {}],
                        ["977",     "hours",  1,    {}],
                        ["1014",    "",       0,    {'0':'Normal', '1':'Alarm'}],
                        ["1011",    "",       0,    {'0':'Normal', '1':'Alarm'}],
                        ["992",     "V",      1,    {}],
        
                        ["960",     "",       0,    {}],
                        ["961",     "",       0,    {}],
                        ["980",     "hours",  1,    {}],
                        ["983",     "hours",  1,    {}],

                        ["990",     "",       2,    {}],
                        ["991",     "",       2,    {}],
                    ];

                    for (var j = 0; j < arrSingal.length; j++) {
                        var signal, dataValue, dataTime;

                        signal = signalData[arrSingal[j][0]];
                        dataValue = signal !== undefined ? format(signal.dataVal, arrSingal[j][2]) : '-';
                        dataValue = arrSingal[j][3][dataValue] || dataValue;
                        dataValue = dataValue + ' ' + arrSingal[j][1];
                        dataTime = signal ? signal.dataTime.substr(0, 19) : '';                        

                        $('#signal_' + arrSingal[j][0]).html(dataValue).siblings('.time').html(dataTime);
                    };

                    //数字精度
                    function format(str, accuracy) {
                        if ($.isNumeric(str) && ~str.indexOf('.')) {
                            str = parseFloat(str).toFixed(accuracy).toString();
                        }

                        return str;
                    }
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