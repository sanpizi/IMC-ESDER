$(document).ready(function() {
    Page.extend({
        init: function() {
            var self = this,
                $sitemap = $('.sitemap');

            this._init();

            //渲染统计
            this.renderGlobalStats();

            //渲染矩阵
            this.renderSitesMatrix();

            //渲染图表
            //this.renderStatistics();

            //图片加载完后把 map 滚动条默认滚动中间位置
            $(window).on('load', function() {
                setTimeout(function() {
                    $sitemap.scrollTop(($sitemap.prop('scrollHeight') - $sitemap.height()) / 2).scrollLeft(($sitemap.prop('scrollWidth') - $sitemap.width()) / 2);
                }, 5);
            });

            //绑定站点点击事件
            $sitemap.on('click','span', function() {
                var siteId = this.getAttribute('data-site-id');
                if (!siteId) return;

                window.location.href = '/realtime.html?siteId=' + siteId;
            });

        },

        //渲染统计
        renderGlobalStats: function() {
            var self = this;

            $.ajax({
                type: "GET",
                url: "/globalStats",
                dataType: "json",
                success: function(data) {                    
                    //站点总数
                    $('#totalSitesNum').text(data.sites.total);
                    $('#onlineSitesNum').text(data.sites.normal);
                    $('#alarmSitesNum').text(data.sites.alarm);
                    $('#offlineSitesNum').text(data.sites.offline);

                    //告警
                    $('#fatalAlarmNum').text(data.alarms.fatal);
                    $('#urgentAlarmNum').text(data.alarms.urgent);
                    $('#importantAlarmNum').text(data.alarms.important);
                    $('#generalAlarmNum').text(data.alarms.general);
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取整体统计数据失败。');
                },
                complete: function(xhr, textStatus) {
                    if (window.config['Automatic_Refresh_Interval']) {                        
                        window.setTimeout(function() {
                            self.renderGlobalStats();
                        }, window.config['Automatic_Refresh_Interval']);
                    }
                }
            });
        },

        //渲染矩阵
        renderSitesMatrix: function() {
            var self = this,
                maxSites = 2000; //站点矩阵最大显示数量

            //获取所有站点信息
            $.ajax({
                type: "GET",
                url: "/sites",
                data: {
                    start: 1,
                    amount: maxSites
                },
                dataType: "json",
                success: function(data) {
                    var $sitesMatrix = $('#sites-matrix'),
                        arrSites = data.siteList || [],
                        html = '',
                        siteTmpl = self.tmpl('<span style="top:<%=y-5%>px; left:<%=x-5%>px;" title="Status: <%=status%>&#10;ID: <%=siteId%>&#10;Site: <%=siteName%>&#10;Zone: <%=areaName%>" data-site-id="<%=siteId%>" class="site-status-<%=status.toLowerCase()%>"></span>');

                    for (var i = 0; i < arrSites.length && i < maxSites; i++) {
                        html += siteTmpl({
                            siteId: arrSites[i].id,
                            siteName: arrSites[i].name,
                            areaId: arrSites[i].areaId,
                            areaName: arrSites[i].areaName,
                            status: arrSites[i].status,
                            x: arrSites[i].x_pos,
                            y: arrSites[i].y_pos
                        });
                    };

                    $sitesMatrix.html(html);
                },
                error: function(err) {
                    //window.alert('Failed to get the sites matrix info.');
                    console.error('获取站点矩阵失败。');
                },
                complete: function(xhr, textStatus) {
                    if (window.config['Automatic_Refresh_Interval']) {                        
                        window.setTimeout(function() {
                            self.renderSitesMatrix();
                        }, window.config['Automatic_Refresh_Interval']);
                    }
                }
            });
        },

        //渲染图表
        renderStatistics: function() {
            var d = new Date().getTime(),
                startTime = d - 30 * 24 * 3600 * 1000,
                endTime = d,
                x = []; //x 坐标

            //加载全部区域数据
            $.ajax({
                type: "GET",
                url: "/alarmsStat",
                data: {
                    "startTime": startTime,
                    "endTime": endTime
                },
                dataType: "json",
                success: function(data) {
                    //格式化 x 坐标
                    for (var i = 0; i < data.datetime.length; i++) {
                        var d = new Date(data.datetime[i]);
                        x.push(d.getMonth() + 1 + '-' + d.getDate());
                    };

                    $('.statistics').highcharts({
                        chart: {
                            type: 'line'
                        },
                        title: {
                            text: 'Alarms statistics',
                            align: 'left',
                            x: 10,
                            y: 20,
                            margin: 10
                        },
                        subtitle: {
                            text: 'The last 30 days',
                            x: -30
                        },
                        xAxis: {
                            staggerLines: 20,
                            tickmarkPlacement: 'on',
                            categories: x
                        },
                        yAxis: {
                            title: {
                                text: 'Alarms Number'
                            },
                            min: 0
                        },
                        tooltip: {
                            valueSuffix: ''
                        },
                        legend: {
                            layout: 'vertical',
                            align: 'right',
                            verticalAlign: 'middle',
                            borderWidth: 0
                        },
                        series: [{
                            name: 'Fatal',
                            color: '#c00',
                            data: data.fatal
                        }, {
                            name: 'Ugrent',
                            color: '#f60',
                            data: data.ugrent
                        }, {
                            name: 'Important',
                            color: '#fc0',
                            data: data.important
                        }, {
                            name: 'General',
                            color: '#06c',
                            data: data.general
                        }],
                        credits: false
                    });
                },
                error: function(err) {
                    //window.alert('Failed to get the alarm data.');
                    console.error('获取告警失败。');
                }
            })

        }
    });

    window.page = new Page();
});