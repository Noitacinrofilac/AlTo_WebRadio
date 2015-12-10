/**
 * Created by hadrien on 16/06/14.
 */
deezer_autocompletion = function(query, tbId) {
    DZ.api('/search/autocomplete?q='+query, function(response){
        //Get artist
        if(tbId == 0) {
            $("#datal_artist option").remove();
            for (var i = 0; i < response.artists.data.length; i++) {
                $("#datal_artist").append(new Option(response.artists.data[i].name, response.artists.data[i].name));
            }
        }
        // Get song title
        else {
            $("#datal_title option").remove();
            for (var i = 0; i < response.tracks.data.length; i++) {
                if((response.tracks.data[i].artist.name)===($("#tb_artist").val())){
                    $("#datal_title").append(new Option(response.tracks.data[i].title, response.tracks.data[i].title));
                }
            }
        }
    });
}
deezer_getImagesOldSongs = function(){
    $("#timeline figure").remove();
    getImagesOldSongs(list_cache.length-1);
}
getImagesOldSongs =function(imgId){
    if( imgId< list_cache.length){
        DZ.api('/search/artist?q='+list_cache[imgId][0], function(response){
            $("#timeline").append(
                '<figure id="current">'+
                    '<img id="current" src="'+response.data[0].picture+'"/>'+
                    '<figcaption id="artist">'+list_cache[imgId][0]+'</figcaption>'+
                    '<figcaption id="title">'+list_cache[imgId][1]+'</figcaption>'+
                '</figure>');
            //getImagesOldSongs(imgId+1);
        });
    }
}
deezer_getImagesNextSongs = function(){
    $("#list_next figure").remove();
    getImagesNextSongs(0);
}
getImagesNextSongs =function(imgId){
    if( imgId< list_selected.length && imgId < 5){
        DZ.api('/search/artist?q='+list_selected[imgId][0], function(response){
            $("#list_next").append(
                    '<figure id='+imgId+' class="figureNext" onClick="selectSong(\''+list_selected[imgId][0]+'\',\''+list_selected[imgId][1]+'\',\''+list_selected[imgId][2]+'\')" title="'+list_selected[imgId][2]+'">'+
                        '<input class="figure_img_button" type="image" src="'+response.data[0].picture+'"/>'+
                        '<figcaption id="artist">'+list_selected[imgId][0]+'</figcaption>'+
                        '<figcaption id="title">'+list_selected[imgId][1]+'</figcaption>'+
                    '</figure>');
            getImagesNextSongs(imgId+1);
        });

    }
}