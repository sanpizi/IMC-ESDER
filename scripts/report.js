$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var $grid = $('.grid'),
                grid;
            
            grid = $grid.grid({
                ajax: "/hisalarms",
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
        }
    });

    window.page = new Page();
});