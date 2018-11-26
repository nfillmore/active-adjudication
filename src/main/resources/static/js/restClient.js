var restClient = {
    post: function(url, params, callback){
       $.ajax({
            data: params,
            url:url,
            method:'POST',
            error: function(jqXHR, status, errorThrown){
                console.log(jqXHR);
                console.log(errorThrown);
                if(jqXHR.responseJSON && jqXHR.responseJSON.message){
                    alert(jqXHR.responseJSON.message)
                }else{
                    alert(errorThrown);
                }
            },
            success: callback
       });
    },
    postJson: function(url, params, callback){
           $.ajax({
                contentType:'application/json;charset=UTF-8',
                data: JSON.stringify(params),
                url:url,
                method:'POST',
                error: function(jqXHR, status, errorThrown){
                    console.log(jqXHR);
                    console.log(errorThrown);
                    if(jqXHR.responseJSON && jqXHR.responseJSON.message){
                        alert(jqXHR.responseJSON.message)
                    }else{
                        alert(errorThrown);
                    }
                },
                success: callback
           });
        },
    get: function(url, params, callback){
       $.ajax({
            data: params,
            url:url,
            method:'GET',
            error: function(jqXHR, status, errorThrown){
                console.log(jqXHR);
                console.log(errorThrown);
                if(jqXHR.responseJSON && jqXHR.responseJSON.message){
                    alert(jqXHR.responseJSON.message)
                }else{
                    alert(errorThrown);
                }
            },
            success: callback
       });
    }
};