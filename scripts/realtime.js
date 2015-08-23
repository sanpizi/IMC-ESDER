$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var $grid = $('.grid'),
                grid;
            
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