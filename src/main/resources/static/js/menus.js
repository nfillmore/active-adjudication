var menus = {
    data:[
        {
            title:'My Labs',
            href:'/task/list.html'
        },
        {
            title:'All Labs',
            href:'/task/list4All.html'
        },
        {
            title:'New Task',
            href:'/task/new.html'
        },
        {
            title:'Task Consensus',
            href:'/task/consensus.html'
        },
        {
            title:'Users Manager',
            href:'/users/list.html'
        },
    ],

    init: function(){
        var self = this;

        var html = '';
        $.each(self.data,function(index, item){
            html += '<li class="nav-item">';
            var active = window.location.href.indexOf(item.href)>-1;
            html += '<a class="nav-link '+(active?'active':'')+'" href="'+(active?'#':item.href)+'">'+item.title+(active?'<span class="sr-only">(current)</span>':'')+'</a>';
            html += '</li>';

        })
        $('#menus').html(html);
    }
}