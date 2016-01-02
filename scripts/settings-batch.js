$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var self = this;

            //初始化站点选择器
            this.initSitesSelector();

            //更新影响站点数
            //this.updateAffectSites();

            //拉取原始数据
            //self.getParams();

            //表单事件
            $('.form').on('click', '[type="button"]', function(e) {
                var $row = $(this).closest('tr'),
                    $input = $row.find('.setting-input'),
                    $target = $(e.target);

                if ($target.hasClass('save')) { //点击保存按钮
                    //提交参数设置
                    self.setParams($row);
                } else if ($target.hasClass('reset')) { //点击默认值按钮
                    $input.val($input.attr('data-default-value'));
                }

            });
        },

        //验证表单
        validateForm: function() {
            var result = true,
                self = this;

            return result;
        },

        //拉取原始数据
        getParams: function() {
            $.ajax({
                type: "GET",
                url: "/settings",
                dataType: "json",
                data: {},
                success: function(data) {
                    $('#p_1').val(data['1']);
                },
                error: function(err) {
                    //window.alert('Failed to get the settings.');
                    console.error('获取配置信息失败。');
                }
            });
        },

        //保存设置
        setParams: function($row) {
            var self = this,
                selectedSites = self.selector.getSelected().join(','),
                signalName = $row.find('td:first').text(),
                bt = $row.find('.save')[0],
                input = $row.find('.setting-input')[0];

            if (!selectedSites) {
                alert('No site was selected.');
                return;
            }

            if (!input.value) {
                alert('Please enter a value.');
                input.focus();
                return;
            }

            $.ajax({
                type: "POST",
                url: "/config",
                dataType: "json",
                data: {
                    siteId: selectedSites,
                    signalName: signalName,
                    newValue: input.value
                },
                beforeSend: function() {
                    //禁用按钮
                    bt.disabled = true;
                },
                success: function(data) {
                    if (data.result === 0) {
                        alert('Operation sent successfully.');
                    } else {
                        alert(data.errMsg);
                    }
                },
                error: function(err) {
                    //window.alert('Failed to save the settings.');
                    console.error('保存配置失败。');
                },
                complete: function() {
                    //启用按钮
                    bt.disabled = false;
                }
            });
        },

        //初始化站点选择器
        initSitesSelector: function() {
            //拉取树形菜单数据            
            var self = this;

            $.ajax({
                type: "GET",
                url: "/sites",
                dataType: "json",
                success: function(data) {
                    var siteList = data.siteList,
                        areas = {},
                        sites = {},
                        treeData = [{
                            "text": "All Zones",
                            "state": {
                                "opened": true
                            },
                            "children": []
                        }];

                    //提取所有区域、站点数据
                    for (var i = 0; i < siteList.length; i++) {
                        var areaId = siteList[i].areaId,
                            areaName = siteList[i].areaName,
                            site = {
                                "id": "site_" + siteList[i].id,
                                "text": siteList[i].name,
                                "icon": "jstree-" + siteList[i].status
                            };

                        //建立站点数据模型
                        sites[siteList[i].id] = siteList[i];

                        //提取所有区域
                        if (!(areaId in areas)) {
                            areas[areaId] = {
                                "id": "area_" + areaId,
                                "text": areaName,
                                "state": {
                                    "opened": false
                                },
                                "children": []
                            }
                        }

                        areas[areaId].children.push(site);
                    };

                    //建立站点数据模型
                    self.sites = sites;

                    //构造 jsTree 所需要的数据格式
                    for(var x in areas) {
                        treeData[0].children.push(areas[x]);
                    }

                    //创建树
                    self.makeTree(treeData);
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取站点数据失败。');
                }
            });

        },

        //生成树形菜单
        makeTree: function(data) {
            var self = this;

            //初始化树形菜单
            $('.selector').jstree({
                'plugins': ["checkbox"],
                'core': {
                    'data': data
                }
            }).on('changed.jstree', function (e, data) {
                //获取选中的末级节点数
                var arrSelectedSites = data.instance.get_bottom_selected(),
                    selectedSites = {};

                for (var i = 0; i < arrSelectedSites.length; i++) {
                    selectedSites[arrSelectedSites[i].slice(5)] = 1;
                }

                //更新站点数据模型
                for (var x in self.sites) {
                    if (x in selectedSites) {
                        self.sites[x].selected = true;
                    } else {
                        self.sites[x].selected = false;
                    }
                }

                //更新受影响站点数
                $('#selected-sites-count').html(arrSelectedSites.length);

                //刷新已选区域
                self.selector.refreshSelected();
            });

            //绑定已选项删除事件
            $('.selected').on('click', '.delete', function() {
                //要删除的站点
                var siteId = $(this).prev('.site').attr('data-site-id'),
                    nodeId = 'site_' + siteId;

                //操作
                $('.selector').jstree().uncheck_node(nodeId);
            });
        },

        selector: {
            //更新已选站点
            refreshSelected: function() {
                var $selected = $('.selected ul'),
                    sites = window.page.sites || {},
                    siteTmpl = Page.prototype.tmpl('' +
                        '<%for (var x in sites) {%>'+
                            '<%if (sites[x].selected) {%>'+
                                '<li>' +
                                    '<span class="site" data-site-id="<%=sites[x].id%>" title="ID: <%=sites[x].id%>&#10;Site: <%=sites[x].name%>&#10;Zone: <%=sites[x].areaName%>"><%=sites[x].areaName%>/<%=sites[x].name%></span>' +
                                    '<span class="delete" title="Remove">&times;</span>' +
                                '</li>' + 
                            '<%}%>'+
                        '<%}%>'+
                        '');

                var html = siteTmpl({sites: sites});

                $selected.html(html);
            },

            //获取已选择站点 ID 数组
            getSelected: function() {
                var arrSites = $('.selector').jstree().get_bottom_checked();

                arrSites = $.map(arrSites, function(site) {
                    return site.slice(5);
                })

                return arrSites;
            }
        },

        //更新设置站点数
        updateAffectSites: function() {
            var self = this,
                $area = $('#areaId');
            $site = $('#siteId');

            $.ajax({
                type: "GET",
                url: "/sites",
                dataType: "json",
                data: {
                    areaId: $area.val()
                },
                success: function(data) {
                    $('#affectSites').html(data.totalRecords);
                },
                error: function(err) {
                    //window.alert('Failed to get the sites data.');
                    console.error('获取区域数据失败。');
                }
            });
        }
    });

    window.page = new Page();
});