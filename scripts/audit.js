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

            $grid.data('grid', grid);
        }
    });

    window.page = new Page();
});