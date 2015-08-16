$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var $grid = $('.grid'),
                grid;
            
            grid = $grid.grid({
                "ajax": "/mock/alarms.json"
            });

            $grid.data('grid', grid);
        }
    });

    window.page = new Page();
});