$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            //渲染统计
            this.renderGlobalStats();

            //渲染矩阵
            this.renderSitesMatrix();

            //渲染图表
            //this.renderStatistics();
        },

        //渲染统计
        renderGlobalStats: function() {
            var slef = this;

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
                }
            });
        },

        //渲染矩阵
        renderSitesMatrix: function() {
            var slef = this,
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
                    var arrSites = data.siteList || [],
                        html = '<div id="sites-matrix">',
                        siteTmpl = slef.tmpl('<span title="Status: <%=status%>&#10;ID: <%=siteId%>&#10;Name: <%=siteName%>&#10;Area: <%=areaName%>" data-site-id="<%=siteId%>" data-site-name="<%=siteName%>" data-area-id="<%=areaId%>" data-area-name="<%=areaName%>" class="site-status-<%=status.toLowerCase()%>"></span>');

                    for (var i = 0; i < arrSites.length && i < maxSites; i++) {
                        html += siteTmpl({
                            siteId: arrSites[i].id,
                            siteName: arrSites[i].name,
                            areaId: arrSites[i].areaId,
                            areaName: arrSites[i].areaName,
                            status: arrSites[i].status
                        });
                    };

                    html += '</div>';

                    $('.sitemap').html(html);

                    //绑定站点点击事件
                    $('#sites-matrix').on('click','span', function() {
                        window.location.href = '/realtime-details.html?siteId=' + this.getAttribute('data-site-id');
                    });
                },
                error: function(err) {
                    //window.alert('Failed to get the sites matrix info.');
                    console.error('获取站点矩阵失败。');
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