/******Variables de connexion ******/
getParamsFromURL = function (url) {
        var parms = {}, pieces, parts, i;
        var hash = url.lastIndexOf("#");
        if (hash !== -1)
            { // remove hash value
              url = url.slice(0, hash);
            }
         var question = url.lastIndexOf("?");
         if (question !== -1) {
                url = url.slice(question + 1); pieces = url.split("&");
             for (i = 0; i < pieces.length; i++) {
                parts = pieces[i].split("=");
             if (parts.length < 2) {
                parts.push("");
                 }
             parms[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
                }
             }
             return parms;

   }
var param=getParamsFromURL(window.location.href);
var userID = param.user_id;//getuserID
var urlConn=param.url;
var appID = "46d4e72300b2926ef1db7146BT";
var appSecret = "9693c774cd249118203372afBT";

var ddp = new MeteorDdp('ws://alto.polytech.meteor.com/websocket');
var connection = ddp.connect();

/******Connexion au coeur ******/

connection.done(function() {
    console.log('Connected!');
   // ddp.subscribe("plugins",[appID,appSecret]);
    var notif = ddp.subscribe("plugins",[appID,appSecret]);
    var notif2 =	ddp.subscribe("pluginUser",[appID,userID]);
    notif.done(function(){
		    console.log('Subscribed!');

   });
   notif2.done(function(){
		    console.log('Subscribed!');

   });
   });




/******Connexion au coeur ******/
   
var titre="";
var artiste="";
var stream_url="";
var points=0;

var playlist;
var songN=0;
var fieldset;


var liked=new Array();

/****** Foction vérifiant qu'une chanson n'est pas dans la liste des chansons aimées.
Si ce n'est pa le cas elle l'ajoute.******/
function likedSongs(song){
	if (!alreadyLiked){
		 liked.push(song);

	}
	alreadyLiked=true;
}
/*****Variable qui permet d'empecher d'ajouter plusieurs fois la meme chanson aux chansons aimées.*****/
var alreadyLiked=false;

window.addEventListener("load",init,false);
loadEnd();

//var all_playlists=ddp.call("plugin_getPlaylists",[appID,userID]);
//var playlist=all_playlist[0].tracks;

function track(titre ,stream_url) {
    this.titre = titre;
    this.stream_url=stream_url;

}



/******Fonction permettant de lire les informations sur les playlists des utilisateurs*****/


ddp.watch('users', function(changedDoc, message) {
        /*
         chdc currentPlaylist
         chdc currentTrack
         chdc playlists
         chdc _id
        */
		var user_playlists ;

		user_playlists = changedDoc.playlists;
		 var k=user_playlists.length;
		if (user_playlists.length>1 && songN==0) {
			
			 var n=Math.floor((Math.random() * k) + 1); 
			playlist=user_playlists[n];//alert("done");
			//sendByFacebook();

		}
	
});


/******Affiche l'interface de base sur la page ******/

var btnStart;
var btnReStart;
function init(){
	btnStart = document.createElement("button");
	btnStart.textContent = "Commencer à jouer";
	btnStart.className = "btn btn btn-primary";
	document.body.appendChild(btnStart);


    	btnStart.onclick=function(){

		console.log('Playing!');
	    //ddp.call("plugin_playSong",[appID,userID,"Buffalo Soldier","Bob Marley",true]);
	    artiste=playlist.tracks[songN].track.author;
	    titre=playlist.tracks[songN].track.title;
		stream_url=playlist.tracks[songN].track.url;
	    songN++;

		display();

        var playing= ddp.call("plugin_playSong",[appID,userID,titre,"",true]);
		playing.done(function(){
		    console.log('Playing2!');

		});
		liked=[];
		
		clearInterval(timer);
		IndiquerMinutes(0.25); 
		DemarrerChrono();
		document.body.removeChild(btnStart);


	}
}
/******Affiche l'interface de base sur la page ******/


var input;
function display(){
	if(fieldset){
			document.body.removeChild(fieldset);
		}
	fieldset = document.createElement("fieldset");
		
	var legend = document.createElement("legend");
	legend.textContent = "Ecoutez attentivement";
	fieldset.appendChild(legend);
	
	var label = document.createElement("label");
	label.textContent = "Quel est l'Artiste/le Titre que vous entendez ? (1 seul mot par Réponse)";
	 input = document.createElement("input");
	input.type="text";
	input.style.display="block"
	input.style.margin= 0;
	//input.style.width="100%";
	input.style.fontFamily="sans-serif";
	input.style.fontSize="18px";
	input.style.appearance="none";
	input.style.boxShadow="none";
	label.appendChild(input);
	fieldset.appendChild(label);
	
	var btnEnvoyer = document.createElement("button");
	btnEnvoyer.textContent = "Envoyer";
	btnEnvoyer.className="btn btn btn-info";


	btnEnvoyer.onclick=function(){
        submit();

	}
	input.onkeypress=function(e){
	        if (e.keyCode==13) submit();
	}
	label.appendChild(btnEnvoyer);

	var btnLike = document.createElement("button");
	btnLike.textContent = "J'aime cette chanson!";
	btnLike.className="btn btn btn-success";

	btnLike.onclick=function(){
	var toAdd =new track(titre,stream_url);
		likedSongs(toAdd);


	
	}
		label.appendChild(btnLike);


	//document.body.appendChild(fieldset);	
	document.getElementById("div1").appendChild(fieldset);
}

/****** Vérifie la validité d'une réponse ******/ 
function submit(){
		var result=input.value;
		input.value='';
		label = document.createElement("label");
		label.textContent = result;
		var result2=titre.split(' ');
		var semaphore=false;
		/*******Loop which check if the answer given by the user is in the title ******/
		for (var i in result2){
			if (!semaphore){
				if (stringClose(result2[i],result) && result.length>2 ){
					console.log("OHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH3");
					$("#result" ).empty();
					$("#result").append( '<div class="alert alert-success fade in">'+ '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+ '<p>Bonne réponse  ;)</p>'+ '</div>');
					points+=secondes+1;
					semaphore=true;

					}
				
				}
			}
			if (!semaphore){
						console.log("OHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH4444");
						$("#result" ).empty();

						$("#result").append( '<div class="alert alert-danger fade in">'+ '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>'+ '<p>Dommage!La bonne réponse était ' +result2 +'  </p>'+ '</div>');
						}

		next();
		}
		
function next(){


	
	if (songN==playlist.tracks.length){
		ArreterChrono();
		redirectionFin();
	}
	else{
	  	artiste=playlist.tracks[songN].track.author;
	    titre=playlist.tracks[songN].track.title;

    	//stream_url=playlist[songN].stream_url;
    	alreadyLiked=false;
	    ddp.call("plugin_playSong",[appID,userID,titre,"",true]);
		songN++;
		clearInterval(timer);IndiquerMinutes(0.25); DemarrerChrono();
	}

	//appel de la chanson suivante
	//var song =ddp.call('getCurrentPlayedSong',[('blindtest','456')]);

	
}


function endOfGame(){
	alert("votre score est de "+points);
	points=0;
}

/*Calcule la distance de Levenshtein entre 2 strings*/
function distanceLevenshtein( str1, str2){
	
	
		var d = []; 
		var s1=str1.toLowerCase(); 
		var s2=str2.toLowerCase(); 
		
		var l1=s1.length;
		var l2=s2.length;
		var i, j, k;
		
		for (i=0;i<l1;i++){
			d[i]=[];
			d[i][0]=i;
		
		}
		for (i=0;i<l2;i++){
			d[0][i]=i;
		
		}
		for (i=1;i<l1;i++){
			for (j=1;j<l2;j++){
				if (s1.charAt(i)==s2.charAt(j)) k=0;
				else k=1;
				
				d[i][j]=Math.min(d[i-1][ j] + 1, d[i][  j-1] + 1,d[i-1][j-1] + k );
			}

		}	
		   return d[l1-1][l2-1];
}
/*Vérifie la proximité de 2 strings*/
function stringClose(s1,s2){
	if (distanceLevenshtein(s1,s2)<2) return true;
	return false;
}

/****** Affiche l'écran final******/
function redirectionFin(){

			document.getElementById("div1").removeChild(fieldset);


			document.getElementById("chrono").innerHTML='';
			document.getElementById("div1").innerHTML="Votre score est de "+points+" points";


			ArreterChrono();
			loadEnd();


}

function getCookie(sName) {
        var cookContent = document.cookie, cookEnd, i, j;
        var sName = sName + "=";
 
        for (i=0, c=cookContent.length; i<c; i++) {
                j = i + sName.length;
                if (cookContent.substring(i, j) == sName) {
                        cookEnd = cookContent.indexOf(";", j);
                        if (cookEnd == -1) {
                                cookEnd = cookContent.length;
                        }
                        return decodeURIComponent(cookContent.substring(j, cookEnd));
                }
        }       
        return null;
}

function setCookie(sName, sValue) {
        var today = new Date(), expires = new Date();
        expires.setTime(today.getTime() + (24*60*60*1000));
        document.cookie = sName + "=" + encodeURIComponent(sValue) + ";expires=" + expires.toGMTString();
}


function sendByFacebook(){
	var toSend="";

	for (var i=0;i<playlist.tracks.length;i++){
		
	toSend=toSend.concat(playlist.tracks[i].track.title+"|");

	//alert(toSend);
	}
		alert(toSend);

	//document.getElementById(id).data-href= "http://alto.polytech.meteor.com/plugin/BT/asPgCxMgChu4tz6cD?"+toSend; 
	$("#result").append( '<div class="fb-send"  data-href="http://alto.polytech.meteor.com/plugin/BT/asPgCxMgChu4tz6cD?'+toSend+' data-colorscheme="light"></div>');
}