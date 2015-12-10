/**
 * Created by hadrien on 10/06/14.
 */
var add_server = "http://127.0.0.1:8080/webradio/";
//var add_server = "http://ec2-54-72-148-114.eu-west-1.compute.amazonaws.com:8080/webradio/";

ws_playlist = function(userID,listPlaylist){
    //*
    $.ajax({
        cache: false,
        type: "POST",
        async: false,
        crossDomain: true,
        url: add_server+"/webradio/playlist/",
        data: JSON.stringify(listPlaylist),
        //contentType: "application/json",
        dataType: "json",
        success: function (val) {
            console.log("Success "+val);
        },
        error: function (xhr) {
            console.log("/!\\ Error : "+xhr.responseText+xhr);
        }
    });//*/
    //$.post('http://192.168.43.50:8080/myapp/WR', JSON.stringify(listPlaylist), function(d){console.log("ooooooooooooblubliblou"+d)})
}

/**
 * Send the current song to the server
 * Should receive a list of N next songs
 * @param artists
 * @param title
 */
ws_currentSong = function(artists,title) {
    console.log("SENDING TO SERVER ",artists,title);
    $.ajax({
        url: add_server+ artists+"_"+title
    }).then(function (data) {
        if(data[0].artist == "-1"){
            $(".navbar").append(
                '<div class="alert alert-danger fade in">'+
                    '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+
                    '<p>Song '+artists+' by '+title+' not found in the db :-(</p>'+
                '</div>');
        }
        else {
            console.log("get reply from the server");

            list_nextSongs = [];
            for (var i = 0; i < data.length; i++) {
                list_nextSongs.push([data[i].artist, data[i].title,data[i].likes]);
            }
            list_nextSongs.sort(function(a, b){return b[2]- a[2]});
            updateListNewSong();
            return true;
        }
    });
}

ws_rateSong = function(artist,title,value){
    /*
    $.ajax({
        url: add_server+"/webradio/rate/" + currentArtists + "<>" + currentTitle + "<>"+value
    }).then(function (data) {
        $('#serverText').append(data);


        console.log(data);
    });*/
    $.ajax({
        cache: false,
        type: "POST",
        async: false,
        crossDomain: true,
        url: add_server+"/webradio/rate/",
        data: JSON.stringify(listPlaylist),
        //contentType: "application/json",
        dataType: "json",
        success: function (val) {
            console.log("Success "+val);
        },
        error: function (xhr) {
            console.log("/!\\ Error : "+xhr.responseText+xhr);
        }
    });
}