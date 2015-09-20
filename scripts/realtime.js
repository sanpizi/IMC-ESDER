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

                    //指标
                    $('#signal_01').html(data.details['963'] + 'V/' 
                        + data.details['964'] + 'V/' 
                        + data.details['965'] + 'V');

                    $('#signal_02').html(data.details['966'] + 'A/' 
                        + data.details['967'] + 'A/' 
                        + data.details['968'] + 'A');

                    $('#signal_03').html(data.details['970'] + 'A/' 
                        + data.details['971'] + 'A/' 
                        + data.details['972'] + 'A');

                    $('#signal_04').html(data.details['960']);

                    $('#signal_05').html(data.details['961']);

                    $('#signal_06').html(data.details['962']);

                    $('#signal_07').html(data.details['958'] + 'Celsiur');

                    $('#signal_08').html(data.details['959'] + '%');

                    $('#signal_09').html(data.details['957'] + 'Celsiur');

                    $('#signal_10').html(data.details['1004']);

                    $('#signal_11').html(data.details['976'] + ' days ' 
                        + data.details['977'] + ' hours ' 
                        + data.details['978'] + ' minutes');

                    $('#signal_12').html(data.details['979'] + ' days ' 
                        + data.details['980'] + ' hours ' 
                        + data.details['981'] + ' minutes');

                    $('#signal_13').html(data.details['982'] + ' days ' 
                        + data.details['983'] + ' hours ' 
                        + data.details['984'] + ' minutes');

                    $('#signal_14').html(data.details['985'] + '%');

                    $('#signal_15').html(data.details['986'] + 'kWh');

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