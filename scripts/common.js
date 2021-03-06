//系统配置项
window.config = {
    //自动刷新时间间隔，单位：毫秒。
    //关闭自动刷新请设为 0。
    "Automatic_Refresh_Interval": 30 * 1000, 

    //是否缓存树菜单中加载过的站点。
    //false: 每次展开菜单时都重新从服务端取数据。
    //true: 第一次从从服务端取数据并缓存，此后使用缓存，直到刷新页面。
    "Is_Tree_Sites_Cache": false,

    //是否允许树菜单中同时展开多个区域
    //false: 同一时间只能有一个区域展开状态，自动刷新能生效
    //true: 允许同一时间有多个区域展开，自动刷新将失效（因为可能产生大量 ajax 请求，增加服务端压力）。
    "Is_Allow_Expand_Multiple_Area": false,

    //站点搜索时，最多显示的结果个数
    "Maximum_Number_Of_Site_Search_Result": 5
}


//页面基类
var Page = function() {
    if (this.init && typeof this.init === 'function') {
        this.init.apply(this, arguments);
    } else {
        this._init.apply(this, arguments);
    }
}

Page.prototype = {
    //初始化
    init: function() {
        var self = this;

        //当 session 丢失以后
        $(document).ajaxError(function(event, xhr, settings, thrownError) {
            var currentUrl = encodeURIComponent(location.href);
            if (xhr.status === 401) {
                if (!self.sessionTimeout) {
                    self.sessionTimeout = true;
                    alert('Session timeout, you need to log in again.');
                    location.href = '/index.html?goUrl=' + currentUrl;
                }
            }
        });

        //获取 url 参数
        this.getUrlParams();

        //生成 heaer
        this.renderHeader();

        //生成 footer
        this.renderFooter();

        //老版本的 chrome 在 domready 时有高度计算 bug，加 setTimeout 可解决
        window.setTimeout(function() {
            self.initTreeHeight();
        }, 1);

        //窗口大小大变后重新调整高度
        $(window).on('resize', function() {
            self.initTreeHeight();
        })

        //生成树菜单
        this.loadArea();

        //初始化区域搜索框
        this.initSearchArea();

        //初始化右上角下拉菜单
        this.initDropdown();

        //绑定 log out 事件
        $('#logout').on('click', function() {
            self.logout();
        });

        //权限判断
        setTimeout(function() {
            var curUserRole = self.role[self.getCookie('role')];
            $('[role]').each(function(index, el) {
                var $el = $(el),
                    role = $el.attr('role');

                if (curUserRole >= role) {
                    $el.show();
                }
            });
        }, 1);
    },

    //角色权限定义
    role: {        
        "admin": 4,
        "engineer": 3,
        "operator": 2,
        "guest": 1
    },

    //获取 url 参数
    getUrlParams: function() {
        var search = location.search,
            params = {};

        if (search) {
            var arrParams = search.replace(/^\?/, '').split('&');

            for (var i = 0; i < arrParams.length; i++) {
                var arr = arrParams[i].split('=');
                params[arr[0]] = arr[1] || '';
            }
        }

        //添加到全局上下文
        this.params = params;
    },

    //微型 JS 模板引擎
    tmpl: function(str, data) {
        var fn = new Function("obj",
            "var p=[]; with(obj){p.push('" + str
            .replace(/[\r\t\n]/g, " ")
            .split("<%").join("\t")
            .replace(/((^|%>)[^\t]*)'/g, function($1) {
                return $1.replace(/'/g, '\r') + '\r'
            })
            .replace(/\t=(.*?)%>/g, "',$1,'")
            .split("\t").join("');")
            .split("%>").join("p.push('")
            .split("\r").join("\\'") + "');}return p.join('');");
        return data ? fn(data) : fn;
    },

    //写 cookie
    setCookie: function(key, value) {
        if (!key || !value) {
            return;
        }

        document.cookie = key + "=" + encodeURIComponent(value);
    },

    //读 cookie
    getCookie: function(key) {
        var value = '';
        var reg = new RegExp("(^| )" + key + "=([^;]*)(;|$)");
        var arr = document.cookie.match(reg) || [];

        if (arr.length) {
            value = decodeURIComponent(arr[2]);
        }

        return value;
    },

    //删 cookie
    delCookie: function(key) {
        var exp = new Date();
        exp.setTime(exp.getTime() - 1);
        var cval = this.getCookie(key);
        if (cval != null) {
            document.cookie = key + "=" + cval + ";expires=" + exp.toGMTString();
        }
    },

    //生成 header
    renderHeader: function() {
        var headTmpl = this.tmpl('' +
            '		<div class="logo">IMC-ESDER</div>' +
            '		<div class="topbar">' +
            '			<ul>' +
            '				<li class="dropdown"><a href="javascript:void(0);"><%=username%><span class="arrow-down"></span></a>' +
            '					<ul>' +
            '						<li><a href="change-password.html">Change password</a></li>' +
            '						<li><a id="logout" href="javascript:void(0);">Log out</a></li>' +
            '					</ul>' +
            '				</li>' +
            '				<li><a href="help.html">Help</a></li>' +
            '			</ul>' +
            '		</div>' +
            '		<div class="navbar">' +
            '			<ul>' +
            '				<li><a href="overview.html" <%if (!path.indexOf("/overview")) {%>class="selected"<%}%>>Overview</a></li>' +
            '				<li><a href="active-alarm.html" <%if (!path.indexOf("/active-alarm")) {%>class="selected"<%}%>>Alarms</a></li>' +
            '               <li><a href="history-alarm.html" <%if (!path.indexOf("/history")) {%>class="selected"<%}%>>History</a></li>' +
            '				<li role="2" style="display:none"><a href="report-monthly.html" <%if (!path.indexOf("/report")) {%>class="selected"<%}%>>Reports</a></li>' +
            '				<li><a href="settings-single.html" <%if (!path.indexOf("/settings")) {%>class="selected"<%}%>>Settings</a></li>' +
            '			</ul>' +
            '		</div>');

        $('.header').html(headTmpl({
            username: this.getCookie('username'),
            path: location.pathname
        }));
    },

    //生成 header
    renderFooter: function() {
        var footerHtml = 'Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.';
        $('.footer').html(footerHtml);
    },

    //调整树菜单高度
    initTreeHeight: function() {
        //与右边内容同高
        // var
        //     $areaTree = $('.area-tree'),
        //     $container = $('.container'),
        //     headerHeight = $('.header').outerHeight(),
        //     containerMargin = parseInt($container.css('margin-top')) + parseInt($container.css('margin-bottom')),
        //     footerHeight = $('.footer').outerHeight(true),
        //     searchHeight = $('.area-search').outerHeight(true),
        //     areaTreePadding = parseInt($areaTree.css('padding-top')) + parseInt($areaTree.css('padding-bottom')),
        //     mainHeight = $('.main').height(),
        //     sideBarHeight = $(window).height() - headerHeight - containerMargin - footerHeight;

        // if (mainHeight > sideBarHeight) {
        //     $areaTree.height(mainHeight - searchHeight - areaTreePadding);
        // } else {
        //     $areaTree.height(sideBarHeight - searchHeight - areaTreePadding);
        // }

        //适应窗口高度
        $('.area-tree').height($(window).height() - 205);

        //把 map 的高度也改一下
        $('.sitemap').height($(window).height() - 157);
    },

    //初始化区域搜索框
    initSearchArea: function() {
        var searchTmpl = this.tmpl('' +
            '       <ul>' +
            '       <% for (var i = 0; i < arrResult.length; i++) { %>' +
            '           <li data-site-id="<%= arrResult[i].id %>"  title="<%= arrResult[i].areaName %>" <% if (i ===0) { %>class="selected"<% } %>><%= arrResult[i].name %></li>' +
            '       <% } %>' +
            '       </ul>'),
            searchHtml = '',
            $searchResult = $('.search-result'),
            $search = $('.search .keywords'),
            timer,
            siteList,
            arrResult;

        //获取焦点时加载所有站点数据供搜索
        $search.on('focus', function() {
            siteList = $('.search').data('siteList') || (window.sessionStorage && sessionStorage['siteList'] && JSON.parse(sessionStorage['siteList'])) || null;

            if (!siteList) {
                //获取所有站点信息
                $.ajax({
                    type: "GET",
                    url: "/sites",
                    dataType: "json",
                    beforeSend: function() {
                        //加载数据期间禁用
                        $search.prop('disabled', true);
                    },
                    success: function(data) {
                        //给站点搜索框提供搜索数据
                        if (window.sessionStorage) {
                            sessionStorage['siteList'] = JSON.stringify(data.siteList);
                        }
                        $('.search').data('siteList', data.siteList);
                    },
                    error: function(err) {
                        //window.alert('Failed to get the full sites info.');
                        console.error('获取所有站点数据失败。');
                    },
                    complete: function(xhr, textStatus) {    
                        //加载完数据后启用
                        $search.prop('disabled', false).focus();
                    }
                });
            }


            //页面跳转函数
            var jump = function(li) {
                var url = '/realtime.html?siteId=' + $(li).attr('data-site-id');
                window.location.href = url;
            }

            //点击搜索结果时        
            $searchResult.on('click', 'li', function() {
                jump(this);
            });

            //搜索框失去焦点时隐藏搜索结果
            // $search.on('blur', function() {
            //     setTimeout(function() {
            //         $searchResult.slideUp(100);
            //     }, 100);            
            // });

            //点击空白地方时隐藏搜索结果
            $(document).on('click', function() {
                $searchResult.slideUp(100);
            });


            //搜索框按下键盘时
            $search.on('keydown', function(e) {
                if (this.disabled) return;

                var keycode = e.keyCode,
                    $selected = $searchResult.find('.selected');

                switch (keycode) {
                    case 13:
                        jump($selected);
                        break;                    
                    case 38:
                        moveSelected('up');
                        e.preventDefault();
                        break;
                    case 40:
                        moveSelected('down');
                        e.preventDefault();
                        break;
                }

                function moveSelected(direction) {
                    var li = $searchResult.find('li'),
                        selectedIndex = li.filter('.selected').index();

                    if (direction === 'up' && selectedIndex > 0) {
                        li.removeClass('selected').eq(selectedIndex - 1).addClass('selected');
                    }

                    if (direction === 'down' && selectedIndex < li.length - 1) {
                        li.removeClass('selected').eq(selectedIndex + 1).addClass('selected');
                    }
                };
            });

            //搜索框按下键盘时
            $search.on('keyup', function(e) {
                if (this.disabled) return;

                var maxNum = window.config["Maximum_Number_Of_Site_Search_Result"],
                    $input = $(this),
                    keywords = $input.val();

                if (keywords !== $input.data('keywords')) {
                    $input.data('keywords', keywords);

                    keywords = $.trim(keywords);
                    if (keywords === '') {
                        $searchResult.slideUp(100);
                    } else {
                        if (timer) clearTimeout(timer);

                        //从 siteList 中搜索
                        arrResult = search(keywords, siteList);

                        if (arrResult.length) {
                            if (arrResult.length > maxNum) {
                                arrResult.length = maxNum;
                            }

                            //填充搜索结果
                            searchHtml = searchTmpl({
                                arrResult: arrResult
                            });
                            $searchResult.html(searchHtml).slideDown(100);

                        } else {
                            $searchResult.html('<div style="padding:5px 15px;color:#999">No matched site.</div>').slideDown(100);
                            timer = setTimeout(function() {
                                $searchResult.slideUp(100);
                            }, 1500);
                        }
                    }
                }

                //从 siteList 中搜索
                function search(keywords, siteList) {
                    var arrKeywords = keywords.toLowerCase().split(''),
                        result = $.extend(true, [], siteList);

                    if (result.length) {
                        for (var i = 0; i < arrKeywords.length; i++) {
                            for (var j = 0; j < result.length; j++) {
                                if (result[j].matched === undefined) {
                                    result[j].matched = result[j].name.toLowerCase();
                                    result[j].order = [];
                                }

                                var index = result[j].matched.indexOf(arrKeywords[i]);
                                if (~index) {
                                    result[j].order.push(('000' + index).slice(-3));
                                    result[j].matched = result[j].matched.slice(index + 1);
                                    result[j].name = result[j].name.replace(new RegExp('(' + arrKeywords[i] + ')(' + result[j].matched + '$)', 'i'), '<strong>$1</strong>$2');
                                } else {
                                    result.splice(j, 1);
                                    j--;
                                }
                            };
                        };
                    };

                    result.sort(function(a, b) {
                        return a.order.join('') > b.order.join('');
                    });

                    return result
                }
            });

        });
    },

    //初始化右上角下拉菜单
    initDropdown: function() {
        var $dropdown = $('.header .topbar .dropdown ul');
        $('.header .dropdown>a').on('mouseover', function() {
            $dropdown.slideDown(100);
        });
        $dropdown.on('mouseleave', function() {
            setTimeout(function() {
                $dropdown.slideUp(100);
            }, 500);
        });
        $(document).on('click', function() {
            $dropdown.slideUp(100);
        });
    },

    //生成树菜单
    loadArea: function() {
        var self = this,
            $tree = $('.area-tree');

        //先从 sessionStorage 取数据
        if (window.sessionStorage) {
            var areaList = sessionStorage.areaList || [];
            if (areaList.length) {
                //生成树菜单
                areaList = JSON.parse(areaList);
                makeTree.call(self, areaList);

                return;
            }
        }

        //加载全部区域数据
        $.ajax({
            type: "GET",
            url: "/areas",
            data: null,
            dataType: "json",
            success: function(data) {
                if (data.areaList) {
                    //缓存到数据模型
                    self.areaList = data.areaList;

                    //缓存到 sessionStorage
                    if (window.sessionStorage) {
                        sessionStorage.areaList = JSON.stringify(data.areaList);
                    }

                    //生成树菜单
                    makeTree.call(self, data.areaList);

                } else {
                    //window.alert('Failed to get the area data.');
                    console.error('获取区域列表失败。')
                }
            },
            error: function(err) {
                //window.alert('Failed to get the area data.');
                console.error('获取区域列表失败。');
            }
        });

        //生成树
        function makeTree(data) {
            var self = this,
                isNeedCacheSites = false,
                menuStatus = {
                    //增加
                    add: function(id) {
                        var strMenuStatus = ',' + self.getCookie('menuStatus') + ',';

                        if (!~strMenuStatus.indexOf(',' + id + ',')) {
                            var newMenuStatus = strMenuStatus + id;
                            self.setCookie('menuStatus', newMenuStatus.replace(/^,+/g, ''));
                        }
                    },

                    //删除
                    remove: function(id) {
                        var strMenuStatus = ',' + self.getCookie('menuStatus') + ',';

                        if (~strMenuStatus.indexOf(',' + id + ',')) {
                            var newMenuStatus = strMenuStatus.replace(',' + id, '').replace(/^,|,$/g, '');
                            if (newMenuStatus) {
                                self.setCookie('menuStatus', newMenuStatus);
                            } else {
                                self.delCookie('menuStatus');
                            }
                        }
                    }
                };

            //生成树的 html
            var html = '<ul id="tree-root"><li class="expanded">All Zones<ul id="tree-menu">',
                menuTmpl = self.tmpl('<li class="<%=status%>" data-area-id="<%=id%>"><span class="area"><%=name%></span><ul></ul></li>'),
                menuStatusStr = ',' + this.getCookie('menuStatus') + ','; //树的展开状态

            for (var i = 0; i < data.length; i++) {
                //树的展开状态
                if (~menuStatusStr.indexOf(',' + data[i].id + ',')) {
                    data[i].status = 'expanded';
                } else {
                    data[i].status = 'closed';
                }

                html += menuTmpl(data[i]);
            };

            html += '</ul></li></ul>';

            //渲染到树
            $tree.html(html);

            //加载展开状态的站点
            $('#tree-menu li.expanded').each(function(index, el) {
                self.loadSites($(el));
            });


            //绑定菜单点击事件
            $tree.on('click', '.area', function() {                
                var $li = $(this).closest('li');

                //如果只允许同一时刻展开一个区域
                if (!window.config['Is_Allow_Expand_Multiple_Area']) {
                    $li.siblings('.expanded').each(function(index, el) {
                        foldMenu($(el));
                    });;
                }

                //切换展开/收拢样式
                if ($li.attr('class') === 'closed') {
                    $li.attr('class', 'expanded');

                    //缓存菜单状态
                    menuStatus.add($li.attr('data-area-id'));

                    //不缓存
                    if (!isNeedCacheSites) {
                        $li.find('ul').html('').show();
                    }

                    //无站点则加载数据
                    if ($li.find('li').length === 0) {
                        //加载站点数据
                        self.loadSites($li);
                    } else {
                        //展开菜单
                        $li.find('ul').slideDown(100);
                    }
                } else {
                    foldMenu($li);
                }

                function foldMenu($li) {
                    //收拢菜单
                    $li.attr('class', 'closed').find('ul').slideUp(100);

                    //缓存菜单状态
                    menuStatus.remove($li.attr('data-area-id'));
                }
            });

            //菜单操作事件，记录滚动高度到 cookie 中，供刷新后自动定位到
            $tree.on('mousedown', function() {
                var scrollTop = $(this).scrollTop();
                self.setCookie('menuScrollTop', scrollTop);
            });

            //加载指定区域站点
            // function loadSites(el) {   
            //     var refreshDelay = window.config['Automatic_Refresh_Interval'],
            //         $el = $(el);
            //     self.loadSites($el, function() {
            //         if (refreshDelay) {
            //             setTimeout(function() {
            //                 loadSites($el);
            //             }, refreshDelay);
            //         }
            //     });
            // }
        }
    },

    //加载站点数据
    loadSites: function($area) {
        var self = this,
            areaId = $area.attr('data-area-id');

        //加载指定区域站点
        $.ajax({
            type: "GET",
            url: "/sites",
            data: {
                areaId: areaId
            },
            dataType: "json",
            beforeSend: function(xhr) {
                //显示 loading 状态
                $area.find('ul').html('<span style="color:#999">Loading...</span>');
            },
            complete: function() {
                //单区域展开模式下可自动刷新
                if (window.config['Automatic_Refresh_Interval'] && !window.config['Is_Allow_Expand_Multiple_Area']) {
                    var refreshDelay = window.config['Automatic_Refresh_Interval'];

                    //防止多重刷新
                    if (window.intervalRefreshArea) {
                        clearTimeout(window.intervalRefreshArea);
                    }

                    //刷新定时
                    window.intervalRefreshArea = setTimeout(function() {
                        //从 cookie 取展开状态的 area
                        var arrArea = self.getCookie('menuStatus').split(',');

                        //只允许一个区域展开时定时刷新
                        if (arrArea.length === 1) {
                            var $area = $('li[data-area-id="' + arrArea.toString() + '"]');

                            if ($area.length) {
                                self.loadSites($area);
                            }                            
                        }                        
                    }, refreshDelay);
                };
            },
            success: function(data) {
                if (data.siteList) {
                    //添加到数据模型
                    var area = self.getAreaById(areaId);
                    if (area) {
                        area.siteList = data.siteList;
                    }

                    //站点列表
                    if (data.siteList.length) {
                        //生成 html
                        var html = '';
                        var siteTmpl = self.tmpl('<li data-site-id="<%=id%>"><span title="<%=status%>" class="site-status-<%=status.toLowerCase()%>"></span><a<%if (selected) {%> class="selected"<%}%> href="realtime.html?siteId=<%=id%>"><%=name%></a></li>');
                        for (var i = 0, l = data.siteList.length; i < l; i++) {
                            //根据 url 中的参数判断选中状态
                            data.siteList[i].selected = self.params.siteId == data.siteList[i].id;

                            html += siteTmpl(data.siteList[i]);
                        };

                        //添加到菜单
                        $area.find('ul').html(html);

                        //自动定位滚动条到选中菜单的位置
                        var scrollTop = self.getCookie('menuScrollTop');
                        if (scrollTop) {
                            $('.area-tree').scrollTop(scrollTop);
                        }
                    } else {
                        $area.find('ul').html('<span style="color:#999">No data.</span>');
                    }
                } else {
                    $area.find('ul').html('<span style="color:#999">No data.</span>');
                    //window.alert('Failed to get the sites data.')
                    console.error('获取站点数据失败。')
                }
            },
            error: function(err) {
                //window.alert('Failed to get the sites data.')
                console.error('获取站点数据失败。');
            }
        });
    },

    //通过 areaId 返回 area 数据模型
    getAreaById: function(areaId) {
        if (this.areaList) {
            var areaList = this.areaList;
            for (var i = areaList.length - 1; i >= 0; i--) {
                if (areaList[i].id === areaId) return areaList[i];
            };
        }

        return [];
    },

    //log out
    logout: function() {
        $.ajax({
            type: "get",
            url: "/logout",
            dataType: "json",
            success: function(data) {
                window.location.href = '/index.html';
            },
            error: function(error) {
                window.location.href = '/index.html';
                console.error('退出失败。');
            }
        });
    }

}

Page.extend = function(obj) {
    if (Object.prototype.toString.call(obj) === '[object Object]') {
        obj._init = this.prototype.init;
        obj.constructor = this;
        $.extend(this.prototype, obj);
    }
}

//扩展 jquery 的 grid 插件
$.fn.extend({
    grid: function(option) {
        var $grid = $(this);

        if (!option) {
            console.error('Grid 缺少配置。');
            return;
        }
        if (!option.ajax.length) {
            console.error('Grid 缺少 url 配置。');
            return;
        }
        if (!option.columns.length) {
            console.error('Grid 缺少 header 配置。');
            return;
        }

        var defaultOption = {
            fnComplete: null,
            paging: true,
            pageSize: 10,
            pageNum: 9, //分页码个数
            maxRecordAmount: 10000, //最大展示记录条数
            currentPage: 1,
            params: {}
        };

        //合并配置
        var option = $.extend(true, defaultOption, option);

        //表格对象
        var grid = {
            element: $grid, //表格所在 dom 元素
            option: option, //表格配置

            //初始化表格
            init: function() {
                var self = this,
                    gridHtml = Page.prototype.tmpl('<table class="data-grid" cellpadding="0" cellspacing="0" >'+
                    '    <thead>'+
                    '        <tr>'+
                    '<%for (var i = 0; i < arrCols.length; i++) {%>'+
                    '            <th><%=arrCols[i].header%></th>'+
                    '<%}%>'+
                    '        </tr>'+
                    '    </thead>'+
                    '    <tbody></tbody>'+
                    '</table>', {
                        arrCols: self.option.columns
                    });

                //生成表头与基本表格结构
                $grid.html(gridHtml);

                //显示加载中
                this.loading(); 

                //数据查询参数
                var queryData = self.option.paging ? $.extend({
                        start: self.option.pageSize * (self.option.currentPage - 1) + 1,
                        amount: self.option.pageSize
                    }, self.option.params) : self.option.params;

                //取表格数据
                $.ajax({                    
                    type: "GET",
                    url: this.option.ajax,
                    data: queryData,
                    dataType: "json",
                    success: function(data) {
                        var arrRecords = data.recordList,
                            $tbody = self.element.find('.data-grid>tbody');

                        //记录数超过最大展示数时将被忽略
                        if (data.totalRecords > self.option.maxRecordAmount) {
                            data.totalRecords = self.option.maxRecordAmount;

                            //给出数据被忽略的提示
                            self.element.remove('.paging-overflow').append('<div class="paging-overflow">Show only the first ' + self.option.maxRecordAmount + ' entries.</div>');
                        }

                        if (arrRecords.length === 0) {
                            $tbody.html('<tr><td colspan="' + self.option.columns.length + '" class="g-error">No data.</td></tr>');
                        } else {
                            var html = '',
                                recordsNum = (self.option.pageSize < arrRecords.length && self.paging) ? self.option.pageSize : arrRecords.length;
                            for (var i = 0; i < recordsNum; i++) {
                                var tr = '<tr>',
                                    record = arrRecords[i];
                                for (var j = 0; j < self.option.columns.length; j++) {
                                    var content = '';
                                    try {
                                        content = self.option.columns[j].content(record)
                                    } catch (err) {
                                        console.error('格式化表格数据出错。')
                                    }
                                    tr += '<td>' + content + '</td>';
                                };
                                tr += '</tr>';
                                html = html + tr;
                            };

                            //填充数据到页面
                            $tbody.html(html);

                            //是否分页
                            if (self.option.paging) {
                                var pageSize = self.option.pageSize,
                                    totalRecords = data.totalRecords,
                                    pageAmount = Math.ceil(totalRecords / pageSize),
                                    currentPage = (function() {
                                        var currentPage = 1;
                                        if (self.option.currentPage > 0) {
                                            if (self.option.currentPage < pageAmount) {
                                                currentPage = self.option.currentPage;
                                            } else {
                                                currentPage = pageAmount;
                                            }
                                        }
                                        return currentPage;
                                    })(),
                                    pageNum = pageAmount < self.option.pageNum ? pageAmount : self.option.pageNum,
                                    pageStart = (function() {
                                        var pageStart = 1,
                                            offset = Math.floor(self.option.pageNum / 2);
                                        if (currentPage > offset && currentPage <= pageAmount - offset) {
                                            pageStart = currentPage - offset;
                                        } else if (pageAmount < currentPage + offset) {
                                            pageStart = pageAmount - pageNum + 1;
                                        }
                                        return pageStart;
                                    })(),
                                    pagingTmpl = Page.prototype.tmpl('<div class="paging">'+
                                    '     <ul id="turnTo">'+
                                    '         <li <%if (currentPage === 1) {%>class="p-disabled"<%}%>>Previous</li>'+
                                    '<%if (pageStart > 1) {%>' +
                                    '         <li >1</li>'+
                                    '         <li class="p-disabled">...</li>'+
                                    '<%}%>' +
                                    '<%for (var k = 0; k < pageNum; k++) {%>'+
                                    '         <li <%if (currentPage === pageStart + k) {%>class="p-disabled p-current"<%}%>><%=pageStart + k%></li>'+
                                    '<%}%>'+
                                    '<%if (pageStart + pageNum - 1 < pageAmount) {%>' +
                                    '         <li class="p-disabled">...</li>'+
                                    '         <li ><%=pageAmount%></li>'+
                                    '<%}%>' +
                                    '         <li <%if (currentPage === pageAmount) {%>class="p-disabled"<%}%>>Next</li>'+
                                    '     </ul>'+
                                    '     <div class="p-stat">Showing <%=recordStart%> to <%=recordEnd%> of <%=recordTotal%> entries</div>'+
                                    ' </div>');

                                var pagingHtml = pagingTmpl({
                                    recordStart: pageSize * (currentPage - 1) + 1,
                                    recordEnd: totalRecords < currentPage * pageSize ? totalRecords : currentPage * pageSize,
                                    recordTotal: totalRecords,
                                    currentPage: currentPage,
                                    pageAmount: pageAmount,
                                    pageStart: pageStart,
                                    pageNum: pageNum
                                });

                                //插入表格内容
                                self.element.remove('.paging').find('.data-grid').after(pagingHtml);

                                //绑定翻页事件
                                $('#turnTo').on('click', 'li', function() {
                                    $li = $(this);
                                    if ($li.hasClass('p-disabled')) {
                                        return;
                                    }

                                    //跳转页
                                    self.turnTo($li.text());
                                });
                            }
                        }

                        //重新调整菜单高度
                        Page.prototype.initTreeHeight();

                        //回调 fnComplete
                        if (self.option.fnComplete) {
                            self.option.fnComplete(data);
                        }
                    },
                    error: function(err) {
                        console.error('加载表格数据失败。')
                        self.element.find('tbody').html('<tr><td colspan="' + self.option.columns.length + '" class="g-error">No data.</td></tr>');
                    }
                });
            },

            //翻页
            turnTo: function(pageNum) {
                if (isNaN(pageNum)) {
                    //快捷翻页
                    if (pageNum === 'Previous') {
                        this.option.currentPage--;
                    } else {                        
                        this.option.currentPage++;
                    }
                } else {
                    //跳转到指定页
                    this.option.currentPage = parseInt(pageNum);
                }

                //重新初始化表格
                this.init();
            },

            //表格显示"Loading..."
            loading: function() {
                if (this.option.paging) {
                    this.element.find('.paging').remove();
                }
                
                this.element.find('tbody').html('<tr><td colspan="' + this.option.columns.length + '" class="g-loading">Loading...</td></tr>');
            },

            //导出数据
            exportData: function(url) {//数据查询参数

                //弹出下载页面
                window.open(url + '?' + $.param(this.option.params));
            }
        }

        grid.init();

        grid.element.data('grid', grid);

        return grid;
    }
});