var header = {
    init: function(){
        $('body').prepend($('<header>'
                                 +' <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">'
                                 +' <a class="navbar-brand" href="#">Dashboard</a>'
                                 + '<div style="flex:1"></div>'
                                 +  '<ul class="navbar-nav px-3">'
                                        +  ' <li class="nav-item text-nowrap">'
                                           +  '<a class="nav-link" href="/logout">Sign out</a>'
                                         +  '</li>'
                                       +  '</ul>'
                                 +' </nav>'
                             +' </header>'));
    }
}