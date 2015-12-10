/**
 * Created by hadrien on 11/06/14.
 */

var list_cache = Array();
var list_nextSongs = Array();
var list_selected = Array();

$(document).ready(function() {
/*
    $(".navbar").append(
        '<div class="alert alert-warning fade in">'+
            '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+
            '<p>Don\'t forget to empty your current playlist to have the best experience with the Webradio</p>'+
        '</div>');
*/
    $("#tb_artist").keyup(function(){
        deezer_autocompletion($(this).val(),0);
    });
    $("#tb_title").keyup(function(){
        deezer_autocompletion($(this).val(),1);
    });

    $("#play").click(function () {
        core_playSong($("#tb_artist").val(),$("#tb_title").val(),true);
        deezer_getImagesOldSongs();
    });

    $("#next").click(function () {
        core_nextSong();
    });

    $("#like").click(function () {
        $(".navbar").append(
                '<div class="alert alert-success fade in">'+
                    '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+
                    '<p>You just liked '+currentSong[1]+' by '+currentSong[0]+', enjoy!</p>'+
                '</div>');
        //ws_rateSong(currentSong[0],currentSong[1],1);
        $("#like").attr('disabled','disabled');
    });

    $("#dislike").click(function () {
        $(".navbar").append(
                '<div class="alert alert-success fade in">'+
                '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+
                '<p>You don\'t like '+currentSong[1]+' by '+currentSong[0]+', we\'re playing the next one ;)</p>'+
                '</div>');
        //ws_rateSong(currentSong[0],currentSong[1],0);
        $("#like").attr('disabled','disabled');
        $("#dislike").attr('disabled','disabled');
        core_nextSong();
    });

    $("#slider").change(function(){
        updateSliderText()
    });

});
selectSong = function(artist,title,value){
    console.log("val = "+value + " sliderval = "+ $("#slider").val());
    $("#slider").val(value);
    console.log("val = "+value + " sliderval = "+ $("#slider").val());
    updateSliderText();
    core_addToList(artist,title);
}

updateSliderText = function(){
    //$("#sliderValue").val($("#slider").val());
    if($("#slider").val() < 50){
        $("#sliderText").val("Rare");
    }
    if($("#slider").val() >= 50 && $("#slider").val() <=100){
        $("#sliderText").val("Commons");
    }
    if($("#slider").val() > 100 && $("#slider").val() <= 150){
        $("#sliderText").val("Classics");
    }
    if($("#slider").val() > 150){
        $("#sliderText").val("Legends");
    }
    updateListNewSong();
}

updateListNewSong =  function() {
    //var str = "<ul>";
    var like = $("#slider").val();
    list_selected = Array();
    for (var i in list_nextSongs) {
        if(list_nextSongs[i][2] < like && list_nextSongs[i][2] > like-40) {
            list_selected.push(list_nextSongs[i]);
        }
    }
    /*
    for (var i in list_selected) {
        str += '<li>' + list_selected[i] + '</li>';
    }
    str += '</ul>';
    $('#newSong_list').empty();
    $('#newSong_list').append(str);*/
    currentSong=list_selected[0];
    deezer_getImagesNextSongs();
}
