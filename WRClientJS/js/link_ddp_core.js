/**
 * Created by hadrien on 05/06/14.
 */

getParamsFromURL = function (url) {
    var parms = {}, pieces, parts, i;
    var hash = url.lastIndexOf("#");
    if (hash !== -1) {
        // remove hash value
        url = url.slice(0, hash);
    }
    var question = url.lastIndexOf("?");
    if (question !== -1) {
        url = url.slice(question + 1);
        pieces = url.split("&");
        for (i = 0; i < pieces.length; i++) {
            parts = pieces[i].split("=");
            if (parts.length < 2) {
                parts.push("");
            }
            parms[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
        }
    }
    return parms;
};

var appID = "40108d109e3dc342e550a29fwebradio";
var appSecret = "533001922d68736923d85530webradio";
params = getParamsFromURL(window.location.href);
var userID = params.user_id;
var currentSong = Array();
var currentSongFromAlTo = new Array();
var firstTimePlayed = true;
var lastTimeInPlayerStopped = false;
console.log("params = ",params);
/******************************Connection***************************************************/
var ddp = new MeteorDdp(params.url);
var connection = ddp.connect();
connection.done(function() {
    console.log('Connected!');


    /***************************************** Subscribe ************************************************/
    ddp.subscribe("plugins",[appID,appSecret]);             // called only the first time
    ddp.subscribe("pluginUser",[appID,userID]).done(function(){
        console.log("subscribed");
    });

    /*****************************************check changes on collections ******************************/
    ddp.watch('users', function(changedDoc, message) {
        /*
         chdc currentPlaylist
         chdc currentTrack
         chdc playlists
         chdc _id
        */
        if (message === "added") {
            console.log("Webradio || Adding in the user collection "+changedDoc);
        }
        if (message === "changed") {
            console.log("Webradio || Change in the user collection "+changedDoc);
            console.log("chdc",changedDoc);
            if( firstTimePlayed){
                console.log("DDP || First time play : "+currentSong[0]+" - "+currentSong[1]);
                firstTimePlayed = false;
                lastTimeInPlayerStopped = false;
                if(currentSong){
                    //core_playSong(currentSong[0],currentSong[1],true);
                    //ws_currentSong(currentSong[0],currentSong[1]);
                }
                else{
                    console.log("NOPEE currentSong = "+currentSong);
                }
            }
            else if(changedDoc.player.track != null && $.inArray(changedDoc.player.track.id,currentSongFromAlTo) ){
                currentSongFromAlTo.push(changedDoc.player.track.id);
                lastTimeInPlayerStopped= false;
                console.log("Changedoc || Current track has changed for "+changedDoc.player.track.title);
                console.log("DDP || Next song to play : "+currentSong[0]+" - "+currentSong[1]);
                deezer_getImagesOldSongs();
                $("#like").removeAttr('disabled');
                $("#dislike").removeAttr('disabled')
                if(currentSong){
                    ws_currentSong(currentSong[0],currentSong[1]);
                }
                else{
                    console.log("NOPEE currentSong = "+currentSong);
                }
            }
            else if(changedDoc.player.status=="stopped" && (changedDoc.player.track ==null || $.inArray(changedDoc.player.track.id,currentSongFromAlTo)) && !lastTimeInPlayerStopped){
                console.log("player was stopped");
                lastTimeInPlayerStopped = true;
                core_nextSong();
            }
            else{
                lastTimeInPlayerStopped = false;
                console.log("STOPPED but same song ",changedDoc);
            }
        }
        if (message === "removed") {
            console.log("Webradio || Something as been removed of the user collection "+changedDoc);
        }
    });


    /*****************************play song**************************************/
    core_playSong = function(artist,title,playNow){
        currentSong = [artist, title];
        list_cache.push(currentSong);
        console.log("song pushed in core play song")
        var songPlayed = ddp.call("plugin_playSong",[appID,userID,title.toLowerCase(),artist.toLowerCase(),playNow]);
        songPlayed.fail(function(result){
            console.log("ATTENTION : "+result);
        });
    };

    core_nextSong = function(){
        var startingPoint = 0;
        var sameArtistAllowed = false;
        _nextSong(startingPoint,sameArtistAllowed);

    };
    _nextSong = function(track,sameArtistAllowed){

        var alreadyPlayed = false;

        //var artistAlreadyPlayed = false;

        //If no song available in selected take all of them
        if(list_selected.length == 0){
            for(var i in list_nextSongs) {
                list_selected.push(list_nextSongs[i]);
            }
        }

        for(var i =0 ;i<list_cache.length; i++){
            console.log("equality  "+list_cache[i][0]+" = "+list_selected[track][0]);
            console.log("equality  "+list_cache[i][1]+" = "+list_selected[track][1]);
            console.log("So : "+(list_cache[i][0] == list_selected[track][0] && list_cache[i][1] == list_selected[track][1]));
            if( (list_cache[i][0] == list_selected[track][0] && list_cache[i][1] == list_selected[track][1]) && !alreadyPlayed){
                console.log(list_selected[track][1]+"is already played");
                alreadyPlayed = true;
            }
            /*
            if(list_cache[i][0] == list_selected[track][0] ){
                artistAlreadyPlayed = true;
            }*/
        }

        //if(!alreadyPlayed && (!artistAlreadyPlayed || sameArtistAllowed)){//$.inArray( list_nextSongs[track], list_cache)==-1){
        if(!alreadyPlayed){
            console.log("Playing next song with : "+[appID,userID, list_selected[track][1], list_selected[track][0],true]);
            currentSong=[list_selected[track][0],list_selected[track][1]];
            console.log("song pushed in core next song");
            list_cache.push(currentSong);
            var newSongPlayed = ddp.call("plugin_playSong",[appID,userID, list_selected[track][1], list_selected[track][0],true]);
            newSongPlayed.fail(function(result){
                console.log("ATTENTION : "+result);
                //return _nextSong(track+1,true);
            });
            return;
        }else{
            if(track < list_selected.length-1){
                console.log(list_selected[track][1]+" not available so we try next "+track);
                var nxtTr = track+1;
                _nextSong(nxtTr,false);
            }
            else{
                console.log("No song found");
                //_nextSong(0,true);
            }
        }
    };

    core_addToList = function (artist,title) {
        console.log("song added to the playlist :");
        /*
        $(".navbar").append(
            '<div class="alert alert-success fade in">'+
                '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+
                '<p>The song '+title+' by '+artist+' has been added to the webradio list and will be played soon ;=)</p>'+
            '</div>');
            */
        core_playSong(artist,title,false);
    };
});